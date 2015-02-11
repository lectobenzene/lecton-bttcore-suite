package com.tcs.mobility.btt.btteditor.editor.pages.context.elements.contexttree.elements.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

public class DataElementAddDialog extends Dialog {
	private Label lblType;
	private Combo combo;
	private Label lblId;
	private Label lblValue;
	private Text text;
	private Text text_1;

	protected DataElementAddDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginLeft = 1;
		composite.setLayout(gl_composite);

		lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type");

		combo = new Combo(composite, SWT.NONE);
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo.horizontalIndent = 5;
		combo.setLayoutData(gd_combo);

		lblId = new Label(composite, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblId.setText("ID");

		text = new Text(composite, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.horizontalIndent = 5;
		text.setLayoutData(gd_text);

		lblValue = new Label(composite, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblValue.setText("Value");

		text_1 = new Text(composite, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.horizontalIndent = 5;
		text_1.setLayoutData(gd_text_1);

		return composite;
	}

}
