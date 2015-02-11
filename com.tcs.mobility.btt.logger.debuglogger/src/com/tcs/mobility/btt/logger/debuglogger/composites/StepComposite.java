package com.tcs.mobility.btt.logger.debuglogger.composites;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class StepComposite extends Composite {
	private Composite cmpCommon;
	private Label lblLbltime;
	private Label lblLblsequence;
	private Composite cmpInfo;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public StepComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		cmpCommon = new Composite(this, SWT.NONE);
		cmpCommon.setLayout(new GridLayout(3, false));
		cmpCommon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblLbltime = new Label(cmpCommon, SWT.NONE);
		lblLbltime.setText("lblTime");
		new Label(cmpCommon, SWT.NONE);

		lblLblsequence = new Label(cmpCommon, SWT.NONE);
		lblLblsequence.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblLblsequence.setText("lblSequence");

		cmpInfo = new Composite(this, SWT.NONE);
		cmpInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
