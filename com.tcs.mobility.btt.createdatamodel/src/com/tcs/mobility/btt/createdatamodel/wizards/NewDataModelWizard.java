package com.tcs.mobility.btt.createdatamodel.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.tcs.mobility.btt.core.source.files.javacreator.context.ContextJavaCreator;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.parent.DataElementModel;
import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;
import com.tcs.mobility.btt.core.utils.resources.WSConsole;
import com.tcs.mobility.btt.createdatamodel.wizards.pages.PrimaryInputWizardPage;
import com.tcs.mobility.btt.createdatamodel.wizards.pages.XMLInputWizardPage;

public class NewDataModelWizard extends Wizard implements INewWizard {

	PrimaryInputWizardPage primaryInputWizardPage;
	XMLInputWizardPage xmlInputWizardPage;

	private MessageConsole console;
	private MessageConsoleStream out;
	private IStructuredSelection selection;
	private IWorkbenchSite site;

	private ArrayList<ICompilationUnit> javaUnitsCreated;

	public NewDataModelWizard() {
		super();
		setWindowTitle("Create Data Model");
		setNeedsProgressMonitor(true);
		console = WSConsole.findDefaultConsole();
		out = console.newMessageStream();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.site = workbench.getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
	}

	@Override
	public boolean performFinish() {
		final String pkgPath = primaryInputWizardPage.getTxtResponseLocationText();
		final String className = primaryInputWizardPage.getTxtServiceNameText();
		final KeyedCollectionModel requestKColl = xmlInputWizardPage.getRequestKColl();
		final KeyedCollectionModel responseKColl = xmlInputWizardPage.getResponseKColl();

		javaUnitsCreated = new ArrayList<ICompilationUnit>();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(className, pkgPath, requestKColl, responseKColl, monitor);
				} catch (MalformedTreeException e) {
					e.printStackTrace();
					out.println(e.getMessage());
					out.println(e.toString());
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
			e.printStackTrace();
			out.println("Exception - " + realException.getMessage());
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}

