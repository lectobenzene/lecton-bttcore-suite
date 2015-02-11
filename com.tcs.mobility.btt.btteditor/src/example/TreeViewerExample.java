package example;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * @author Ram Kulkarni (http://ramkulkarni.com)
 *
 */
public class TreeViewerExample {

	Map<TreeItemData,TreeItemData[]> treeModelData = new HashMap<TreeItemData, TreeItemData[]>();
	TextCellEditor cellEditor = null;
	
	public TreeViewerExample(Shell shell) {
		TreeViewer treeViewer = new TreeViewer(shell);
		
		treeModelData.put(new TreeItemData("parent1"), new TreeItemData[]{
			new TreeItemData("child1"), 
			new TreeItemData("child2")
			});
		
		treeViewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			@Override
			public void dispose() {
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (treeModelData.containsKey(element))
					return true;
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement.equals("initial_input"))
					return treeModelData.keySet().toArray();
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				Object value = treeModelData.get(parentElement);
				if (value != null)
					return (TreeItemData[])value;
				return null;
			}
		});
		
		treeViewer.setInput("initial_input");
		
		cellEditor = new MyTextCellEditor(treeViewer.getTree());

		TreeViewerEditor.create(treeViewer, new ColumnViewerEditorActivationStrategy(treeViewer){
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {  
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}  
			
		}, TreeViewerEditor.DEFAULT);
	
		//Using old APIs
		//enableEditing1(treeViewer);
		
		
		//Using new APIs (since Eclipse 3.3)
		enableEditing2(treeViewer);
		
	}
	
	private void enableEditing1(TreeViewer treeViewer)
	{
		treeViewer.setColumnProperties(new String[]{"col1"});
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setCellEditors(new CellEditor[]{cellEditor});
		treeViewer.setCellModifier(new ICellModifier() {
			
			@Override
			public void modify(Object element, String property, Object value) {
				if (element instanceof TreeItem)
				{
					//update element and tree model
					TreeItem treeItem = (TreeItem)element;
					TreeItemData data = (TreeItemData)treeItem.getData();
					data.value = value.toString();
					treeItem.setText(value.toString());
				}
			}
			
			@Override
			public Object getValue(Object element, String property) {
				return element.toString();
			}
			
			@Override
			public boolean canModify(Object element, String property) {
				return true;
			}
		});
	}
	
	private void enableEditing2 (TreeViewer treeViewer)
	{
		final TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.getColumn().setWidth(50);
		column.setLabelProvider(new TreeLabelProvider());
		final TreeViewer finalTreeViewer = treeViewer;
		column.setEditingSupport(new EditingSupport(treeViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof TreeItemData)
				{
					TreeItemData data = (TreeItemData)element;
					data.value = value.toString();
				}
				finalTreeViewer.update(element, null);
			}
			
			@Override
			protected Object getValue(Object element) {
				return element.toString();
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return cellEditor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	
		treeViewer.getControl().addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				column.getColumn().setWidth(((Tree)e.getSource()).getBounds().width);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	
	}

	public static void main(String[]args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new TreeViewerExample(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
	
	class TreeItemData
	{
		String value;
		public TreeItemData (String value)
		{
			this.value = value;
		}
		
		public String toString()
		{
			return value;
		}
	}
	
	class TreeLabelProvider extends ColumnLabelProvider
	{
		public String getText(Object element) {
			return element.toString();
		}
	}
	
	class MyTextCellEditor extends TextCellEditor
	{
		int minHeight = 0;

		public MyTextCellEditor(Tree tree) {
			super(tree, SWT.BORDER);
			Text txt = (Text)getControl();
		
	    	Font fnt = txt.getFont();
	    	FontData[] fontData = fnt.getFontData();
	    	if (fontData != null && fontData.length > 0)
	    		minHeight = fontData[0].getHeight() + 10;
		}
		
	    public LayoutData getLayoutData() {
	    	LayoutData data = super.getLayoutData();
	    	if (minHeight > 0)
	    		data.minimumHeight = minHeight;
	    	return data;
	    }	
	}
	
}