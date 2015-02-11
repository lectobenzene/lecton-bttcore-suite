package com.tcs.mobility.btt.utilities.constantsmapper;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.tcs.mobility.btt.utilities.constantsmapper"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
//		for(IProject project : projects){
//			if(project.isOpen()){
//				AddRemoveBTTNatureHandler.createNature(project);
//			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public Image createImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry()
					.put(path,
							AbstractUIPlugin.imageDescriptorFromPlugin(
									PLUGIN_ID, path));
			image = getImageRegistry().get(path);
		}
		return image;
	}
	
}
