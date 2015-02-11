/**
 * 
 */
package com.tcs.mobility.btt.createopxml.wizards.pages;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;
import com.tcs.mobility.btt.core.utils.resources.WSConsole;
import com.tcs.mobility.btt.createopxml.wizards.NewOpFileWizard;

/**
 * The second page of the Wizard
 * 
 * @author Saravana
 *
 */
public class ConfigurationWizardPage extends WizardPage {
	private Group grpProcessor;
	private Label label;
	private Label label_1;
	private Label lblLocation;
	private Button btnBrowseProcessorLocation;
	private Text txtProcessorName;
	private Text txtProcessorLocation;
	private Group grpContext;
	private Label lblTheContextOf;
	private Label label_3;
	private Label label_4;
	private Button btnBrowseContextLocation;
	private Text txtContextName;
	private Text txtContextLocation;

	private MessageConsole console;
	private MessageConsoleStream out;

	private PrimaryWizardPage primaryPage;

	public ConfigurationWizardPage() {
		super("ConfigurationWizardPage");
		setTitle("Configure Op File");
		setDescription("This wizard lets you to configure the default webservice files created");
		console = WSConsole.findDefaultConsole();
		out = console.newMessageStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		container.setLayout(new FormLayout());

		grpProcessor = new Group(container, SWT.NONE);
		FormData fd_grpProcessor = new FormData();
		fd_grpProcessor.right = new FormAttachment(100, -10);
		fd_grpProcessor.top = new FormAttachment(0, 10);
		fd_grpProcessor.left = new FormAttachment(0, 10);
		grpProcessor.setLayoutData(fd_grpProcessor);
		grpProcessor.setText("Processor");

		label = new Label(grpProcessor, SWT.WRAP | SWT.CENTER);
		label.setText("The Processor of the webservice requires an implementation class. This file will be created with the default name in the default location, unless otherwise changed.");
		label.setAlignment(SWT.LEFT);
		label.setBounds(10, 25, 534, 36);

		label_1 = new Label(grpProcessor, SWT.NONE);
		label_1.setText("Name:");
		label_1.setBounds(10, 67, 55, 15);

		lblLocation = new Label(grpProcessor, SWT.NONE);
		lblLocation.setBounds(10, 97, 55, 15);
		lblLocation.setText("Location:");

		btnBrowseProcessorLocation = new Button(grpProcessor, SWT.NONE);
		btnBrowseProcessorLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleLocationBrowse(getProcessorLocation(), txtProcessorLocation,
						"Select Location of Processor");
			}
		});
		btnBrowseProcessorLocation.setBounds(469, 92, 75, 25);
		btnBrowseProcessorLocation.setText("Browse...");

		txtProcessorName = new Text(grpProcessor, SWT.BORDER);
		txtProcessorName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtProcessorName.setBounds(71, 67, 392, 21);

		txtProcessorLocation = new Text(grpProcessor, SWT.BORDER);
		txtProcessorLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtProcessorLocation.setBounds(71, 94, 392, 21);

		grpContext = new Group(container, SWT.NONE);
		fd_grpProcessor.bottom = new FormAttachment(grpContext, -6);
		FormData fd_grpContext = new FormData();
		fd_grpContext.bottom = new FormAttachment(81, 35);
		fd_grpContext.top = new FormAttachment(0, 147);
		fd_grpContext.right = new FormAttachment(100, -10);
		fd_grpContext.left = new FormAttachment(0, 10);
		grpContext.setLayoutData(fd_grpContext);
		grpContext.setText("Context");

		lblTheContextOf = new Label(grpContext, SWT.WRAP | SWT.CENTER);
		lblTheContextOf
				.setText("The Context of the webservice requires an implementation class. This file will be created with the default name in the default location, unless otherwise changed.");
		lblTheContextOf.setAlignment(SWT.LEFT);
		lblTheContextOf.setBounds(10, 25, 534, 36);

		label_3 = new Label(grpContext, SWT.NONE);
		label_3.setText("Name:");
		label_3.setBounds(10, 67, 55, 15);

		label_4 = new Label(grpContext, SWT.NONE);
		label_4.setText("Location:");
		label_4.setBounds(10, 97, 55, 15);

		btnBrowseContextLocation = new Button(grpContext, SWT.NONE);
		btnBrowseContextLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleLocationBrowse(getContextLocation(), txtContextLocation,
						"Select Location of Context");
			}
		});
		btnBrowseContextLocation.setText("Browse...");
		btnBrowseContextLocation.setBounds(469, 92, 75, 25);

		txtContextName = new Text(grpContext, SWT.BORDER);
		txtContextName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtContextName.setBounds(71, 67, 392, 21);

		txtContextLocation = new Text(grpContext, SWT.BORDER);
		txtContextLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtContextLocation.setBounds(71, 94, 392, 21);

		setControl(container);
	}

	/**
	 * Opens up the ContainerSelectionDialog and handles the user input
	 * 
	 * @param location
	 *            location of the package
	 * @param txtField
	 *            the respective text field
	 * @param title
	 *            The title of the Container Selection dialog
	 */
	protected void handleLocationBrowse(String location, Text txtField, String title) {
		IContainer initialContainer;
		if (location.length() != 0) {
			initialContainer = ResourcesUtil.getResource(primaryPage.getServiceLocation())
					.getProject().getFolder(location);
		} else {
			initialContainer = ResourcesUtil.getResource(primaryPage.getServiceLocation())
					.getProject().getFolder("src");
		}
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
				initialContainer, false, title);

		int result = dialog.open();
		if (result == ContainerSelectionDialog.OK) {
			Object[] selections = dialog.getResult();
			if (selections.length == 1) {
				txtField.setText(((Path) selections[0]).removeFirstSegments(1).toOSString());
			}
		}
	}

	/**
	 * Validation method
	 */
	private void dialogChanged() {
		if (getProcessorName().length() == 0) {
			updateStatus("Processor name should not be empty");
			return;
		}
		if (getProcessorLocation().length() == 0) {
			updateStatus("Processor location must not be empty");
			return;
		}

		IResource processorResource = ResourcesUtil.getResource(getProcessorFullLocation());

		if (processorResource == null || (processorResource.getType() & IResource.FOLDER) == 0) {
			updateStatus("Processor Location doesn't exist");
			return;
		}

		if (getContextName().length() == 0) {
			updateStatus("Context name should not be empty");
			return;
		}
		if (getContextLocation().length() == 0) {
			updateStatus("Context location must not be empty");
			return;
		}

		IResource contextResource = ResourcesUtil.getResource(getContextFullLocation());

		if (contextResource == null || (contextResource.getType() & IResource.FOLDER) == 0) {
			updateStatus("Context Location doesn't exist");
			return;
		}
		updateStatus(null);

	}

	private String getProcessorLocation() {
		return txtProcessorLocation.getText();
	}

	public String getProcessorFullLocation() {
		return getFullLocation(getProcessorLocation());
	}

	public String getContextFullLocation() {
		return getFullLocation(getContextLocation());
	}

	private String getFullLocation(String path) {
		return getProjectName(primaryPage.getServiceLocation()) + File.separator + path;
	}

	private String getContextLocation() {
		return txtContextLocation.getText();
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private void initialize() {
		primaryPage = (PrimaryWizardPage) ((NewOpFileWizard) getWizard())
				.getPage("PrimaryWizardPage");
		String serviceName = primaryPage.getServiceName();
		txtProcessorName.setText(serviceName + "Processor");
		txtContextName.setText(serviceName + "Context");

		if (primaryPage.isWarProject()) {

			IResource resource = ResourcesUtil.getResource(primaryPage.getServiceLocation());

			if (resource != null
					&& (resource.getType() & (IResource.PROJECT | IResource.FOLDER)) != 0) {

				try {
					resource.getProject().accept(new IResourceVisitor() {

						@Override
						public boolean visit(IResource resource) throws CoreException {
							IPath path = resource.getFullPath().removeFirstSegments(1);
							IPath prefixPath = new Path("src" + File.separator + "main"
									+ File.separator + "java");

							if (prefixPath.isPrefixOf(path)) {
								if ("automaton".equals(path.lastSegment())
										|| "automation".equals(path.lastSegment())) {
									txtProcessorLocation.setText(path.toOSString());
								}
								if ("context".equals(path.lastSegment())) {
									txtContextLocation.setText(path.toOSString());
								}
							}

							return true;
						}
					});

				} catch (CoreException e) {
					out.println(e.getMessage());
					out.println(e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			initialize();
		}
		super.setVisible(visible);
	}

	public String getProcessorName() {
		return txtProcessorName.getText();
	}

	public String getContextName() {
		return txtContextName.getText();
	}

	private String getProjectName(String path) {
		return (ResourcesUtil.getResource(path)).getProject().getName();
	}

	public String getProcessorPackage() {
		return convertToPackageFormat(getProcessorLocation());
	}

	public String getContextPackage() {
		return convertToPackageFormat(getContextLocation());
	}

	private String convertToPackageFormat(String location) {
		return (new Path(location)).removeFirstSegments(3).toOSString()
				.replace(File.separator, ".");
	}

}
