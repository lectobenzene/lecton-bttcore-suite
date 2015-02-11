package com.tcs.mobility.btt.btteditor.editor.pages.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tcs.mobility.btt.btteditor.Activator;

public class TitleComposite extends Composite {
	private Label lblTitle;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public TitleComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		setBackgroundImage(Activator.getDefault().createImage("images/title_bg.png"));

		lblTitle = new Label(this, SWT.NONE);
		lblTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblTitle.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblTitle.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblTitle.setText("BTT Editor");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public String getLblTitleText() {
		return lblTitle.getText();
	}

	public void setLblTitleText(String text) {
		lblTitle.setText(text);
	}
}
