package com.tcs.mobility.btt.logger.debuglogger.views;

import groovy.util.Node;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.tcs.mobility.btt.logger.core.source.controller.Controller;
import com.tcs.mobility.btt.logger.core.source.models.LogRecords;
import com.tcs.mobility.btt.logger.core.source.parser.CentralParser;
import com.tcs.mobility.btt.logger.debuglogger.Activator;
import com.tcs.mobility.btt.logger.debuglogger.composites.SourceComposite;
import com.tcs.mobility.btt.logger.debuglogger.composites.StepComposite;
import org.eclipse.swt.custom.StackLayout;

public class DebugLoggerView extends ViewPart {

	private static List<LogRecords> logRecords;
	private static Controller controller;
	private static CentralParser parser;
	private static Node root;
	private static int sequence = 1;

	private Composite cmpFilepath;
	private Composite cmpDynamic;
	private Label lblDebugFile;
	private Text txtFileLocation;
	private Button btnBrowse;
	private Action nextItemAction;
	private Action previousItemAction;
	private Action separatorAction;
	private Action sourceViewAction;
	private SourceComposite cmpSource;
	private Composite composite;
	private Action lastItemAction;
	private Action firstItemAction;
	private StepComposite cmpStep;
	private Action stepViewAction;
	private Composite composite_1;
	private Composite composite_2;
	private StackLayout layout;

	public DebugLoggerView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(final Composite parent) {

		createActions();
		createToolbar();
		parent.setLayout(new GridLayout(1, false));

		cmpFilepath = new Composite(parent, SWT.NONE);
		cmpFilepath.setLayout(new GridLayout(3, false));
		cmpFilepath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		lblDebugFile = new Label(cmpFilepath, SWT.NONE);
		lblDebugFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDebugFile.setText("Debug File :");

		txtFileLocation = new Text(cmpFilepath, SWT.BORDER);
		txtFileLocation.setEditable(false);
		txtFileLocation.setToolTipText("file to debug");
		txtFileLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnBrowse = new Button(cmpFilepath, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN);
				fd.setText("Open log file");
				String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				System.out.println(selected);
				txtFileLocation.setText(selected);
				initiateLogger();
			}
		});
		btnBrowse.setText("Browse...");

		cmpDynamic = new Composite(parent, SWT.NONE);
		layout = new StackLayout();
		cmpDynamic.setLayout(layout);
		cmpDynamic.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpSource = new SourceComposite(cmpDynamic, SWT.NONE);
		cmpSource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpStep = new StepComposite(cmpDynamic, SWT.NONE);
		cmpStep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		layout.topControl = cmpStep;
		cmpDynamic.layout();
	}

	protected void initiateLogger() {
		// Initialize the existing values
		root = null;
		logRecords = null;
		sequence = 1;
		initializeViews();

		controller = new Controller();
		parser = new CentralParser();
		root = controller.parseFile(txtFileLocation.getText());
		logRecords = controller.getLogRecords(root);

		nextItemAction.setEnabled(true);
		previousItemAction.setEnabled(true);
		firstItemAction.setEnabled(true);
		lastItemAction.setEnabled(true);

		sourceViewAction.setChecked(false);
	}

	private void initializeViews() {
		cmpSource.setTxtLogRecordSource("");
		
		layout.topControl = cmpStep;
		cmpDynamic.layout();
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(sourceViewAction);
		mgr.add(stepViewAction);
		mgr.add(separatorAction);

		mgr.add(firstItemAction);
		mgr.add(previousItemAction);
		mgr.add(nextItemAction);
		mgr.add(lastItemAction);
	}

	private void createActions() {
		firstItemAction = new Action("First") {
			public void run() {
				goToFirst();
			}
		};
		firstItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(
				"icons/checkout_action.gif"));
		firstItemAction.setToolTipText("First log record");

		lastItemAction = new Action("Last") {
			public void run() {
				goToLast();
			}
		};
		lastItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(
				"icons/checkin_action.gif"));
		lastItemAction.setToolTipText("Last log record");

		nextItemAction = new Action("Next") {
			public void run() {
				next();
			}
		};
		nextItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(
				"icons/nav_forward.gif"));
		nextItemAction.setToolTipText("Next Log");

		previousItemAction = new Action("Previous") {
			public void run() {
				previous();
			}
		};
		previousItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(
				"icons/nav_backward.gif"));
		previousItemAction.setToolTipText("Previous Log");

		separatorAction = new Action("") {
		};
		separatorAction.setEnabled(false);

		sourceViewAction = new Action("Source") {
			public void run() {
				openSourceView();
			}
		};
		sourceViewAction.setChecked(true);
		sourceViewAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(
				"icons/source.gif"));
		sourceViewAction.setToolTipText("Source View");

		stepViewAction = new Action("Step") {
			public void run() {
				openStepView();
			}
		};
		stepViewAction.setChecked(true);
		stepViewAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(
				"icons/showcat_co.gif"));
		stepViewAction.setToolTipText("Step View");

		nextItemAction.setEnabled(false);
		previousItemAction.setEnabled(false);
		firstItemAction.setEnabled(false);
		lastItemAction.setEnabled(false);
	}

	protected void goToLast() {
		sequence = logRecords.size();
		Node logRecord = controller.getLogRecord(root, sequence);
		cmpSource.setTxtLogRecordSource(parser.getFormattedSourceXML(logRecord));
	}

	protected void goToFirst() {
		sequence = 1;
		Node logRecord = controller.getLogRecord(root, sequence);
		cmpSource.setTxtLogRecordSource(parser.getFormattedSourceXML(logRecord));
	}

	protected void openStepView() {
		sourceViewAction.setChecked(false);
		stepViewAction.setChecked(true);
		System.out.println("Open step view");
		layout.topControl = cmpStep;
		cmpDynamic.layout();
	}

	protected void openSourceView() {
		stepViewAction.setChecked(false);
		sourceViewAction.setChecked(true);
		System.out.println("Open source view");
		layout.topControl = cmpSource;
		cmpDynamic.layout();
	}

	protected void next() {
		System.out.println("NEXT clicked = " + sequence);
		sequence++;
		previousItemAction.setEnabled(true);
		if (sequence > logRecords.size()) {
			sequence--;
			nextItemAction.setEnabled(false);
		} else {
			Node logRecord = controller.getLogRecord(root, sequence);
			cmpSource.setTxtLogRecordSource(parser.getFormattedSourceXML(logRecord));
		}
	}

	protected void previous() {
		System.out.println("PREV clicked = " + sequence);
		sequence--;
		nextItemAction.setEnabled(true);
		if (sequence < 1) {
			sequence++;
			previousItemAction.setEnabled(false);
		} else {
			Node logRecord = controller.getLogRecord(root, sequence);
			cmpSource.setTxtLogRecordSource(parser.getFormattedSourceXML(logRecord));
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
