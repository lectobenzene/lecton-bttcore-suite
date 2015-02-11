package com.tcs.mobility.btt.btteditor.editor.pages.context;

import java.beans.PropertyChangeListener;
import java.util.EventObject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tcs.mobility.btt.btteditor.Activator;
import com.tcs.mobility.btt.btteditor.editor.pages.common.TitleComposite;
import com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop.ContextTreeDragListener;
import com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop.ContextTreeTypeTransfer;
import com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop.IniValuesTableDropListener;
import com.tcs.mobility.btt.btteditor.editor.pages.context.elements.contexttree.ContextTreeComposite;
import com.tcs.mobility.btt.core.source.models.context.ContextModel;
import com.tcs.mobility.btt.core.source.models.context.elements.childelements.IniValueModel;
import com.tcs.mobility.btt.core.source.models.context.elements.reference.RefKCollModel;
import com.tcs.mobility.btt.core.source.models.context.elements.reference.RefServiceModel;
import com.tcs.mobility.btt.core.source.models.utils.WatchableList;

public class ContextComposite extends Composite {
	private ScrolledComposite scrolledComposite;
	private Composite scrollParentComposite;
	private TitleComposite titleComposite;
	private ContextTreeComposite cmpContextTree;
	private Composite cmpGeneralInformation;
	private Composite cmpGeneralInformationTitle;
	private Label lblGeneralInformationTitle;
	private Label lblGeneralInformationSubTitle;
	private Composite cmpGeneralInformationContents;
	private Label lblId;
	private Label lblClass;
	private Text txtContextId;
	private Text txtContextClass;
	private Composite cmpOthers;
	private Composite cmpInitialValues;
	private Composite cmpServices;
	private Composite cmpServicesTitle;
	private Label lblServicesTitle;
	private Label lblListOfAll;
	private Table tableRefService;
	private TableViewer tableViewerRefService;
	private TableColumn tblclmnId;
	private TableViewerColumn tblViewerClmnId;
	private TableColumn tblclmnAlias;
	private TableViewerColumn tblViewerClmnAlias;
	private TableColumn tblclmnType;
	private TableViewerColumn tblViewerClmnType;
	private Composite cmpInitialValuesTitle;
	private Label lblNewLabel;
	private Label lblShowsTheInitial;
	private Table tableIniValues;
	private TableViewer tableViewerIniValues;
	private TableColumn tblclmnName;
	private TableViewerColumn tblViewerClmnName;
	private TableColumn tblclmnValue;
	private TableViewerColumn tblViewerClmnValue;

	private ContextModel contextModel;
	private WritableValue value;
	private RefKCollModel refKColl;
	private DragSource dragSource;
	private DropTarget dropTarget;

	private ColumnLabelProvider labelProviderIniValuesValue;
	private ColumnLabelProvider labelProviderIniValuesName;
	private ColumnLabelProvider labelProviderRefServiceAlias;
	private ColumnLabelProvider labelProviderRefServiceId;
	private ColumnLabelProvider labelProviderRefServiceType;
	private WatchableList refServices;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ContextComposite(Composite parent, int style, ContextModel model, PropertyChangeListener listener) {
		super(parent, style);

		contextModel = model;

		contextModel.addChangeListener(listener);

		value = new WritableValue();
		value.setValue(contextModel);

		refKColl = (RefKCollModel) contextModel.getRefKColls().get(0);

		contructView();

		setTreeInput();

		setInivaluesTableProviders();
		setIniValuesTableInput();
		setIniValuesTableEditingSupport();

		setRefServiceTableProviders();
		setRefServiceTableInput();
		setRefServiceTableEditingSupport();

		initializeDataBindings();
	}

