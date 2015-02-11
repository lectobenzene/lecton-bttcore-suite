package com.tcs.mobility.btt.core.utils.resources;

import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class ResourcesUtil {

	/**
	 * used by method {@link #isFileAvailable(String, String) isFileAvailable}.
	 * Do not use elsewhere
	 */
	private static String fileLocationWOL527;

	/**
	 * Constructor
	 */
	public ResourcesUtil() {
	}

	public static void main(String[] args) {
		System.out.println(getSplitBeforePath(
				"/TFAL_V2.00_N.war/WebCent/WEBINF/asdf/adsfdd/server/dbal/zoomit", "server"));
	}

	/**
	 * Searches a particular folder at full depth for the mentioned file and
	 * returns the location of the file if found.
	 * 
	 * @param location
	 *            The path to find the file
	 * @param fileName
	 *            The name of the file to be found
	 * @return the location of the file if found
	 */
	public static String isFileAvailable(final String location, final String fileName) {
		fileLocationWOL527 = null;

		IResource resource = getResource(location);

		if (resource != null && (resource.getType() & (IResource.PROJECT | IResource.FOLDER)) != 0) {

			try {
				resource.getProject().accept(new IResourceProxyVisitor() {

					@Override
					public boolean visit(IResourceProxy proxy) throws CoreException {
						if (fileName.equals(proxy.getName())) {
							System.out.println("FOUND : " + proxy.getName());
							fileLocationWOL527 = proxy.requestFullPath().toOSString();
							return false;
						}
						return true;
					}
				}, 0);

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return fileLocationWOL527;
	}

	/**
	 * Returns a {@code IResource} object for the given path
	 * 
	 * @param path
	 *            The location for which the resource is claimed
	 * @return The resource pointing to the path
	 */
	public static IResource getResource(String path) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
	}

	/**
	 * Returns a map of resources in a given location whose name ends with the
	 * provided filter string
	 * 
	 * @param path
	 *            the location for which the resources are claimed
	 * @param fileExtension
	 *            the filter criteria for getting the resource
	 * @return a map of resources
	 */
	public static HashMap<String, IResource> getResources(final String path,
			final String fileExtension) {
		final IResource resource = getResource(path);
		final HashMap<String, IResource> resources = new HashMap<String, IResource>();

		if (resource != null && (resource.getType() & (IResource.PROJECT | IResource.FOLDER)) != 0) {

			try {
				resource.getProject().accept(new IResourceVisitor() {

					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource.getName().endsWith(fileExtension)) {
							System.out.println(resource.getName());
							resources.put(resource.getName(), resource);
						}
						return true;
					}
				});

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return resources;
	}

	public static String getSplitAfterPath(String location, String pathSegment) {
		IPath path = new Path(location);
		int count = path.segmentCount();
		for (int i = 0; i < count; i++) {
			if ((new Path(pathSegment)).isPrefixOf(path)) {
				path = path.removeFirstSegments(1);
				break;
			}
			path = path.removeFirstSegments(1);
		}
		return path.toOSString();
	}

	public static String getSplitBeforePath(String location, String pathSegment) {
		IPath path = new Path(location);
		int count = path.segmentCount();
		int newCount = 0;
		for (int i = 0; i < count; i++) {
			if ((new Path(pathSegment)).isPrefixOf(path)) {
				newCount = path.segmentCount();
				break;
			}
			path = path.removeFirstSegments(1);
		}
		return (new Path(location)).removeFirstSegments(count - newCount - 1).toOSString();
	}

	/**
	 * Creates a chain of folders in the given folder
	 * 
	 * @param parentFolder
	 *            The Folder in which new folders are to be created
	 * @param path
	 *            The path of the folder to be created
	 * @param monitor
	 *            the Progress Monitor object
	 */
	public static IFolder createFolders(IFolder parentFolder, Path path, IProgressMonitor monitor)
			throws CoreException {
		if (!parentFolder.exists()) {
			parentFolder.create(true, true, monitor);
		}
		String[] pathSegments = path.segments();
		IFolder tempFolder = parentFolder;
		for (String pathSegment : pathSegments) {
			tempFolder = tempFolder.getFolder(pathSegment);
			if (!tempFolder.exists()) {
				tempFolder.create(true, true, monitor);
			}
		}
		return tempFolder;
	}
}