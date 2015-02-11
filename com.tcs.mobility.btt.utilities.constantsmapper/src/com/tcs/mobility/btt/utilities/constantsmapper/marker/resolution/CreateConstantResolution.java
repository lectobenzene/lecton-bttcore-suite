package com.tcs.mobility.btt.utilities.constantsmapper.marker.resolution;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;
import com.tcs.mobility.btt.utilities.constantsmapper.Activator;

public class CreateConstantResolution implements IMarkerResolution2 {

	private String className;
	private String classPath;
	private IFile file;
	
	public CreateConstantResolution(IFile file) {
		this.file = file;
		this.className = file.getName();
		this.classPath = file.getFullPath().toOSString();
	}

	@Override
	public String getLabel() {
		return "Create Constant in " + className + "("+classPath+")";
	}

	@Override
	public void run(IMarker marker) {
		try {
			String filePath = (String) marker.getAttribute("FILE_PATH");
			IFile javaFile = (IFile) ResourcesUtil.getResource(filePath);
			ICompilationUnit compUnit = JavaCore.createCompilationUnitFrom(javaFile);
			
			ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
			String typeName = cu.getElementName().substring(0, cu.getElementName().length() - 5);
			IType type = cu.getType(typeName);
			String variableValue = (String) marker.getAttribute(IMarker.TEXT);
			String variableName = returnVariableName(variableValue);
			variableName = createConstant(type, variableName, variableValue);

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

	private String createConstant(IType type, String variableName, String variableValue)
			throws JavaModelException {
		int count = 0;
		while (checkExistenceOfField(type, variableName, count)) {
			count++;
		}
		if (count != 0) {
			variableName = variableName + "_" + count;
		}
		type.createField("String " + variableName + " = \"" + variableValue + "\";", null, true,
				null);
		return variableName;
	}

	private boolean checkExistenceOfField(IType type, String variableName, int count) {
		if (count != 0) {
			variableName = variableName + "_" + count;
		}
		IField field = type.getField(variableName);
		if (field.exists()) {
			return true;
		}
		return false;
	}

	private String returnVariableName(String variableValue) {
		String variableName = variableValue.toUpperCase();
		variableName = variableName.replaceAll("[^\\w\\d]", "_");
		return variableName;
	}

	@Override
	public String getDescription() {
		return "Creates a constant in " + className + " and refers to that";
	}

	@Override
	public Image getImage() {
		return Activator.getDefault().createImage("images/create_constant.png");
	}

}
