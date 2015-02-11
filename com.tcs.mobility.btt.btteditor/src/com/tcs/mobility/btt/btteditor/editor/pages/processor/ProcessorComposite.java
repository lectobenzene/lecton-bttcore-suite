package com.tcs.mobility.btt.btteditor.editor.pages.processor;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tcs.mobility.btt.btteditor.Activator;
import com.tcs.mobility.btt.btteditor.editor.pages.common.TitleComposite;
import com.tcs.mobility.btt.btteditor.editor.pages.processor.elements.FormatterDialog;
import com.tcs.mobility.btt.core.source.models.processor.ProcessorModel;
import com.tcs.mobility.btt.core.source.models.processor.elements.RefFormatModel;
import com.tcs.mobility.btt.core.source.models.utils.WatchableList;

public class ProcessorComposite extends Composite implements KeyListener {
	private WritableValue value;

	private static WatchableList refFormats;
	private ProcessorModel processorModel;

	protected RefFormatModel selectedModel;
	private ScrolledComposite scrolledComposite;
	private TitleComposite cmpTitle;
	private Composite scrollParentComposite;
	private Composite cmpGeneralInformation;
	private Composite cmpFormatters;
	private Composite cmpTemporary;
	private Label lblGeneralInfoTitle;
	private Label lblGeneralInfoSubtitle;
	private Label label;
	private Text txtId;
	private Label label_1;
	private Text txtClass;
	private Label label_2;
	private Text txtFlow;
	private Label label_3;
	private Text txtContext;
	private Label lblNewLabel;
	private Label lblFormatters;
	private Label lblThisShowsA;
	private TableViewer tableViewer;
	private Table table;
	private TableViewerColumn tvcName;
	private TableColumn tblclmnName;
	private TableViewerColumn tvcId;
	private TableColumn tblclmnId;
	private Composite composite_2;
	private Button btnAdd;
	private Button btnRemove;
	private Button btnModify;
	private Composite cmpGeneralInformationTitle;
	private Composite cmpFormattersTitle;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param processorModel
	 */
	public ProcessorComposite(final Composite parent, int style, ProcessorModel model,
			PropertyChangeListener listner) {
		super(parent, style);

		processorModel = model;
		value = new WritableValue();
		value.setValue(processorModel);
		processorModel.addChangeListener(listner);
		refFormats = (WatchableList) processorModel.getRefFormats();
		constructView();
		setListeners(parent);
	}

