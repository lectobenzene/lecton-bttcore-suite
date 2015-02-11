package com.tcs.mobility.btt.btteditor.editor.pages.common.factory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tcs.mobility.btt.btteditor.Activator;

public final class CommonFactory {
	/**
	 * @wbp.factory
	 */
	public static Label createLabel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		label.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		/**
		 * @wbp.
		 */
		label.setBackgroundImage(Activator.getDefault().createImage(
				"images/heading_bg.png"));
		return label;
	}
}