	private void setIniValuesTableEditingSupport() {
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewerIniValues, new FocusCellOwnerDrawHighlighter(
				tableViewerIniValues));

		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(tableViewerIniValues) {
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

		TableViewerEditor.create(tableViewerIniValues, focusCellManager, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tblViewerClmnName.setEditingSupport(new EditingSupport(tableViewerIniValues) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof IniValueModel) {
					((IniValueModel) element).setName(idValue);
					iniValuesTableModified(element);
				}
			}

			@Override
			protected Object getValue(Object element) {
				String value = labelProviderIniValuesName.getText(element);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) tableViewerIniValues.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof IniValueModel;
			}
		});

		tblViewerClmnValue.setEditingSupport(new EditingSupport(tableViewerIniValues) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof IniValueModel) {
					((IniValueModel) element).setValue(idValue);
					iniValuesTableModified(element);
				}
			}

			@Override
			protected Object getValue(Object element) {
				String value = labelProviderIniValuesValue.getText(element);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) tableViewerIniValues.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof IniValueModel;
			}
		});
	}

	private void setRefServiceTableEditingSupport() {
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewerRefService, new FocusCellOwnerDrawHighlighter(
				tableViewerRefService));

		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(tableViewerRefService) {
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

		TableViewerEditor.create(tableViewerRefService, focusCellManager, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tblViewerClmnAlias.setEditingSupport(new EditingSupport(tableViewerRefService) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof RefServiceModel) {
					((RefServiceModel) element).setAlias(idValue);
					refServicesTableModified(element);
				}
			}

			@Override
			protected Object getValue(Object element) {
				String value = labelProviderRefServiceAlias.getText(element);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) tableViewerRefService.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof RefServiceModel;
			}
		});

		tblViewerClmnId.setEditingSupport(new EditingSupport(tableViewerRefService) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof RefServiceModel) {
					((RefServiceModel) element).setRefId(idValue);
					refServicesTableModified(element);
				}
			}

			@Override
			protected Object getValue(Object element) {
				String value = labelProviderRefServiceId.getText(element);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) tableViewerRefService.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof RefServiceModel;
			}
		});

		tblViewerClmnType.setEditingSupport(new EditingSupport(tableViewerRefService) {

			private TextCellEditor editor;

			@Override
			protected void setValue(Object element, Object value) {
				String idValue = (String) value;
				if (element instanceof RefServiceModel) {
					((RefServiceModel) element).setType(idValue);
					refServicesTableModified(element);
				}
			}

			@Override
			protected Object getValue(Object element) {
				String value = labelProviderRefServiceType.getText(element);
				if (value == null) {
					value = "";
				}
				return value;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) tableViewerRefService.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof RefServiceModel;
			}
		});
	}

	protected void iniValuesTableModified(Object element) {
		tableViewerIniValues.refresh(element);
	}

	protected void refServicesTableModified(Object element) {
		tableViewerRefService.refresh(element);
	}

	private void setInivaluesTableProviders() {
		tableViewerIniValues.setContentProvider(new ArrayContentProvider());

		labelProviderIniValuesName = new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				IniValueModel iniValue = (IniValueModel) element;
				return iniValue.getName();
			}
		};
		tblViewerClmnName.setLabelProvider(labelProviderIniValuesName);
		labelProviderIniValuesValue = new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				IniValueModel iniValue = (IniValueModel) element;
				return iniValue.getValue();
			}
		};
		tblViewerClmnValue.setLabelProvider(labelProviderIniValuesValue);

	}

	private void setRefServiceTableProviders() {
		tableViewerRefService.setContentProvider(new ArrayContentProvider());

		labelProviderRefServiceAlias = new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				RefServiceModel refServices = (RefServiceModel) element;
				return refServices.getAlias();
			}
		};
		tblViewerClmnAlias.setLabelProvider(labelProviderRefServiceAlias);
		labelProviderRefServiceId = new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				RefServiceModel refServices = (RefServiceModel) element;
				return refServices.getRefId();
			}
		};
		tblViewerClmnId.setLabelProvider(labelProviderRefServiceId);

		labelProviderRefServiceType = new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				RefServiceModel refServices = (RefServiceModel) element;
				return refServices.getType();
			}
		};
		tblViewerClmnType.setLabelProvider(labelProviderRefServiceType);
	}

	private void setIniValuesTableInput() {
		refKColl = (RefKCollModel) contextModel.getRefKColls().get(0);
		tableViewerIniValues.setInput(refKColl.getIniValues());
	}

	private void setRefServiceTableInput() {
		refServices = contextModel.getRefServices();
		tableViewerRefService.setInput(refServices);
	}

	private void setTreeInput() {
		refKColl = (RefKCollModel) contextModel.getRefKColls().get(0);
		cmpContextTree.getTreeViewerContext().setInput(refKColl.getRootKColl().getChildren());
	}

	private void contructView() {
		setLayout(new GridLayout(1, false));

		titleComposite = new TitleComposite(this, SWT.NONE);
		titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		titleComposite.setLblTitleText("BTT Context");

		scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		scrollParentComposite = new Composite(scrolledComposite, SWT.NONE);
		scrollParentComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scrollParentComposite.setLayout(new GridLayout(3, true));
		scrollParentComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);

		constructContextTree();
		constructGeneralInformation();

		scrolledComposite.setContent(scrollParentComposite);
		scrolledComposite.setMinSize(scrollParentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void constructGeneralInformation() {
		cmpGeneralInformation = new Composite(scrollParentComposite, SWT.NONE);
		cmpGeneralInformation.setLayout(new GridLayout(1, false));
		cmpGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		cmpGeneralInformationTitle = new Composite(cmpGeneralInformation, SWT.NONE);
		GridLayout gl_cmpGeneralInformationTitle = new GridLayout(1, false);
		gl_cmpGeneralInformationTitle.marginLeft = 5;
		gl_cmpGeneralInformationTitle.marginHeight = 0;
		gl_cmpGeneralInformationTitle.marginWidth = 0;
		cmpGeneralInformationTitle.setLayout(gl_cmpGeneralInformationTitle);
		GridData gd_cmpGeneralInformationTitle = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_cmpGeneralInformationTitle.heightHint = 26;
		cmpGeneralInformationTitle.setLayoutData(gd_cmpGeneralInformationTitle);
		cmpGeneralInformationTitle.setBackgroundImage(Activator.getDefault().createImage("images/heading_bg.png"));
		// To inherit the background by all children
		cmpGeneralInformationTitle.setBackgroundMode(SWT.INHERIT_DEFAULT);

		lblGeneralInformationTitle = new Label(cmpGeneralInformationTitle, SWT.NONE);
		lblGeneralInformationTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblGeneralInformationTitle.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblGeneralInformationTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblGeneralInformationTitle.setText("General Information");

		lblGeneralInformationSubTitle = new Label(cmpGeneralInformation, SWT.WRAP);
		GridData gd_lblGeneralInformationSubTitle = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblGeneralInformationSubTitle.verticalIndent = 1;
		lblGeneralInformationSubTitle.setLayoutData(gd_lblGeneralInformationSubTitle);
		lblGeneralInformationSubTitle.setText("This section describes the general description of the Conetext");

		cmpGeneralInformationContents = new Composite(cmpGeneralInformation, SWT.NONE);
		GridLayout gl_cmpGeneralInformationContents = new GridLayout(2, false);
		cmpGeneralInformationContents.setLayout(gl_cmpGeneralInformationContents);
		cmpGeneralInformationContents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		lblId = new Label(cmpGeneralInformationContents, SWT.NONE);
		lblId.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblId.setText("ID:");

		txtContextId = new Text(cmpGeneralInformationContents, SWT.BORDER);
		GridData gd_txtContextId = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtContextId.horizontalIndent = 5;
		txtContextId.setLayoutData(gd_txtContextId);

		lblClass = new Label(cmpGeneralInformationContents, SWT.NONE);
		lblClass.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblClass.setText("Class:");

		txtContextClass = new Text(cmpGeneralInformationContents, SWT.BORDER);
		GridData gd_txtContextClass = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtContextClass.horizontalIndent = 5;
		txtContextClass.setLayoutData(gd_txtContextClass);

		cmpOthers = new Composite(scrollParentComposite, SWT.NONE);
		cmpOthers.setLayout(new GridLayout(1, false));
		cmpOthers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		cmpInitialValues = new Composite(cmpOthers, SWT.NONE);
		cmpInitialValues.setLayout(new GridLayout(1, false));
		cmpInitialValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpInitialValuesTitle = new Composite(cmpInitialValues, SWT.NONE);
		GridLayout gl_cmpInitialValuesTitle = new GridLayout(1, false);
		gl_cmpInitialValuesTitle.marginLeft = 5;
		gl_cmpInitialValuesTitle.marginHeight = 0;
		gl_cmpInitialValuesTitle.marginWidth = 0;
		cmpInitialValuesTitle.setLayout(gl_cmpInitialValuesTitle);
		GridData gd_cmpInitialValuesTitle = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_cmpInitialValuesTitle.heightHint = 26;
		cmpInitialValuesTitle.setLayoutData(gd_cmpInitialValuesTitle);
		cmpInitialValuesTitle.setBackgroundImage(Activator.getDefault().createImage("images/heading_bg.png"));
		cmpInitialValuesTitle.setBackgroundMode(SWT.INHERIT_DEFAULT);

		lblNewLabel = new Label(cmpInitialValuesTitle, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblNewLabel.setText("Initial Values");

		lblShowsTheInitial = new Label(cmpInitialValues, SWT.NONE);
		lblShowsTheInitial.setText("Shows the initial values of the elements in context");

		tableViewerIniValues = new TableViewer(cmpInitialValues, SWT.BORDER | SWT.FULL_SELECTION);
		tableIniValues = tableViewerIniValues.getTable();
		tableIniValues.setHeaderVisible(true);
		GridData gd_tableIniValues = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_tableIniValues.heightHint = 112;
		tableIniValues.setLayoutData(gd_tableIniValues);

		tblViewerClmnName = new TableViewerColumn(tableViewerIniValues, SWT.NONE);
		tblclmnName = tblViewerClmnName.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { ContextTreeTypeTransfer.getInstance() };
		dropTarget = new DropTarget(tableIniValues, operations);
		dropTarget.setTransfer(transferTypes);
		dropTarget.addDropListener(new IniValuesTableDropListener(tableViewerIniValues));

		tblViewerClmnValue = new TableViewerColumn(tableViewerIniValues, SWT.NONE);
		tblclmnValue = tblViewerClmnValue.getColumn();
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("Value");

		cmpServices = new Composite(cmpOthers, SWT.NONE);
		cmpServices.setLayout(new GridLayout(1, false));
		cmpServices.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpServicesTitle = new Composite(cmpServices, SWT.NONE);
		cmpServicesTitle.setBackgroundImage(Activator.getDefault().createImage("images/heading_bg.png"));
		cmpServicesTitle.setBackgroundMode(SWT.INHERIT_DEFAULT);
		GridLayout gl_cmpServicesTitle = new GridLayout(1, false);
		gl_cmpServicesTitle.marginLeft = 5;
		gl_cmpServicesTitle.marginHeight = 0;
		gl_cmpServicesTitle.marginWidth = 0;
		cmpServicesTitle.setLayout(gl_cmpServicesTitle);
		GridData gd_cmpServicesTitle = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_cmpServicesTitle.heightHint = 26;
		cmpServicesTitle.setLayoutData(gd_cmpServicesTitle);

		lblServicesTitle = new Label(cmpServicesTitle, SWT.NONE);
		lblServicesTitle.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblServicesTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblServicesTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblServicesTitle.setText("Services");

		lblListOfAll = new Label(cmpServices, SWT.NONE);
		lblListOfAll.setText("List of all the services used in the web service");

		tableViewerRefService = new TableViewer(cmpServices, SWT.BORDER | SWT.FULL_SELECTION);
		tableRefService = tableViewerRefService.getTable();
		tableRefService.setHeaderVisible(true);
		GridData gd_tableRefService = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_tableRefService.heightHint = 120;
		tableRefService.setLayoutData(gd_tableRefService);

		tblViewerClmnId = new TableViewerColumn(tableViewerRefService, SWT.NONE);
		tblclmnId = tblViewerClmnId.getColumn();
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");

		tblViewerClmnAlias = new TableViewerColumn(tableViewerRefService, SWT.NONE);
		tblclmnAlias = tblViewerClmnAlias.getColumn();
		tblclmnAlias.setWidth(100);
		tblclmnAlias.setText("Alias");

		tblViewerClmnType = new TableViewerColumn(tableViewerRefService, SWT.NONE);
		tblclmnType = tblViewerClmnType.getColumn();
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");
	}

	private void constructContextTree() {
		cmpContextTree = new ContextTreeComposite(scrollParentComposite, SWT.NONE);
		TreeViewer treeViewerContext = cmpContextTree.getTreeViewerContext();
		Tree tree = treeViewerContext.getTree();

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		dragSource = new DragSource(tree, operations);
		Transfer[] transferTypes = new Transfer[] { ContextTreeTypeTransfer.getInstance() };
		dragSource.setTransfer(transferTypes);
		dragSource.addDragListener(new ContextTreeDragListener(treeViewerContext));

		// cmpContextTree.setLayout(new GridLayout(1, false));
		cmpContextTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
	}

	protected DataBindingContext initializeDataBindings() {

		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtIdObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtContextId);
		IObservableValue idModelObserveValue = BeanProperties.value("id").observeDetail(value);
		bindingContext.bindValue(observeTextTxtIdObserveWidget, idModelObserveValue, null, null);
		//
		IObservableValue observeTextTxtClassObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtContextClass);
		IObservableValue implClassModelObserveValue = BeanProperties.value("implClass").observeDetail(value);
		bindingContext.bindValue(observeTextTxtClassObserveWidget, implClassModelObserveValue, null, null);
		return bindingContext;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setInput(ContextModel model, PropertyChangeListener listener) {
		contextModel = model;
		contextModel.addChangeListener(listener);
		value.setValue(contextModel);
		setTreeInput();
		cmpContextTree.autoSizeTree();
		cmpContextTree.getTreeViewerContext().refresh();
	}

	public ContextModel getOutput(PropertyChangeListener listener) {
		contextModel = (ContextModel) value.getValue();
		contextModel.addChangeListener(listener);
		return contextModel;
	}

}
