/**
 * 
 */
package com.tcs.mobility.btt.createopxml.wizards.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.tcs.mobility.btt.core.source.files.FileExtract;
import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;

/**
 * The First page of the wizard
 * 
 * @author Saravana
 *
 */
public class PrimaryWizardPage extends WizardPage {

	private ISelection selection;
	private Label lblName;
	private Text txtServiceName;
	private Text txtServiceLocation;
	private Button btnBrowseServiceLocation;
	private Label lblPackage;
	private Composite composite;
	private Label lblTheProjectYou;
	private Label lblWarProject;
	private Text txtWarProjectLocation;
	private Button btnBrowseWarProjectLocation;
	private Label lblPropertiesFile;
	private Combo cmbPropertiesFile;
	private Label lblNewLabel;
	private Label lblLibrary;
	private Combo cmbLibraryEntries;

	private boolean isWarProject;
	private String dseIniLocation;
	private HashMap<String, IResource> propertiesFileList;
	private Text txtServiceDescription;
	private Label lblDescription;
	private ControlDecoration decorTxtServiceName;

	public PrimaryWizardPage() {
		super("PrimaryWizardPage");
		setTitle("Create AL Webservice");
		setDescription("This wizard creates an AL webservice 'op.xml' file and registers it in the dse.ini file");
	}

