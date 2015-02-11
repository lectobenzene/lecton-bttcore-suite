package com.tcs.mobility.btt.createopxml.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.ide.IDE;

import com.tcs.mobility.btt.core.source.files.FileModify;
import com.tcs.mobility.btt.core.source.files.xml.SkeletonService;
import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;
import com.tcs.mobility.btt.core.utils.resources.WSConsole;
import com.tcs.mobility.btt.createopxml.wizards.pages.ConfigurationWizardPage;
import com.tcs.mobility.btt.createopxml.wizards.pages.PrimaryWizardPage;

/**
 * Wizard to create a Web Service (op.xml or MIB.xml)
 * 
 * @author Saravana
 *
 */
public class NewOpFileWizard extends Wizard implements INewWizard {

	private static final String DSE_JAR_PATH = "dseJarPath";
	private static final String DSE_INI_LOCATION = "dseIniLocation";
	private static final String CONTEXT_NAME = "contextName";
	private static final String CONTEXT_LOCATION = "contextLocation";
	private static final String PROCESSOR_PACKAGE_NAME = "processorPackageName";
	private static final String PROCESSOR_LOCATION = "processorLocation";
	private static final String PROCESSOR_NAME = "processorName";
	private static final String SERVICE_DESCRIPTION = "serviceDescription";
	private static final String SERVICE_LOCATION = "serviceLocation";
	private static final String SERVICE_NAME = "serviceName";
	private static final String CONTEXT_PACKAGE_NAME = "contextPackageName";

	private boolean isWarProject;

	private IStructuredSelection selection;
	private PrimaryWizardPage primaryPage;
	private ConfigurationWizardPage configPage;

	MessageConsole console;
	MessageConsoleStream out;

	@Override
	public void addPages() {
		primaryPage = new PrimaryWizardPage(selection);
		addPage(primaryPage);
		configPage = new ConfigurationWizardPage();
		addPage(configPage);
	}

