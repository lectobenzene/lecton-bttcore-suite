package com.tcs.mobility.btt.btteditor.editor.pages.context.elements.labelprovider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.tcs.mobility.btt.btteditor.Activator;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.FieldModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.elements.RefDataModel;

public class ContextParseLabelProvider implements ITableLabelProvider {

	private Image image;
	private Color colorDataType;

	public ContextParseLabelProvider(Display display) {
		colorDataType = new Color(display, new RGB(255, 0, 0));
	}

	@Override
	public void dispose() {
		colorDataType.dispose();
		colorDataType = null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (element instanceof KeyedCollectionModel) {
				image = Activator.getDefault().createImage("images/kcoll.png");
			} else if (element instanceof IndexedCollectionModel) {
				image = Activator.getDefault().createImage("images/icoll.png");
			} else if (element instanceof FieldModel) {
				image = Activator.getDefault().createImage("images/field.png");
			} else if (element instanceof RefDataModel) {
				image = Activator.getDefault().createImage("images/refdata.png");
			}else {
				image = null;
			}
			return image;
		case 1:
			return null;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String normalText = "UNKNOWN";
		switch (columnIndex) {
		case 0:
			if (element instanceof KeyedCollectionModel) {
				normalText = ((KeyedCollectionModel) element).getId();
			} else if (element instanceof IndexedCollectionModel) {
				normalText = ((IndexedCollectionModel) element).getId();
			} else if (element instanceof FieldModel) {
				normalText = ((FieldModel) element).getId();
			} else if (element instanceof RefDataModel) {
				normalText = ((RefDataModel) element).getRefId();
			}
			break;
		case 1:
			if (element instanceof FieldModel) {
				normalText = ((FieldModel) element).getValue();
			} else {
				normalText = null;
			}
			break;
		}
		return normalText;
	}

}
