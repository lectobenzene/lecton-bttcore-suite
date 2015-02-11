package com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.FieldModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.elements.RefDataModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.parent.DataElementModel;

public class ContextTreeDragListener implements DragSourceListener {

	private final TreeViewer viewer;
	private String id;
	private String type;
	private String value;

	public ContextTreeDragListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("Finshed Drag");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		DataElementModel model = (DataElementModel) selection.getFirstElement();

		id = (String) model.getCompositeId();
		if (model instanceof RefDataModel) {
			type = ContextTreeType.TYPE_REFDATA;
		} else if (model instanceof FieldModel) {
			type = ContextTreeType.TYPE_FIELD_MODEL;
			value = ((FieldModel) model).getValue();
		} else if (model instanceof KeyedCollectionModel) {
			type = ContextTreeType.TYPE_KEYED_COLLECTION;
		} else if (model instanceof IndexedCollectionModel) {
			type = ContextTreeType.TYPE_INDEXED_COLLECTION;
		}

		if (ContextTreeTypeTransfer.getInstance().isSupportedType(event.dataType)) {
			ContextTreeType contextType = new ContextTreeType(id, type);

			/*
			 * If value is null, then use the default value defined
			 * inContextTreeType. Handle this in the DROP listener
			 */
			if (value != null) {
				contextType.setValue(value);
			}

			event.data = contextType;
		}

	}

	@Override
	public void dragStart(DragSourceEvent event) {
		System.out.println("Start Drag");
	}

}