package com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import com.tcs.mobility.btt.core.source.models.context.elements.childelements.IniValueModel;
import com.tcs.mobility.btt.core.source.models.utils.WatchableList;

public class IniValuesTableDropListener extends ViewerDropAdapter {

	private final Viewer viewer;

	public IniValuesTableDropListener(Viewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);
		IniValueModel target = (IniValueModel) determineTarget(event);
		String translatedLocation = "";
		switch (location) {
		case 1:
			translatedLocation = "Dropped before the target ";
			break;
		case 2:
			translatedLocation = "Dropped after the target ";
			break;
		case 3:
			translatedLocation = "Dropped on the target ";
			break;
		case 4:
			translatedLocation = "Dropped into nothing ";
			break;
		}
		System.out.println(translatedLocation);
		if (target != null) {
			System.out.println("The drop was done on the element: " + target.getName());
		}
		super.drop(event);
	}

	// This method performs the actual drop
	// We simply add the String we receive to the model and trigger a refresh of
	// the
	// viewer by calling its setInput method.
	@Override
	public boolean performDrop(Object data) {
		if (data instanceof ContextTreeType) {
			ContextTreeType element = (ContextTreeType) data;
			if (element.getType().equalsIgnoreCase(ContextTreeType.TYPE_FIELD_MODEL)) {
				System.out.println("ID : " + element.getId());
				System.out.println("Type : " + element.getType());
				System.out.println("Value : " + element.getValue());
				IniValueModel iniValue = new IniValueModel();
				iniValue.setName(element.getId());
				
				// If the VALUE is null, then initialize it as EMPTY String
				if(ContextTreeType.DEFAULT.equals(element.getValue())){
					iniValue.setValue("");
				}else{
					iniValue.setValue(element.getValue());
				}
				WatchableList iniValues = (WatchableList) viewer.getInput();
				iniValues.add(iniValue);
				viewer.setInput(iniValues);
				return true;
			}
		}
		return false;

	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
	}

}