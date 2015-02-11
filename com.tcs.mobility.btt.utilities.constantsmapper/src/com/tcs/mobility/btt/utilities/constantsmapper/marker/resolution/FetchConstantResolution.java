package com.tcs.mobility.btt.utilities.constantsmapper.marker.resolution;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;
import com.tcs.mobility.btt.utilities.constantsmapper.Activator;

public class FetchConstantResolution implements IMarkerResolution2 {

	private String className;
	private String classPath;
	private IFile file;
	private String variableName;
	
	public FetchConstantResolution(IFile file, String variableName) {
		this.file = file;
		this.className = file.getName();
		this.classPath = file.getFullPath().toOSString();
		this.variableName = variableName;
	}

	@Override
	public String getLabel() {
		return "Fetch Constant "+variableName+" from " + className;
	}

	
	@Override
	public void run(IMarker marker) {
		try {
			String filePath = (String) marker.getAttribute("FILE_PATH");
			IFile javaFile = (IFile) ResourcesUtil.getResource(filePath);
			ICompilationUnit compUnit = JavaCore.createCompilationUnitFrom(javaFile);
			
			ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
			String typeName = cu.getElementName().substring(0, cu.getElementName().length() - 5);
			String qualifiedVariableName = typeName+"."+variableName;
			
			int startPosition = (Integer) marker.getAttribute(IMarker.CHAR_START);
			int endPosition = (Integer) marker.getAttribute(IMarker.CHAR_END);
			
			
			
			String originalCode = compUnit.getBuffer().getContents();
			StringBuilder modifiedCode = new StringBuilder(originalCode);
			modifiedCode.replace(startPosition, endPosition, qualifiedVariableName);
			compUnit.getBuffer().setContents(modifiedCode.toString());
			compUnit.save(null, true);
			
			// Save the file so that other markers are visible
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = page.getActiveEditor();
			page.saveEditor(editor, false);
			
			// Move the Caret back to the original position
			ISelectionProvider selectionProvider = editor.getEditorSite().getSelectionProvider();
			selectionProvider.setSelection(new TextSelection(startPosition, 0));
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "Fetches the constant "+ variableName+" from " + className +"("+classPath+")"+" and refers to that";
	}

	@Override
	public Image getImage() {
		return Activator.getDefault().createImage("images/fetch_constant.png");
	}

}