		return true;
	}

	protected void doFinish(String serviceName, String responseLocation,
			KeyedCollectionModel requestKColl, KeyedCollectionModel responseKColl,
			IProgressMonitor monitor) {
		
		monitor.beginTask("Setting up the preliminaries", 7);
		
		IResource pkgResource = ResourcesUtil.getResource(responseLocation);
		IFolder responseFolder = pkgResource.getProject().getFolder(
				(new Path(responseLocation).removeFirstSegments(1)));


		/*
		 * Some projects contains 'commons' while others contains 'common'. This
		 * first checks for 'commons' and if not found proceeds with 'common'
		 */
		Path commonsPath = new Path("commons");
		if (!responseFolder.getFolder(commonsPath).exists()) {
			System.out.println("Commons package does not exist. Going ahead with common");
			commonsPath = new Path("common");

		}
		Path modulePath = new Path("module");
		Path inFolderPath = new Path(serviceName + File.separator + "in");
		Path outFolderPath = new Path(serviceName + File.separator + "out");

		monitor.worked(1);

		try {
			monitor.setTaskName("Creating Common classes");
			// Create the package for Common's' if it doesn't exist
			final IFolder commonsFolder = ResourcesUtil.createFolders(responseFolder, commonsPath,
					monitor);
			/*
			 * Check the existence of Java Class for all the KColls present in
			 * the parent node. If present, will compare for equality. If equal,
			 * no extra java class is created, otherwise, java class is created
			 * with a different name
			 */
			checkForJavaClassExistence(requestKColl, commonsFolder, responseFolder, serviceName,
					monitor);
			checkForJavaClassExistence(responseKColl, commonsFolder, responseFolder, serviceName,
					monitor);
			monitor.worked(1);
			monitor.setTaskName("Creating Request Java Class");
			
			// Creates the package for the Request
			final IFolder inFolder = ResourcesUtil.createFolders(responseFolder, inFolderPath,
					monitor);
			// Creates the java class for the Request
			createJavaClass(inFolder, responseFolder, requestKColl, serviceName, serviceName
					+ "Request.java", "in", monitor);

			monitor.worked(1);
			monitor.setTaskName("Creating Response Java Class");
			// Creates the package for the Response
			final IFolder outFolder = ResourcesUtil.createFolders(responseFolder, outFolderPath,
					monitor);
			// Creates the java class for the Response
			createJavaClass(outFolder, responseFolder, responseKColl, serviceName, serviceName
					+ ".java", "out", monitor);


			monitor.worked(1);
			monitor.setTaskName("Creating Module Java Class");
			
			// Creates the module package for the service
			final IFolder moduleFolder = ResourcesUtil.createFolders(responseFolder, modulePath,
					monitor);
			// Creates the java class for the Module
			createJavaClass(moduleFolder, responseFolder, null, serviceName, serviceName
					+ "Module.java", "module", monitor);


			monitor.worked(1);
			monitor.setTaskName("Registering in Response.java");
			
			IFile responseFile = responseFolder.getFile("Response.java");
			File originalFile = responseFile.getLocation().toFile();
			ContextJavaCreator creator = new ContextJavaCreator(null, null, serviceName);
			String modifiedResponseContents = creator.modifyResponseContents(originalFile);

			InputStream is = new ByteArrayInputStream(modifiedResponseContents.getBytes());
			responseFile.setContents(is, true, true, monitor);
			is.close();

			monitor.worked(1);
			monitor.setTaskName("Organizing imports...");
			/*
			 * The Java Class is just modified, but Imports has to be organised.
			 * Hence adding to the list
			 */
			ICompilationUnit responseJavaUnit = (ICompilationUnit) JavaCore.create(responseFile);
			javaUnitsCreated.add(responseJavaUnit);
			/*
			 * The Imports for the Java class created are Organized with the
			 * OrganizeImportsAction. This works perfectly if there are no
			 * ambiguity. If there is, then User Interaction will be required.
			 * The user will have to do this manually once the Wizard exits.
			 */
			Runnable job = new Runnable() {
				@Override
				public void run() {
					// Convertst the array list to an Array
					ICompilationUnit[] units = new ICompilationUnit[javaUnitsCreated.size()];
					units = javaUnitsCreated.toArray(units);
					OrganizeImportsAction org = new OrganizeImportsAction(site);
					org.runOnMultiple(units);
				}

			};
			getShell().getDisplay().syncExec(job);
		} catch (CoreException e) {
			out.println(e.getMessage());
			out.println(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			out.println(e.getMessage());
			out.println(e.toString());
			e.printStackTrace();
		}

		monitor.worked(1);
	}

	private void checkForJavaClassExistence(KeyedCollectionModel parent, IFolder commonsFolder,
			IFolder responseFolder, String serviceName, IProgressMonitor monitor)
			throws CoreException {
		final IPackageFragment commonsPkgFragment = (IPackageFragment) JavaCore
				.create(commonsFolder);

		List<DataElementModel> kCollChildren = parent.getChildren();

		for (DataElementModel child : kCollChildren) {
			if (child instanceof KeyedCollectionModel) {
				KeyedCollectionModel kCollChild = (KeyedCollectionModel) child;
				System.out.println("CheckForJavaClassExistence Called for - " + kCollChild.getId());
				checkForJavaClassExistence(kCollChild, commonsFolder, responseFolder, serviceName,
						monitor);
				checkForExistence(commonsPkgFragment, kCollChild, commonsFolder, responseFolder,
						serviceName, monitor);
			} else if (child instanceof IndexedCollectionModel) {
				IndexedCollectionModel iCollChild = (IndexedCollectionModel) child;
				DataElementModel childElement = (DataElementModel) iCollChild.getElement();
				if (childElement instanceof KeyedCollectionModel) {
					KeyedCollectionModel kCollChild = (KeyedCollectionModel) childElement;
					System.out.println("CheckForJavaClassExistence - ICOLL - Called for - "
							+ kCollChild.getId());
					checkForJavaClassExistence(kCollChild, commonsFolder, responseFolder,
							serviceName, monitor);
					checkForExistence(commonsPkgFragment, kCollChild, commonsFolder,
							responseFolder, serviceName, monitor);
				}
			}
		}
		// return;

		/*
		 * if (true) { createJavaClass(commonsFolder, responseFolder, rootKColl,
		 * serviceName, serviceName, "out", monitor); }
		 */

	}

	private void checkForExistence(final IPackageFragment commonsPkgFragment,
			KeyedCollectionModel kCollChild, IFolder commonsFolder, IFolder responseFolder,
			String serviceName, IProgressMonitor monitor) throws CoreException {
		String className = kCollChild.getDataModelAnnotationName();
		ICompilationUnit javaUnit = commonsPkgFragment.getCompilationUnit(className + ".java");
		System.out.println("Java Unit - " + javaUnit.getElementName());

		if (javaUnit.exists()) {
			List<DataElementModel> kCollChildKids = kCollChild.getChildren();
			Set<String> nodeNameSet = new HashSet<String>();
			for (DataElementModel element : kCollChildKids) {
				System.out.println("NODE children - " + element.getDataModelObjectName());
				nodeNameSet.add(element.getDataModelObjectName());
			}

			IType[] javaTypes = javaUnit.getTypes();
			Set<String> fieldNameSet = new HashSet<String>();
			for (IType javaType : javaTypes) {
				for (IAnnotation annotation : javaType.getAnnotations()) {
					System.out.println("Annotation =  " + annotation.getElementName());
					System.out.println("Annoation source = " + annotation.getSource());
					IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
					for (IMemberValuePair iMemberValuePair : memberValuePairs) {
						System.out.println("MemberValue = " + iMemberValuePair.getMemberName());
						System.out.println("MemberValue STR = " + iMemberValuePair.toString());
					}
				}
				IField[] fields = javaType.getFields();
				for (IField field : fields) {
					fieldNameSet.add(field.getElementName());
					System.out.println("Java Field - " + field.getElementName());
				}
			}
			// This is an additional field added, not present in the node
			fieldNameSet.remove("serialVersionUID");
			if (nodeNameSet.equals(fieldNameSet)) {
				// Do nothing. There is no need to create a Java class
				System.out.println("SETS are equal");
				return;
			} else {
				// Java class has to be created, but with a different name
				System.out.println("SETS are not equal");
				// Creates local common package for the service
				Path localCommonPath = new Path(serviceName+File.separator+"common");
				final IFolder localCommonFolder = ResourcesUtil.createFolders(responseFolder, localCommonPath,
						monitor);

				createJavaClass(localCommonFolder, responseFolder, kCollChild, serviceName,
						kCollChild.getDataModelAnnotationName() + ".java", "commons", monitor);
			}
		} else {
			// If there is no existing Java class, then create one
			createJavaClass(commonsFolder, responseFolder, kCollChild, serviceName,
					kCollChild.getDataModelAnnotationName() + ".java", "commons", monitor);
		}
	}

	private ICompilationUnit createJavaClass(IFolder srcFolder, IFolder responseFolder,
			KeyedCollectionModel input, String serviceName, String javaClassName, String type,
			IProgressMonitor monitor) {

		System.out.println("JAVA class Creating for : " + javaClassName);
		IPackageFragment responseFragment = (IPackageFragment) JavaCore.create(responseFolder);
		IPackageFragment srcPkgFragment = (IPackageFragment) JavaCore.create(srcFolder);

		ContextJavaCreator creator = new ContextJavaCreator(input,
				responseFragment.getElementName(), serviceName);
		creator.setCurrentPackageName(srcPkgFragment.getElementName());
		String contents = creator.createClass(type);
		ICompilationUnit javaUnit = null;
		try {
			javaUnit = srcPkgFragment.createCompilationUnit(javaClassName, contents, true, monitor);
		} catch (JavaModelException e) {
			out.println(e.getMessage());
			out.println(e.toString());
			e.printStackTrace();
		}
		/*
		 * Adds the Java Classes created, so that Organize Action can be done on
		 * all of them
		 */
		javaUnitsCreated.add(javaUnit);
		return javaUnit;
	}

	@Override
	public void addPages() {
		primaryInputWizardPage = new PrimaryInputWizardPage(selection);
		addPage(primaryInputWizardPage);
		xmlInputWizardPage = new XMLInputWizardPage();
		addPage(xmlInputWizardPage);
	}

	@Override
	public boolean canFinish() {
		return (getContainer().getCurrentPage() == xmlInputWizardPage && xmlInputWizardPage
				.isPageComplete());

	}
}
