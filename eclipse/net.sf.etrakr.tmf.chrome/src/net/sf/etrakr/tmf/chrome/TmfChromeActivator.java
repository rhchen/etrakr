package net.sf.etrakr.tmf.chrome;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class TmfChromeActivator extends Plugin {

		// The plug-in ID
		public static final String PLUGIN_ID = "net.sf.etrakr.tmf.chrome"; //$NON-NLS-1$
			
		private static BundleContext context;

		// The shared instance
		private static TmfChromeActivator plugin;
			
		static BundleContext getContext() {
			return context;
		}

		/*
		 * (non-Javadoc)
		 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
		 */
		public void start(BundleContext bundleContext) throws Exception {
			TmfChromeActivator.context = bundleContext;
			plugin = this;
		}

		/*
		 * (non-Javadoc)
		 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
		 */
		public void stop(BundleContext bundleContext) throws Exception {
			plugin = null;
			TmfChromeActivator.context = null;
		}

		/**
		 * Returns the shared instance
		 *
		 * @return the shared instance
		 */
		public static TmfChromeActivator getDefault() {
			return plugin;
		}
		
		/**
	     * Logs a message with severity INFO in the runtime log of the plug-in.
	     *
	     * @param message A message to log
	     */
	    public void logInfo(String message) {
	        getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
	    }

	    /**
	     * Logs a message and exception with severity INFO in the runtime log of the plug-in.
	     *
	     * @param message A message to log
	     * @param exception A exception to log
	     */
	    public void logInfo(String message, Throwable exception) {
	        getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message, exception));
	    }

	    /**
	     * Logs a message and exception with severity WARNING in the runtime log of the plug-in.
	     *
	     * @param message A message to log
	     */
	    public void logWarning(String message) {
	        getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message));
	    }

	    /**
	     * Logs a message and exception with severity WARNING in the runtime log of the plug-in.
	     *
	     * @param message A message to log
	     * @param exception A exception to log
	     */
	    public void logWarning(String message, Throwable exception) {
	        getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, exception));
	    }

	    /**
	     * Logs a message and exception with severity ERROR in the runtime log of the plug-in.
	     *
	     * @param message A message to log
	     */
	    public void logError(String message) {
	        getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
	    }

	    /**
	     * Logs a message and exception with severity ERROR in the runtime log of the plug-in.
	     *
	     * @param message A message to log
	     * @param exception A exception to log
	     */
	    public void logError(String message, Throwable exception) {
	        getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
	    }

}
