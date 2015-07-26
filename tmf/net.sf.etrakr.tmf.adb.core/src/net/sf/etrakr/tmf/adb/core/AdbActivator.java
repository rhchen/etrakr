package net.sf.etrakr.tmf.adb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;

import net.sf.etrakr.tmf.adb.core.internal.IJSchService;

public class AdbActivator extends Plugin implements BundleActivator, IDebugBridgeChangeListener, IDeviceChangeListener, IClientChangeListener{

	public static final String PLUGIN_ID = "net.sf.etrakr.tmf.adb.core"; //$NON-NLS-1$
	
	public static final String DdmsPlugin_DDMS_Post_Create_Init = "DDMS post-create init";
	
	// The shared instance
	private static AdbActivator plugin;

	private IJSchService fJSchService;
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AdbActivator getDefault() {
		return plugin;
	}

	/**
	 * Get unique identifier
	 * 
	 * @return
	 * @since 5.0
	 */
	public static String getUniqueIdentifier() {
		if (getDefault() == null) {
			return PLUGIN_ID;
		}
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message
	 *            the error message to log
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, message, null));
	}

	/**
	 * Logs an internal error with the specified throwable
	 * 
	 * @param e
	 *            the exception to be logged
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, e.getMessage(), e));
	}

	/**
	 * Convenience method for logging CoreExceptions to the plugin log
	 * 
	 * @param e
	 *            the exception
	 */
	public static void log(CoreException e) {
		log(e.getStatus().getSeverity(), e.getMessage(), e);
	}

	public static void log(int severity, String message, Throwable e) {
		log(new Status(severity, PLUGIN_ID, 0, message, e));
	}
	
	/**
	 * Return the OSGi service with the given service interface.
	 * 
	 * @param service service interface
	 * @return the specified service or null if it's not registered
	 */
	public static <T> T getService(Class<T> service) {
		BundleContext context = plugin.getBundle().getBundleContext();
		ServiceReference<T> ref = context.getServiceReference(service);
		return ref != null ? context.getService(ref) : null;
	}
	
	public IJSchService getService() {
		return fJSchService;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		
		super.start(bundleContext);
		
		plugin = this;
		
		ServiceReference<IJSchService> reference = bundleContext.getServiceReference(IJSchService.class);
		fJSchService = bundleContext.getService(reference);
		
		System.out.println("AdbActivator.start : DDMS post-create init");
		
		AndroidDebugBridge.addDebugBridgeChangeListener(this);
        AndroidDebugBridge.addDeviceChangeListener(this);
        AndroidDebugBridge.addClientChangeListener(this);
        
        new Job(DdmsPlugin_DDMS_Post_Create_Init) {
        	
            @Override
            protected IStatus run(IProgressMonitor monitor) {
            	
            	// init the lib
                AndroidDebugBridge.init(false /* debugger support */);

                AndroidDebugBridge.createBridge("adb", false /* forceNewBridge */);

                return Status.OK_STATUS;
            }
            
        }.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		
		AndroidDebugBridge.removeDeviceChangeListener(this);

        AndroidDebugBridge.terminate();
        
		plugin = null;
		
		super.stop(bundleContext);
	}

	@Override
	public void clientChanged(Client client, int changeMask) {
		System.out.println("AdbActivator.clientChanged");
		
	}

	@Override
	public void deviceConnected(IDevice device) {
		System.out.println("AdbActivator.deviceConnected");
		
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		System.out.println("AdbActivator.deviceDisconnected");
		
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		System.out.println("AdbActivator.deviceChanged");
		
	}

	@Override
	public void bridgeChanged(AndroidDebugBridge bridge) {
		System.out.println("AdbActivator.bridgeChanged");
		
	}

}
