package com.tcs.mobility.btt.btteditor.editor.pages.context.elements.contexttree;

import java.util.EventObject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tcs.mobility.btt.btteditor.Activator;
import com.tcs.mobility.btt.btteditor.editor.pages.context.elements.contentprovider.ContextContentProvider;
import com.tcs.mobility.btt.btteditor.editor.pages.context.elements.labelprovider.ContextParseLabelProvider;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.FieldModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.elements.RefDataModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.parent.DataElementModel;

public class ContextTreeComposite extends Composite {

	// private Composite cmpContextTree;
	private Composite cmpContextTreeTitle;
	private Label lblContextTreeTitle;
	private Label lblContextTreeSubTitle;
	private Text txtFilterContextTree;
	private TreeViewer treeViewerContext;

	private Tree treeContext;
	private TreeViewerColumn treeViewerColumnElement;
	private TreeColumn trclmnContextElement;
	private TreeViewerColumn treeViewerColumnValue;
	private TreeColumn trclmnContextValue;

	private Action addSibling;
	private Action addChild;
	private Action removeElement;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ContextTreeComposite(Composite parent, int style) {
		super(parent, style);
		constructView();

		treeViewerContext.setContentProvider(new ContextContentProvider());
		final ContextParseLabelProvider treeLabelProvider = new ContextParseLabelProvider(parent.getDisplay());
		treeViewerContext.setLabelProvider(treeLabelProvider);

		TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(treeViewerContext, new FocusCellOwnerDrawHighlighter(
				treeViewerContext));

		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(treeViewerContext) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				// Enable editor only with mouse double click
				if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
					EventObject source = event.sourceEvent;
					if (source instanceof MouseEvent && ((MouseEvent) source).button == 3)
						return false;
					return true;
				}
				return false;
			}
		};

		TreeViewerEditor.create(treeViewerContext, focusCellManager, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		treeViewerColumnElement.setEditingSupport(new EditingSupport(treeViewerContext) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof RefDataModel) {
					((RefDataModel) element).setRefId(idValue);
				} else {
					((DataElementModel) element).setId(idValue);
				}

			}

			@Override
			protected Object getValue(Object element) {
				String value = treeLabelProvider.getColumnText(element, 0);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) treeViewerContext.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		treeViewerColumnValue.setEditingSupport(new EditingSupport(treeViewerContext) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof FieldModel) {
					((FieldModel) element).setValue(idValue);
				}
			}

			@Override
			protected Object getValue(Object element) {
				String value = treeLabelProvider.getColumnText(element, 1);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) treeViewerContext.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof FieldModel;
			}
		});
		createActions();

		createMenu();
	}

	private void createMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				DataElementModel model = (DataElementModel) treeContext.getSelection()[0].getData();

				if (model instanceof KeyedCollectionModel || model instanceof IndexedCollectionModel) {
					manager.add(addChild);
				}
				manager.add(addSibling);
				manager.add(removeElement);
			}
		});

		Menu menu = menuMgr.createContextMenu(treeContext);
		treeContext.setMenu(menu);
	}

	private void createActions() {
		addSibling = new Action() {
			public void run() {
				System.out.println("Add sibling");
			}
		};
		addChild = new Action() {
			public void run() {
				System.out.println("Add child");
			}
		};
		removeElement = new Action() {
			public void run() {
				System.out.println("Remove");
			}
		};
		addSibling.setText("Add Sibling...");
		addChild.setText("Add Child...");
		removeElement.setText("Remove");
	}

	private void constructView() {
		// cmpContextTree = new Composite(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		// cmpContextTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true, 1, 1));

		cmpContextTreeTitle = new Composite(this, SWT.NONE);
		GridLayout gl_cmpContextTreeTitle = new GridLayout(1, false);
		gl_cmpContextTreeTitle.marginLeft = 5;
		gl_cmpContextTreeTitle.marginHeight = 0;
		gl_cmpContextTreeTitle.marginWidth = 0;
		cmpContextTreeTitle.setLayout(gl_cmpContextTreeTitle);
		GridData gd_cmpContextTreeTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_cmpContextTreeTitle.heightHint = 26;
		cmpContextTreeTitle.setLayoutData(gd_cmpContextTreeTitle);
		//cmpContextTreeTitle.setBackgroundImage(Activator.getDefault().createImage("images/heading_bg.png"));
		// To inherit the background by all children
		cmpContextTreeTitle.setBackgroundMode(SWT.INHERIT_DEFAULT);

		lblContextTreeTitle = new Label(cmpContextTreeTitle, SWT.NONE);
		lblContextTreeTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblContextTreeTitle.setBackground(null);
		lblContextTreeTitle.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblContextTreeTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblContextTreeTitle.setText("Context Tree");

		lblContextTreeSubTitle = new Label(this, SWT.WRAP);
		GridData gd_lblContextTreeSubTitle = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_lblContextTreeSubTitle.verticalIndent = 1;
		lblContextTreeSubTitle.setLayoutData(gd_lblContextTreeSubTitle);
		lblContextTreeSubTitle.setText("Shows the elements in the Context");

		txtFilterContextTree = new Text(this, SWT.SEARCH);
		txtFilterContextTree.setMessage("type filter text");
		txtFilterContextTree.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
		txtFilterContextTree.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		treeViewerContext = new TreeViewer(this, SWT.FULL_SELECTION);
		treeContext = treeViewerContext.getTree();
		treeContext.setHeaderVisible(true);
		treeContext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		treeViewerColumnElement = new TreeViewerColumn(treeViewerContext, SWT.NONE);
		trclmnContextElement = treeViewerColumnElement.getColumn();
		trclmnContextElement.setWidth(100);
		trclmnContextElement.setText("Element");

		treeViewerColumnValue = new TreeViewerColumn(treeViewerContext, SWT.NONE);
		trclmnContextValue = treeViewerColumnValue.getColumn();
		trclmnContextValue.setWidth(100);
		trclmnContextValue.setText("Value");

	}

	public void autoSizeTree() {
		trclmnContextElement.pack();
		trclmnContextValue.pack();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public TreeViewer getTreeViewerContext() {
		return treeViewerContext;
	}

	public void setTreeViewerContext(TreeViewer treeViewerContext) {
		this.treeViewerContext = treeViewerContext;
	}
}
