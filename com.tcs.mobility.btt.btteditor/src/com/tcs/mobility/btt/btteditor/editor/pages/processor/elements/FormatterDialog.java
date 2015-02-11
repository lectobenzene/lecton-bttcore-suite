package com.tcs.mobility.btt.btteditor.editor.pages.processor.elements;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FormatterDialog extends Dialog {
	private Label lblFormatterType;
	private Combo cmbFormatterType;
	private Label lblName;
	private Text txtFormatterName;
	private Label lblRefid;
	private Text txtFormatterRefId;

	private String formatterName;
	private String formatterRefId;

	public FormatterDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		lblFormatterType = new Label(composite, SWT.NONE);
		lblFormatterType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblFormatterType.setText("Formatter Type");

		cmbFormatterType = new Combo(composite, SWT.NONE);
		GridData gd_cmbFormatterType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_cmbFormatterType.horizontalIndent = 10;
		cmbFormatterType.setLayoutData(gd_cmbFormatterType);

		lblName = new Label(composite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name");

		txtFormatterName = new Text(composite, SWT.BORDER);
		GridData gd_txtFormatterName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtFormatterName.widthHint = 300;
		gd_txtFormatterName.horizontalIndent = 10;
		txtFormatterName.setLayoutData(gd_txtFormatterName);

		lblRefid = new Label(composite, SWT.NONE);
		lblRefid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblRefid.setText("Ref ID");

		txtFormatterRefId = new Text(composite, SWT.BORDER);
		GridData gd_txtFormatterRefId = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtFormatterRefId.horizontalIndent = 10;
		txtFormatterRefId.setLayoutData(gd_txtFormatterRefId);

		return composite;
	}

	@Override
	protected void okPressed() {
		setFormatterName(txtFormatterName.getText());
		setFormatterRefId(txtFormatterRefId.getText());
		super.okPressed();

	}

	public String getFormatterName() {
		return formatterName;
	}

	public void setFormatterName(String formatterName) {
		txtFormatterName.setText(formatterName);
		this.formatterName = formatterName;
	}

	public String getFormatterRefId() {
		return formatterRefId;
	}

	public void setFormatterRefId(String formatterRefId) {
		txtFormatterRefId.setText(formatterRefId);
		this.formatterRefId = formatterRefId;
	}
}
