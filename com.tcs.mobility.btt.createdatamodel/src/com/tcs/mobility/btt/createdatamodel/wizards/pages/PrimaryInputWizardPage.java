package com.tcs.mobility.btt.createdatamodel.wizards.pages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;

public class PrimaryInputWizardPage extends WizardPage {
	private Label lblServiceName;
	private Text txtServiceName;
	private Label lblResponseLocation;
	private Text txtResponseLocation;
	private Button btnBrowse;
	private ISelection selection;

	public PrimaryInputWizardPage() {
		super("PrimaryInputWizardPage");
		setTitle("Create Data Model");
		setDescription("This wizard creates Data Model for the given Web Service");
	}

	/**
	 * @wbp.parser.constructor
	 */
	public PrimaryInputWizardPage(ISelection selection) {
		this();
		this.selection = selection;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		lblServiceName = new Label(container, SWT.NONE);
		lblServiceName.setText("Service Name :");

		txtServiceName = new Text(container, SWT.BORDER);
		txtServiceName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		GridData gd_txtServiceName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtServiceName.widthHint = 413;
		txtServiceName.setLayoutData(gd_txtServiceName);
		new Label(container, SWT.NONE);

		lblResponseLocation = new Label(container, SWT.NONE);
		lblResponseLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblResponseLocation.setText("Response Location :");

		txtResponseLocation = new Text(container, SWT.BORDER);
		txtResponseLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtResponseLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleModuleLocationBrowse();
			}
		});
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse...");
		container.setTabList(new Control[] { txtServiceName, txtResponseLocation, btnBrowse });

		setSelectionData(selection);
	}

	/**
	 * Method to handle the Browse button action of Service location
	 */
	private void handleModuleLocationBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin
				.getWorkspace().getRoot(), false, "Select the DataModel Jar Project");
		int result = dialog.open();
		if (result == ContainerSelectionDialog.OK) {
			Object[] selections = dialog.getResult();
			if (selections.length == 1) {
				/*
				 * If the selected Project contains the Response.java, then the
				 * package of Response.java is set in the field, else, the
				 * container with is selected is set in the field
				 */
				String responseFilePath = ResourcesUtil.isFileAvailable(
						((Path) selections[0]).toOSString(), "Response.java");
				if (responseFilePath != null) {
					setTxtResponseLocationText(ResourcesUtil.getResource(responseFilePath)
							.getParent().getFullPath().toOSString());
				} else {
					setTxtResponseLocationText(((Path) selections[0]).toOSString());
				}
			}
		}
	}

	/**
	 * If a package is selected while creating a new Data Model, then this
	 * location is used as the location of the Module class file.
	 * 
	 * @param selection
	 *            The selection made
	 */
	private void setSelectionData(ISelection selection) {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() <= 1) {
				Object obj = sSelection.getFirstElement();
				String path = null;
				if (obj instanceof PackageFragmentRoot) {
					final PackageFragmentRoot pkgFragmentRoot = (PackageFragmentRoot) obj;
					path = pkgFragmentRoot.getJavaProject().getPath().toOSString();

				} else if (obj instanceof PackageFragment) {
					final PackageFragment pkgFragment = (PackageFragment) obj;
					path = pkgFragment.getJavaProject().getPath().toOSString();

				} else if (obj instanceof CompilationUnit) {
					final CompilationUnit cmpUnit = (CompilationUnit) obj;
					path = cmpUnit.getJavaProject().getPath().toOSString();
				}
				if (obj instanceof IResource) {
					IContainer container;
					if (obj instanceof IContainer) {
						container = (IContainer) obj;
					} else {
						container = ((IResource) obj).getParent();
					}
					path = container.getProject().getFullPath().toOSString();
				}
				if (path != null) {
					String responseFilePath = ResourcesUtil.isFileAvailable(path, "Response.java");
					if (responseFilePath != null) {
						setTxtResponseLocationText(ResourcesUtil.getResource(responseFilePath)
								.getParent().getFullPath().toOSString());
					} else {
						System.out.println("No Response.java");
					}
				} else {
					System.out.println("Path is null");
				}
			}
		}

	}

	/**
	 * The method that is called when any field is changed. This method acts as
	 * a Validation method. The 'Next' button is enabled when all validation are
	 * satisfied.
	 */
	private void dialogChanged() {
		String serviceName = getTxtServiceNameText();
		IResource resource = ResourcesUtil.getResource(getTxtResponseLocationText());

		if ("".equalsIgnoreCase(serviceName.trim())) {
			updateStatus("Service Name should not be empty");
			return;
		}
		if (serviceName.contains(".")) {
			updateStatus("Provide proper name for the service");
			return;
		}
		if (getTxtResponseLocationText().length() == 0) {
			updateStatus("Provide the package where the Response.java is located");
			return;
		}
		if (resource == null
				|| !resource.exists()
				|| (ResourcesUtil.isFileAvailable(getTxtResponseLocationText(), "Response.java") == null)) {
			updateStatus("Response.java is not found. Select proper location");
			return;
		}
		IResource pkgResource = ResourcesUtil.getResource(getTxtResponseLocationText());
		IFolder responseFolder = pkgResource.getProject().getFolder(
				(new Path(getTxtResponseLocationText()).removeFirstSegments(1)));
		System.out.println(responseFolder.getFullPath());
		IFolder currentFolder = responseFolder.getFolder(getTxtServiceNameText());
		System.out.println(currentFolder.getFullPath());
		if (currentFolder.exists()) {
			updateStatus("Service already exists. Choose a different name");
			return;
		}
		updateStatus(null);
	}

	/**
	 * Displays an error message in the title area and enables/disables the NEXT
	 * button
	 * 
	 * @param message
	 *            Error message to display in the title area
	 */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getTxtServiceNameText() {
		return txtServiceName.getText();
	}

	public void setTxtServiceNameText(String text) {
		txtServiceName.setText(text);
	}

	public String getTxtResponseLocationText() {
		return txtResponseLocation.getText();
	}

	public void setTxtResponseLocationText(String text_1) {
		txtResponseLocation.setText(text_1);
	}
}
