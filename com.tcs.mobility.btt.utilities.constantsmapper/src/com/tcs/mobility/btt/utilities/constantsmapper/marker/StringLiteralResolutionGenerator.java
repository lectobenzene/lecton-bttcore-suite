package com.tcs.mobility.btt.utilities.constantsmapper.marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import com.tcs.mobility.btt.core.utils.resources.ResourcesUtil;
import com.tcs.mobility.btt.utilities.constantsmapper.builder.BTTBuilder;
import com.tcs.mobility.btt.utilities.constantsmapper.marker.resolution.CreateConstantResolution;
import com.tcs.mobility.btt.utilities.constantsmapper.marker.resolution.FetchConstantResolution;

public class StringLiteralResolutionGenerator implements IMarkerResolutionGenerator2 {

	private String text;

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		final List<IMarkerResolution2> resolutions = new ArrayList<IMarkerResolution2>();
		String projectPath = null;
		try {
			projectPath = (String) marker.getAttribute("PROJECT_PATH");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (!BTTBuilder.isConstantsFilesReceived) {
			HashMap<String, IResource> resources = ResourcesUtil.getResources(projectPath, "Constants.java");
			for (String fileName : resources.keySet()) {
				IResource iResource = resources.get(fileName);
				if (!BTTBuilder.isFound((IFile) iResource)) {
					BTTBuilder.constantsFiles.add((IFile) iResource);
				}
			}
			BTTBuilder.isConstantsFilesReceived = true;
		}
		
		
		for(final IFile file : BTTBuilder.constantsFiles){
			ICompilationUnit fileUnit = JavaCore.createCompilationUnitFrom(file);
			try {
				text = (String) marker.getAttribute(IMarker.TEXT);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(fileUnit);
			parser.setStatementsRecovery(true);
			ASTNode node = parser.createAST(null);
			
			if (node instanceof CompilationUnit) {
				final CompilationUnit cuNode = (CompilationUnit) node;
				cuNode.accept(new ASTVisitor() {
					@Override
					public boolean visit(StringLiteral node) {
						if(node.getLiteralValue().equals(text)){
							ASTNode parent = node.getParent();
							if(parent != null && parent.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT){
								VariableDeclarationFragment fragment = (VariableDeclarationFragment) parent;
								SimpleName name = fragment.getName();
								resolutions.add(new FetchConstantResolution(file, name.getFullyQualifiedName()));
								return false;
							}
						}
						return true;
					}
				});
			}
		}
		for (IFile file : BTTBuilder.constantsFiles) {
			resolutions.add(new CreateConstantResolution(file));
		}

		return (IMarkerResolution[]) resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

	@Override
	public boolean hasResolutions(IMarker marker) {
		return true;
	}

}
