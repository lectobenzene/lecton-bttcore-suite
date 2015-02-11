package com.tcs.mobility.btt.btteditor.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import com.tcs.mobility.btt.btteditor.editor.pages.context.ContextComposite;
import com.tcs.mobility.btt.btteditor.editor.pages.processor.ProcessorComposite;
import com.tcs.mobility.btt.core.source.models.context.ContextModel;
import com.tcs.mobility.btt.core.source.models.processor.ProcessorModel;
import com.tcs.mobility.btt.core.source.models.utils.IWatchableListListener;
import com.tcs.mobility.btt.core.source.parsers.ServiceParser;

public class BTTEditor extends MultiPageEditorPart implements PropertyChangeListener,
		IWatchableListListener {

	private StructuredTextEditor sourceTextEditor;
	private ProcessorComposite processorComposite;
	private ContextComposite contextComposite;

	private boolean isModified = false;
	
	private boolean isProcessorUptoDate = false;
	private boolean isContextUptoDate = false;
	
	public static ProcessorModel processorModel;
	public static ContextModel contextModel;

	private ServiceParser serviceParser;

	@Override
	protected void createPages() {
		serviceParser = new ServiceParser();
		createSourcePage();
		updateAllModels();
		createProcessorPage();
		createContextPage();
		initProcesserPage();
		initContextPage();
		updateTitle();
		isModified = false;
	}

	private void initProcesserPage() {
		processorComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateProcessorPage();
			}
		});
	}

	private void initContextPage() {
		contextComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateContextPage();
			}
		});
	}

	private void createProcessorPage() {
		processorComposite = new ProcessorComposite(getContainer(), SWT.NONE, processorModel, this);
		int index = addPage(processorComposite);
		setPageText(index, "Processor");
	}

	private void createContextPage() {
		contextComposite = new ContextComposite(getContainer(), SWT.NONE, contextModel, this);
		int index = addPage(contextComposite);
		setPageText(index, "Context");
	}

	@Override
	protected void pageChange(int newPageIndex) {
		System.out.println("pageChange isModified= " + isModified);
		switch (newPageIndex) {
		case 0:
			if (isModified) {
				updateSourcePage();
				isModified = false;
			}
			break;
		case 1:
			// Remove this condition in the future
			if (isDirty()) {
				updateProcessorPage();
			}
			if(!isProcessorUptoDate){
				processorComposite.setInput(processorModel, this);
			}
			break;

		case 2:
			// Remove this condition in the future
			if (isDirty()) {
				updateContextPage();
			}
			if(!isContextUptoDate){
				contextComposite.setInput(contextModel, this);
			}
			break;
		}
		super.pageChange(newPageIndex);
	}

	
	private void updateAllModels(){
		String sourceContent = sourceTextEditor.getDocumentProvider()
				.getDocument(sourceTextEditor.getEditorInput()).get();
		serviceParser.parse(sourceContent);
		processorModel = serviceParser.getProcessorModel();
		contextModel = serviceParser.getContextModel();
		
		isProcessorUptoDate = false;
		isContextUptoDate = false;
	}
	
	private void updateProcessorPage() {
		String sourceContent = sourceTextEditor.getDocumentProvider()
				.getDocument(sourceTextEditor.getEditorInput()).get();
		serviceParser.parse(sourceContent);
		processorModel = serviceParser.getProcessorModel();
		if (processorComposite != null) {
			processorComposite.setInput(processorModel, this);
		}
	}

	private void updateContextPage() {
		String sourceContent = sourceTextEditor.getDocumentProvider()
				.getDocument(sourceTextEditor.getEditorInput()).get();
		serviceParser.parse(sourceContent);
		contextModel = serviceParser.getContextModel();
		if (contextComposite != null) {
			contextComposite.setInput(contextModel, this);
		}
	}

	void updateSourcePage() {
		processorModel = processorComposite.getOutput(this);
		contextModel = contextComposite.getOutput(this);
		serviceParser.setProcessorModel(processorModel);
		serviceParser.setContextModel(contextModel);
		String build = (String) serviceParser.build();
		sourceTextEditor.getDocumentProvider().getDocument(sourceTextEditor.getEditorInput())
				.set(build);
	}

	public void setFocus() {
		switch (getActivePage()) {
		case 0:
			sourceTextEditor.setFocus();
			break;
		case 1:
			processorComposite.getTxtId().setFocus();
			break;
		}
	}

	@Override
	protected IEditorSite createSite(IEditorPart editor) {
		// TODO Auto-generated method stub
		return super.createSite(editor);
	}

	private void createSourcePage() {
		try {
			sourceTextEditor = new StructuredTextEditor();
			int index = addPage(sourceTextEditor, getEditorInput());
			setPageText(index, "Source");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		/*
		 * Modified. Trying to figure out why this is not called in certain
		 * scenarios
		 */
		if (getActivePage() != 0 && isModified) {
			updateSourcePage();
		}
		/*
		 * If saved from source Page with some modifications. Then ProcessorPage
		 * should be updated with these changes.
		 */
		updateAllModels();
		isModified = false;
		sourceTextEditor.doSave(monitor);
	}

	@Override
	public boolean isDirty() {
		return (isModified || super.isDirty());
	}

	@Override
	public void doSaveAs() {
		if (getActivePage() != 0 && isModified) {
			updateSourcePage();
			isModified = false;
		}
		sourceTextEditor.doSaveAs();
		setInput(sourceTextEditor.getEditorInput());
		updateTitle();
	}

	private void updateTitle() {
		IEditorInput input = getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, input);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		isModified = true;
		firePropertyChange(PROP_DIRTY);
		if (!isDirty()) {
			// using this causes problems.
			// Which is the best way? need time to figure out
		}
	}

	@Override
	public void elementAdded(Object element) {
		System.out.println("Element ADDED - ");
	}

	@Override
	public void elementRemoved(Object element) {
		System.out.println("Element REMOVED - ");
	}
}
