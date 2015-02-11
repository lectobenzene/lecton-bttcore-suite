package com.tcs.mobility.btt.logger.debuglogger.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SourceComposite extends Composite {
	private Text txtLogRecordSource;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SourceComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		txtLogRecordSource = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtLogRecordSource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	}

	public String getTxtLogRecordSource() {
		return txtLogRecordSource.getText();
	}

	public void setTxtLogRecordSource(String txtLogRecordSourceText) {
		txtLogRecordSource.setText(txtLogRecordSourceText);
	}
}