	/**
	 * @param pageName
	 * @wbp.parser.constructor
	 */
	public PrimaryWizardPage(ISelection selection) {
		this();
		this.selection = selection;
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

		lblName = new Label(container, SWT.NONE);
		FormData fd_lblName = new FormData();
		fd_lblName.top = new FormAttachment(0, 10);
		fd_lblName.left = new FormAttachment(0, 10);
		lblName.setLayoutData(fd_lblName);
		lblName.setText("Service Name:");

		txtServiceName = new Text(container, SWT.BORDER);
		txtServiceName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		FormData fd_txtServiceName = new FormData();
		fd_txtServiceName.right = new FormAttachment(100, -86);
		fd_txtServiceName.left = new FormAttachment(lblName, 8);
		fd_txtServiceName.top = new FormAttachment(0, 7);
		txtServiceName.setLayoutData(fd_txtServiceName);

		// Decoration to show warning if service names doesn't end with 'Op'
		decorTxtServiceName = new ControlDecoration(txtServiceName, SWT.CENTER);
		decorTxtServiceName.setDescriptionText("Service names ends with 'Op'");
		Image image = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
		decorTxtServiceName.setImage(image);

		txtServiceLocation = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtServiceLocation.setEditable(true);
		txtServiceLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkDseIniAvailability();
				dialogChanged();
			}
		});
		FormData fd_txtServiceLocation = new FormData();
		txtServiceLocation.setLayoutData(fd_txtServiceLocation);

		btnBrowseServiceLocation = new Button(container, SWT.NONE);
		fd_txtServiceLocation.right = new FormAttachment(btnBrowseServiceLocation, -6);
		btnBrowseServiceLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleServiceLocationBrowse();
			}
		});
		FormData fd_btnBrowseServiceLocation = new FormData();
		fd_btnBrowseServiceLocation.top = new FormAttachment(txtServiceLocation, -2, SWT.TOP);
		fd_btnBrowseServiceLocation.left = new FormAttachment(0, 494);
		fd_btnBrowseServiceLocation.right = new FormAttachment(100, -10);
		btnBrowseServiceLocation.setLayoutData(fd_btnBrowseServiceLocation);
		btnBrowseServiceLocation.setText("Browse...");

		lblPackage = new Label(container, SWT.NONE);
		fd_txtServiceLocation.left = new FormAttachment(lblPackage, 34);
		FormData fd_lblPackage = new FormData();
		fd_lblPackage.top = new FormAttachment(txtServiceLocation, 3, SWT.TOP);
		fd_lblPackage.left = new FormAttachment(lblName, 0, SWT.LEFT);
		lblPackage.setLayoutData(fd_lblPackage);
		lblPackage.setText("Location:");

		composite = new Composite(container, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, -43);
		fd_composite.top = new FormAttachment(0, 99);
		fd_composite.left = new FormAttachment(0);
		fd_composite.right = new FormAttachment(0, 574);
		composite.setLayoutData(fd_composite);
		composite.setVisible(false);

		lblTheProjectYou = new Label(composite, SWT.WRAP);
		lblTheProjectYou.setBounds(10, 10, 554, 38);
		lblTheProjectYou
				.setText("The Project you selected does not contain a 'dse.ini' file. You might be trying to create the service in JAR project. Select a WAR project and the library in which this file will be present.");

		lblWarProject = new Label(composite, SWT.NONE);
		lblWarProject.setBounds(10, 59, 76, 15);
		lblWarProject.setText("WAR Project:");

		txtWarProjectLocation = new Text(composite, SWT.BORDER);
		txtWarProjectLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtWarProjectLocation.setBounds(92, 56, 396, 21);

		btnBrowseWarProjectLocation = new Button(composite, SWT.NONE);
		btnBrowseWarProjectLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWarProjectLocationBrowse();
			}
		});
		btnBrowseWarProjectLocation.setText("Browse...");
		btnBrowseWarProjectLocation.setBounds(494, 54, 70, 25);

		lblPropertiesFile = new Label(composite, SWT.NONE);
		lblPropertiesFile.setBounds(10, 97, 77, 15);
		lblPropertiesFile.setText("Properties File:");

		cmbPropertiesFile = new Combo(composite, SWT.READ_ONLY);
		cmbPropertiesFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				System.out.println("COMBO MODIFIED");
				if (getPropetiesFileLocation() != null) {
					cmbLibraryEntries.setEnabled(true);
					cmbLibraryEntries.removeAll();
					FileExtract extract = new FileExtract();
					System.out.println(getPropetiesFileLocation());
					File file = new File(getPropetiesFileLocation());
					ArrayList<String> propertyKeysList = extract.getPropertyKeys(file);
					for (String key : propertyKeysList) {
						cmbLibraryEntries.add(key);
					}
				}
				dialogChanged();
			}
		});
		cmbPropertiesFile.setBounds(92, 94, 174, 23);

		lblNewLabel = new Label(composite, SWT.SEPARATOR);
		lblNewLabel.setBounds(272, 97, 9, 21);

		lblLibrary = new Label(composite, SWT.NONE);
		lblLibrary.setBounds(287, 97, 39, 15);
		lblLibrary.setText("Library:");

		cmbLibraryEntries = new Combo(composite, SWT.READ_ONLY);
		cmbLibraryEntries.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		cmbLibraryEntries.setBounds(332, 94, 156, 23);

		txtServiceDescription = new Text(container, SWT.BORDER);
		fd_txtServiceLocation.top = new FormAttachment(txtServiceDescription, 6);
		FormData fd_txtServiceDescription = new FormData();
		fd_txtServiceDescription.right = new FormAttachment(txtServiceName, 0, SWT.RIGHT);
		fd_txtServiceDescription.top = new FormAttachment(txtServiceName, 6);
		fd_txtServiceDescription.left = new FormAttachment(txtServiceName, 0, SWT.LEFT);
		txtServiceDescription.setLayoutData(fd_txtServiceDescription);

		lblDescription = new Label(container, SWT.NONE);
		FormData fd_lblDescription = new FormData();
		fd_lblDescription.top = new FormAttachment(txtServiceDescription, 3, SWT.TOP);
		fd_lblDescription.left = new FormAttachment(lblName, 0, SWT.LEFT);
		lblDescription.setLayoutData(fd_lblDescription);
		lblDescription.setText("Description:");

		setControl(container);
		container.setTabList(new Control[] { txtServiceName, txtServiceDescription,
				txtServiceLocation, btnBrowseServiceLocation, composite });

		initialize();
	}

	/**
	 * Getter for {@link #txtServiceName ServiceName}
	 * 
	 * @return name of the service
	 */
	public String getServiceName() {
		return txtServiceName.getText();
	}

	/**
	 * Getter for {@link #txtServiceDescription Service Description}
	 * 
	 * @return description of the service
	 */
	public String getServiceDescription() {
		return txtServiceDescription.getText();
	}

	/**
	 * Getter for {@link #txtServiceLocation ServiceLocation}
	 * 
	 * @return location of the service file
	 */
	public String getServiceLocation() {
		return txtServiceLocation.getText();
	}

	/**
	 * Getter for {@link #txtWarProjectLocation War Project Location}
	 * 
	 * @return location of the war project
	 */
	public String getWarProjectLocation() {
		return txtWarProjectLocation.getText();
	}

	/**
	 * Returns the key in the .PROPERTIES file
	 * 
	 * @return the currently selected key in the .PROPERTIES file
	 */
	public String getJarPath() {
		if (cmbLibraryEntries.getSelectionIndex() != -1) {
			return cmbLibraryEntries.getItem(cmbLibraryEntries.getSelectionIndex());
		}
		return null;
	}

	/**
	 * To find if the project where the service is created is of type JAR or WAR
	 * 
	 * @return true if WAR project, otherwise false
	 */
	public boolean isWarProject() {
		return isWarProject;
	}

	/**
	 * Getter for the dseIniLocation
	 * 
	 * @return location of dse.ini file
	 */
	public String getDseIniLocation() {
		return dseIniLocation;
	}

	/**
	 * Checks if the selected Project contains dse.ini file. If not, its
	 * considered as a JAR project, else a WAR project.
	 */
	private void checkDseIniAvailability() {
		if (getServiceLocation().length() != 0) {
			dseIniLocation = ResourcesUtil.isFileAvailable(getServiceLocation(), "dse.ini");
			isWarProject = dseIniLocation != null;
			System.out.println("FF:" + composite.getVisible());
			// secondary project selection views are made visible
			composite.setVisible(!isWarProject);
		}
	}

	/**
	 * Initialization method
	 */
	private void initialize() {
		System.out.println(composite.getVisible());
		cmbLibraryEntries.setEnabled(false);
		cmbPropertiesFile.setEnabled(false);

		/*
		 * If a folder is selected while creating a new webservice, then this
		 * location is used as the location of the service file.
		 */
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() <= 1) {
				Object obj = sSelection.getFirstElement();
				if (obj instanceof IResource) {
					IContainer container;
					if (obj instanceof IContainer) {
						container = (IContainer) obj;
					} else {
						container = ((IResource) obj).getParent();
					}
					txtServiceLocation.setText(container.getFullPath().toOSString());
				}
			}
		}
		// Check if the service location is JAR or WAR project
		checkDseIniAvailability();
	}

	/**
	 * Method to handle the Browse button action of Service location
	 */
	private void handleServiceLocationBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin
				.getWorkspace().getRoot(), false, "Select Location of Service");
		int result = dialog.open();
		if (result == ContainerSelectionDialog.OK) {
			Object[] selections = dialog.getResult();
			if (selections.length == 1) {
				txtServiceLocation.setText(((Path) selections[0]).toOSString());
			}
		}
	}

	/**
	 * Method to handle the Browse button action of the WAR project location
	 */
	private void handleWarProjectLocationBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin
				.getWorkspace().getRoot(), false, "Select a WAR project");
		int result = dialog.open();
		if (result == ContainerSelectionDialog.OK) {
			Object[] selections = dialog.getResult();
			if (selections.length == 1) {
				IPath path = ((Path) selections[0]);
				IResource resource = ResourcesUtil.getResource(path.toOSString());

				// Remove entries of both the List
				cmbPropertiesFile.removeAll();
				cmbLibraryEntries.removeAll();

				/*
				 * No matter what folder in the project is selected, just
				 * consider the project name
				 */
				txtWarProjectLocation.setText(resource.getProject().getFullPath().toOSString());
			}
		}
	}

	/**
	 * The method that is called when any field is changed. This method acts as
	 * a Validation method. The 'Next' button is enabled when all validation are
	 * satisfied.
	 */
	private void dialogChanged() {
		String serviceName = getServiceName();
		IResource resource = ResourcesUtil.getResource(getServiceLocation());

		if ("".equalsIgnoreCase(serviceName)) {
			updateStatus("Service Name should not be empty");
			return;
		}
		if (serviceName.contains(".")) {
			updateStatus("Provide proper name for the service");
			return;
		}
		if ("".equals(getServiceLocation())) {
			updateStatus("Service Location should not be empty");
			return;
		}
		if (resource == null || (resource.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("Service Location doesn't not exist");
			return;
		}
		if (!(resource instanceof IContainer)) {
			updateStatus("Service Location is not a folder");
			return;
		}
		IContainer container = (IContainer) resource;
		if (container.getFile(new Path(getServiceFileName())).exists()) {
			updateStatus("Service already exists. Enter new Service name");
			return;
		}
		if (getServiceName().endsWith("Op")) {
			decorTxtServiceName.hide();
		} else {
			decorTxtServiceName.show();
		}
		if (isWarProject) {
			updateStatus(null);
			return;
		} else {
			System.out.println("A JAR PROJECT");
			cmbPropertiesFile.setEnabled(false);
			cmbLibraryEntries.setEnabled(false);

			if ("".equals(getWarProjectLocation())) {
				updateStatus("Select a WAR project");
				return;
			}
			dseIniLocation = ResourcesUtil.isFileAvailable(getWarProjectLocation(), "dse.ini");
			if (dseIniLocation != null) {
				cmbPropertiesFile.setEnabled(true);
				propertiesFileList = ResourcesUtil.getResources(getWarProjectLocation(),
						".properties");
			} else {
				updateStatus("The WAR project does not contain a 'dse.ini' file");
				return;
			}

			if (propertiesFileList.size() == 0) {
				updateStatus("There are no .PROPERTIES file in the WAR project.");
				return;
			} else if (cmbPropertiesFile.getItemCount() == 0) {
				for (String properties : propertiesFileList.keySet()) {
					cmbPropertiesFile.add(properties);
				}
			}

			if (cmbPropertiesFile.getSelectionIndex() == -1) {
				updateStatus("Please select a Properties File");
				return;
			}

			cmbLibraryEntries.setEnabled(true);

			if (cmbLibraryEntries.getSelectionIndex() == -1) {
				updateStatus("Please select the Library Path");
				return;
			}

			updateStatus(null);
			return;
		}

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

	/**
	 * Gets the service file name, with the '.xml' extension
	 * 
	 * @return location of the war project
	 */
	private String getServiceFileName() {
		return getServiceName() + ".xml";
	}

	/**
	 * Gets the location of the .PROPERTIES file when the Property file is
	 * selected in the Combo list
	 * 
	 * @return location of the .PROPERTIES file
	 */
	private String getPropetiesFileLocation() {
		String propertyLocation = null;
		if (cmbPropertiesFile.getSelectionIndex() != -1) {
			String propertySelected = cmbPropertiesFile.getItem(cmbPropertiesFile
					.getSelectionIndex());

			IResource propertyResource = propertiesFileList.get(propertySelected);
			System.out.println(propertyResource.getName());
			System.out.println(propertyResource.getLocation().toOSString());
			propertyLocation = propertyResource.getLocation().toOSString();
		}
		return propertyLocation;
	}

}