	public NewOpFileWizard() {
		super();
		setWindowTitle("New Op File");
		setNeedsProgressMonitor(true);
		console = WSConsole.findDefaultConsole();
		out = console.newMessageStream();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean canFinish() {
		return (getContainer().getCurrentPage() == configPage && configPage.isPageComplete());
	}

	@Override
	public boolean performFinish() {

		final HashMap<String, String> details = new HashMap<String, String>();
		details.put(SERVICE_NAME, primaryPage.getServiceName());
		details.put(SERVICE_LOCATION, primaryPage.getServiceLocation());
		details.put(SERVICE_DESCRIPTION, primaryPage.getServiceDescription());
		details.put(PROCESSOR_NAME, configPage.getProcessorName());
		details.put(PROCESSOR_LOCATION, configPage.getProcessorFullLocation());
		details.put(PROCESSOR_PACKAGE_NAME, configPage.getProcessorPackage());
		details.put(CONTEXT_NAME, configPage.getContextName());
		details.put(CONTEXT_LOCATION, configPage.getContextFullLocation());
		details.put(CONTEXT_PACKAGE_NAME, configPage.getContextPackage());
		isWarProject = primaryPage.isWarProject();

		details.put(DSE_INI_LOCATION, primaryPage.getDseIniLocation());
		if (!isWarProject) {
			details.put(DSE_JAR_PATH, primaryPage.getJarPath());
		}

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(details, monitor);
				} catch (CoreException e) {
					out.println(e.getMessage());
					out.println(e.toString());
					e.printStackTrace();
				} finally {
					monitor.done();
				}
			}
		};

		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			out.println("Exception - " + realException.getMessage());
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Helper method which actually does the dirty work
	 * 
	 * @param details
	 *            A map of all the inputs
	 * @param monitor
	 *            To register the progress
	 * @throws CoreException
	 */
	protected void doFinish(HashMap<String, String> details, IProgressMonitor monitor)
			throws CoreException {

		monitor.beginTask("Creating " + details.get(SERVICE_NAME), 5);

		final IFile serviceFile = obtainFile(details.get(SERVICE_NAME) + ".xml",
				details.get(SERVICE_LOCATION));

		SkeletonService skeleton = new SkeletonService();
		skeleton.setServiceName(details.get(SERVICE_NAME));
		skeleton.setServiceDescription(details.get(SERVICE_DESCRIPTION));
		skeleton.setProcessorName(details.get(PROCESSOR_NAME));
		skeleton.setProcessorPackageName(details.get(PROCESSOR_PACKAGE_NAME));
		skeleton.setContextName(details.get(CONTEXT_NAME));
		skeleton.setContextPackageName(details.get(CONTEXT_PACKAGE_NAME));
		String serviceFileContent = skeleton.createSkeletonService();

		try {
			InputStream inputStream = new ByteArrayInputStream(serviceFileContent.getBytes());
			serviceFile.create(inputStream, true, monitor);
			inputStream.close();
			monitor.worked(1);

			monitor.setTaskName("Creating Processor Class");
			IFile processorJavaFile = obtainFile(details.get(PROCESSOR_NAME) + ".java",
					details.get(PROCESSOR_LOCATION));
			inputStream = new ByteArrayInputStream(skeleton.getJavaContent(
					details.get(PROCESSOR_PACKAGE_NAME),
					"com.fortis.be.commons.operations.CommonsServerOperationProcessor",
					details.get(PROCESSOR_NAME), "CommonsServerOperationProcessor"));
			processorJavaFile.create(inputStream, true, monitor);
			inputStream.close();
			monitor.worked(1);

			monitor.setTaskName("Creating Context Class");
			IFile contextJavaFile = obtainFile(details.get(CONTEXT_NAME) + ".java",
					details.get(CONTEXT_LOCATION));
			inputStream = new ByteArrayInputStream(skeleton.getJavaContent(
					details.get(CONTEXT_PACKAGE_NAME), "com.ibm.btt.base.LocalContextImpl",
					details.get(CONTEXT_NAME), "LocalContextImpl"));
			contextJavaFile.create(inputStream, true, monitor);
			inputStream.close();
			monitor.worked(1);

		} catch (IOException e) {
			out.println(e.getMessage());
			out.println(e.toString());
			e.printStackTrace();
		}

		monitor.setTaskName("Updating dse.ini file");

		String path;
		String value;
		if (isWarProject) {
			path = ResourcesUtil.getSplitAfterPath(details.get(SERVICE_LOCATION), "server")
					.replace("\\", "/");
			value = (details.get(SERVICE_NAME) + ".xml").replace("\\", "/");
		} else {
			path = "%" + details.get(DSE_JAR_PATH) + "%";
			value = (ResourcesUtil.getSplitBeforePath(details.get(SERVICE_LOCATION), "server")
					+ File.separator + (details.get(SERVICE_NAME) + ".xml")).replace("\\", "/");
		}

		String entry = skeleton.createDseIniWebServiceEntry(details.get(SERVICE_NAME), value, path);

		// If dse.ini file is read-only, then convert it into non-read-only
		IResource dseIniResource = ResourcesUtil.getResource(details.get(DSE_INI_LOCATION));
		ResourceAttributes attrib = new ResourceAttributes();
		attrib.setReadOnly(false);
		dseIniResource.setResourceAttributes(attrib);

		File dseIniFile = dseIniResource.getLocation().toFile();
		String dseFileContent = (new FileModify()).updateDseIniFile(dseIniFile, entry);
		IFile file = obtainFile(details.get(DSE_INI_LOCATION));

		try {
			InputStream inputStream = new ByteArrayInputStream(dseFileContent.getBytes());
			file.setContents(inputStream, true, true, monitor);
			inputStream.close();
		} catch (IOException e) {
			out.println(e.getMessage());
			out.println(e.toString());
			e.printStackTrace();
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage();
				try {
					IDE.openEditor(page, serviceFile, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * Get a file from the folder
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param location
	 *            the location of the folder
	 * @return the returned file
	 */
	private IFile obtainFile(String fileName, String location) {
		IContainer container = (IContainer) ResourcesUtil.getResource(location);
		return container.getFile(new Path(fileName));
	}

	/**
	 * Get the file directly from the given path
	 * 
	 * @param fullLocation
	 *            the full location of the file relative to the workspace
	 * @return the returned file
	 */
	private IFile obtainFile(String fullLocation) {
		IFile file = (IFile) ResourcesUtil.getResource(fullLocation);
		System.out.println(ResourcesUtil.getResource(fullLocation).getClass());
		return file;
	}
}