	private void setListeners(final Composite parent) {
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FormatterDialog dialog = new FormatterDialog(getShell());
				dialog.open();
				if (dialog.getReturnCode() == Dialog.OK) {
					RefFormatModel refFormat = new RefFormatModel();
					refFormat.setName(dialog.getFormatterName());
					refFormat.setRefId(dialog.getFormatterRefId());
					refFormats = (WatchableList) tableViewer.getInput();
					refFormats.add(refFormat);
					processorModel.setRefFormats(refFormats);
					tableViewer.refresh();
				}
			}
		});
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeFormatter();
			}
		});
		btnModify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FormatterDialog dialog = new FormatterDialog(getShell());
				dialog.create();
				dialog.setFormatterName(selectedModel.getName());
				dialog.setFormatterRefId(selectedModel.getRefId());
				dialog.open();
				if (dialog.getReturnCode() == Dialog.OK) {
					selectedModel.setName(dialog.getFormatterName());
					selectedModel.setRefId(dialog.getFormatterRefId());
					refFormats = (WatchableList) tableViewer.getInput();
					processorModel.setRefFormats(refFormats);
					tableViewer.refresh();
				}
			}
		});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				RefFormatModel model = (RefFormatModel) selection.getFirstElement();
				selectedModel = model;
			}
		});

		// setting key listener for the table
		table.addKeyListener(this);
	}

	private void constructView() {
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginBottom = 2;
		setLayout(gridLayout);

		cmpTitle = new TitleComposite(this, SWT.NONE);
		cmpTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmpTitle.setLblTitleText("BTT Processor");

		scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		scrollParentComposite = new Composite(scrolledComposite, SWT.NONE);
		scrollParentComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scrollParentComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		scrollParentComposite.setLayout(new GridLayout(2, true));

		consturctGeneralInformation();
		constructTemporary();
		constructFormatters();

		scrolledComposite.setContent(scrollParentComposite);
		scrolledComposite.setMinSize(scrollParentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		autoSizeTable();
		initializeDataBindings();
	}

	private void autoSizeTable() {
		tblclmnName.pack();
		tblclmnId.pack();
	}

	private void consturctGeneralInformation() {

		cmpGeneralInformation = new Composite(scrollParentComposite, SWT.NONE);
		cmpGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		GridLayout gl_cmpGeneralInformation = new GridLayout(2, false);
		cmpGeneralInformation.setLayout(gl_cmpGeneralInformation);

		cmpGeneralInformationTitle = new Composite(cmpGeneralInformation, SWT.NONE);
		GridLayout gl_cmpGeneralInformationTitle = new GridLayout(2, false);
		gl_cmpGeneralInformationTitle.marginLeft = 5;
		gl_cmpGeneralInformationTitle.marginHeight = 0;
		gl_cmpGeneralInformationTitle.marginWidth = 0;
		cmpGeneralInformationTitle.setLayout(gl_cmpGeneralInformationTitle);
		GridData gd_cmpGeneralInformationTitle = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_cmpGeneralInformationTitle.heightHint = 26;
		cmpGeneralInformationTitle.setLayoutData(gd_cmpGeneralInformationTitle);
		cmpGeneralInformationTitle.setBackgroundImage(Activator.getDefault().createImage(
				"images/heading_bg.png"));
		cmpGeneralInformationTitle.setBackgroundMode(SWT.INHERIT_DEFAULT);

		lblGeneralInfoTitle = new Label(cmpGeneralInformationTitle, SWT.NONE);
		lblGeneralInfoTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		lblGeneralInfoTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblGeneralInfoTitle.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblGeneralInfoTitle.setText("General Information");

		lblGeneralInfoSubtitle = new Label(cmpGeneralInformation, SWT.NONE);
		lblGeneralInfoSubtitle.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		GridData gd_lblGeneralInfoSubtitle = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblGeneralInfoSubtitle.verticalIndent = 1;
		lblGeneralInfoSubtitle.setLayoutData(gd_lblGeneralInfoSubtitle);
		lblGeneralInfoSubtitle
				.setText("This section describes general information about the processor");

		label = new Label(cmpGeneralInformation, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		label.setText("ID:");

		txtId = new Text(cmpGeneralInformation, SWT.BORDER);
		GridData gd_txtId = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtId.verticalIndent = 4;
		txtId.setLayoutData(gd_txtId);

		label_1 = new Label(cmpGeneralInformation, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		label_1.setText("Class:");

		txtClass = new Text(cmpGeneralInformation, SWT.BORDER);
		txtClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		label_2 = new Label(cmpGeneralInformation, SWT.NONE);
		label_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		label_2.setText("Flow:");

		txtFlow = new Text(cmpGeneralInformation, SWT.BORDER);
		txtFlow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		label_3 = new Label(cmpGeneralInformation, SWT.NONE);
		label_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		label_3.setText("Context:");

		txtContext = new Text(cmpGeneralInformation, SWT.BORDER);
		txtContext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	}

	private void constructFormatters() {
		cmpFormatters = new Composite(scrollParentComposite, SWT.NONE);
		cmpFormatters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_cmpFormatters = new GridLayout(2, false);
		cmpFormatters.setLayout(gl_cmpFormatters);

		cmpFormattersTitle = new Composite(cmpFormatters, SWT.NONE);
		GridData gd_cmpFormattersTitle = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gd_cmpFormattersTitle.heightHint = 26;
		cmpFormattersTitle.setLayoutData(gd_cmpFormattersTitle);
		GridLayout gl_cmpFormattersTitle = new GridLayout(2, false);
		gl_cmpFormattersTitle.marginLeft = 5;
		gl_cmpFormattersTitle.marginHeight = 0;
		gl_cmpFormattersTitle.marginWidth = 0;
		cmpFormattersTitle.setLayout(gl_cmpFormattersTitle);
		cmpFormattersTitle.setBackgroundImage(Activator.getDefault().createImage(
				"images/heading_bg.png"));
		cmpFormattersTitle.setBackgroundMode(SWT.INHERIT_DEFAULT);

		lblFormatters = new Label(cmpFormattersTitle, SWT.NONE);
		lblFormatters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		lblFormatters.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblFormatters.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		lblFormatters.setText("Formatters");

		lblThisShowsA = new Label(cmpFormatters, SWT.WRAP);
		GridData gd_lblThisShowsA = new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1);
		gd_lblThisShowsA.verticalIndent = 1;
		gd_lblThisShowsA.widthHint = 336;
		gd_lblThisShowsA.heightHint = 29;
		lblThisShowsA.setLayoutData(gd_lblThisShowsA);
		lblThisShowsA
				.setText("This shows a list of all the formatters to be used in the processor");

		tableViewer = new TableViewer(cmpFormatters, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		table = tableViewer.getTable();
		table.setCapture(true);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_table.heightHint = 200;
		table.setLayoutData(gd_table);

		tvcName = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnName = tvcName.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		tvcId = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnId = tvcId.getColumn();
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");

		composite_2 = new Composite(cmpFormatters, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.marginWidth = 0;
		composite_2.setLayout(gl_composite_2);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_2.widthHint = 43;
		composite_2.setLayoutData(gd_composite_2);

		btnAdd = new Button(composite_2, SWT.NONE);
		btnAdd.setToolTipText("Add new formatter");
		btnAdd.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		GridData gd_btnAdd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_btnAdd.widthHint = 26;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setText("+");

		btnRemove = new Button(composite_2, SWT.NONE);
		btnRemove.setToolTipText("Remove selected formatter\r\n");
		btnRemove.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		GridData gd_btnRemove = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_btnRemove.widthHint = 24;
		btnRemove.setLayoutData(gd_btnRemove);
		btnRemove.setText("-");

		btnModify = new Button(composite_2, SWT.NONE);
		btnModify.setToolTipText("Modify selected formatter");
		btnModify.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnModify.setText("M");

		tvcName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RefFormatModel rf = (RefFormatModel) element;
				return rf.getName();
			}
		});
		tvcId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RefFormatModel rf = (RefFormatModel) element;
				return rf.getRefId();
			}
		});
		tableViewer.setInput(refFormats);
	}

	private void constructTemporary() {
		cmpTemporary = new Composite(scrollParentComposite, SWT.NONE);
		cmpTemporary.setLayout(new GridLayout(1, false));
		cmpTemporary.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));

		lblNewLabel = new Label(cmpTemporary, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblNewLabel
				.setText("TEMPORARY SPACE!\r\n\r\nDo Not Park Here!\r\nWaiting for Flow && Context");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Text getTxtId() {
		return txtId;
	}

	protected DataBindingContext initializeDataBindings() {

		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtIdObserveWidget = WidgetProperties.text(SWT.Modify).observe(
				txtId);
		IObservableValue idModelObserveValue = BeanProperties.value("id").observeDetail(value);
		bindingContext.bindValue(observeTextTxtIdObserveWidget, idModelObserveValue, null, null);
		//
		IObservableValue observeTextTxtClassObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(txtClass);
		IObservableValue implClassModelObserveValue = BeanProperties.value("implClass")
				.observeDetail(value);
		bindingContext.bindValue(observeTextTxtClassObserveWidget, implClassModelObserveValue,
				null, null);
		//
		IObservableValue observeTextTxtFlowObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(txtFlow);
		IObservableValue refFlowModelObserveValue = BeanProperties.value("refFlow").observeDetail(
				value);
		bindingContext.bindValue(observeTextTxtFlowObserveWidget, refFlowModelObserveValue, null,
				null);
		//
		IObservableValue observeTextTxtContextObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(txtContext);
		IObservableValue operationContextModelObserveValue = BeanProperties.value(
				"operationContext").observeDetail(value);
		bindingContext.bindValue(observeTextTxtContextObserveWidget,
				operationContextModelObserveValue, null, null);
		return bindingContext;
	}

	public WritableValue getValue() {
		return value;
	}

	public void setValue(WritableValue value) {
		this.value = value;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setInput(ProcessorModel model, PropertyChangeListener listener) {
		processorModel = model;
		processorModel.addChangeListener(listener);
		value.setValue(processorModel);
		refFormats = (WatchableList) processorModel.getRefFormats();
		tableViewer.setInput(refFormats);
	}

	public ProcessorModel getOutput(PropertyChangeListener listener) {
		processorModel = (ProcessorModel) value.getValue();
		refFormats = (WatchableList) tableViewer.getInput();
		processorModel.addChangeListener(listener);
		processorModel.setRefFormats(refFormats);
		return processorModel;
	}

	private void removeFormatter() {
		refFormats = (WatchableList) tableViewer.getInput();
		refFormats.remove(selectedModel);
		processorModel.setRefFormats(refFormats);
		tableViewer.refresh();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.DEL) {
			removeFormatter();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
