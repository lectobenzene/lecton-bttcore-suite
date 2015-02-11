package com.tcs.mobility.btt.utilities.constantsmapper.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;

public class BTTBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "btt.builder";
	public static final String MARKER_ID = "btt.marker.stringliteral";
	public static List<IFile> constantsFiles;
	public static boolean isConstantsFilesReceived = false;
	
	public BTTBuilder() {
		constantsFiles = new ArrayList<IFile>();
	}
	
	class JavaDeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				if(resource instanceof IFile && resource.getName().endsWith("Constants.java")){
					if(!isFound((IFile)resource)){
						constantsFiles.add((IFile) resource);
					}
				}
				checkJava(resource);
				break;
			case IResourceDelta.REMOVED:
				if(resource instanceof IFile && resource.getName().endsWith("Constants.java")){
					constantsFiles.remove((IFile)resource);
				}
				break;
			case IResourceDelta.CHANGED:
				checkJava(resource);
				break;
			}
			return true;
		}

	}

	class JavaResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) throws JavaModelException {
			checkJava(resource);
			return true;
		}
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}
	

	public static boolean isFound(IFile file) {
		String filePath = file.getFullPath().toOSString();
		for(IFile iFile : constantsFiles){
			if(filePath.equalsIgnoreCase(iFile.getFullPath().toOSString())){
				return true;
			}
		}
		return false;
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		getProject().accept(new JavaResourceVisitor());
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
			throws CoreException {
		delta.accept(new JavaDeltaVisitor());
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_ID, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	private void checkJava(IResource resource) throws JavaModelException {
		if (resource instanceof IFile && resource.getName().endsWith(".java")) {
			final IFile file = (IFile) resource;
			deleteMarkers(file);
			final ICompilationUnit javaFile = JavaCore.createCompilationUnitFrom(file);

			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setResolveBindings(true);
			parser.setSource(javaFile);
			parser.setStatementsRecovery(true);
			parser.setBindingsRecovery(true);
			ASTNode node = parser.createAST(null);
			if (node instanceof CompilationUnit) {
				final CompilationUnit cuNode = (CompilationUnit) node;
				cuNode.accept(new ASTVisitor() {

					@Override
					public boolean visit(StringLiteral node) {
						System.out.println("JAVA FILE NAME = "+javaFile.getElementName());
						ASTNode parent = node.getParent();
						if(parent != null && parent.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT){
							// Do not MARK Variable Declaration fields
							return false;
						}
						String literalValue = node.getLiteralValue();
						int startPosition = node.getStartPosition();
						int lineNumber = cuNode.getLineNumber(startPosition);
						int length = node.getLength();
						addMarker(javaFile, file, literalValue, startPosition, startPosition
								+ length, lineNumber);
						return super.visit(node);
					}
				});
			}
		}
	}

	private void addMarker(ICompilationUnit javaFile, IFile file, String text, int start, int end, int lineNumber) {
		try {
			IMarker marker = file.createMarker(MARKER_ID);
			System.out.println("PRE SET attribute");

			marker.setAttribute("FILE_PATH", javaFile.getResource().getFullPath().toOSString());
			marker.setAttribute("PROJECT_PATH", javaFile.getJavaProject().getResource().getFullPath().toOSString());
			
			marker.setAttribute(IMarker.TEXT, text);
			marker.setAttribute(IMarker.MESSAGE, "String Literal Not allowed");
			marker.setAttribute(IMarker.SEVERITY,  IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}
}
