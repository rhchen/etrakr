package net.sf.etrakr.remote.adb.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;

import net.sf.etrakr.remote.adb.core.adb.Adb;
import net.sf.etrakr.remote.adb.core.internal.AdbService;
import net.sf.etrakr.remote.adb.core.internal.IAdbService;

public class AdbPlugin extends Plugin
		implements BundleActivator, IDebugBridgeChangeListener, IDeviceChangeListener, IClientChangeListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.etrakr.remote.adb.core"; //$NON-NLS-1$

	public static final String DdmsPlugin_DDMS_Post_Create_Init = "DDMS post-create init";

	// The shared instance
	private static AdbPlugin plugin;

	private IAdbService fAdbService;

	@SuppressWarnings("rawtypes")
	private ServiceTracker tracker;

	@SuppressWarnings("rawtypes")
	private ServiceRegistration adbService;

	private Adb adb;

	private AndroidDebugBridge androidDebugBridge;
	
	private Job adbBridgeJob;
	
	private List<IDevice> devices = new ArrayList<IDevice>();
	
	public List<IDevice> getDevices() {
		return devices;
	}

	public AndroidDebugBridge getAndroidDebugBridge() {
		return androidDebugBridge;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AdbPlugin getDefault() {
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
	 * Return the OSGi service with the given service interface.
	 * 
	 * @param service
	 *            service interface
	 * @return the specified service or null if it's not registered
	 */
	public static <T> T getService(Class<T> service) {
		BundleContext context = plugin.getBundle().getBundleContext();
		ServiceReference<T> ref = context.getServiceReference(service);
		return ref != null ? context.getService(ref) : null;
	}

	public IAdbService getService() {
		return fAdbService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext )
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		/* Regist service */
		tracker = new ServiceTracker(getBundle().getBundleContext(), IProxyService.class.getName(), null);
		tracker.open();
		adbService = getBundle().getBundleContext().registerService(IAdbService.class.getName(),
				AdbService.getInstance(), new Hashtable());

		/* get service */
		ServiceReference<IAdbService> reference = context.getServiceReference(IAdbService.class);
		fAdbService = context.getService(reference);

		/* Initial adb regist */
		System.out.println("AdbActivator.start : DDMS post-create init");

		AndroidDebugBridge.addDebugBridgeChangeListener(this);
		AndroidDebugBridge.addDeviceChangeListener(this);
		AndroidDebugBridge.addClientChangeListener(this);

		Job adbBridgeJob = new Job(DdmsPlugin_DDMS_Post_Create_Init) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				// init the lib
				AndroidDebugBridge.init(false /* debugger support */);

				androidDebugBridge = AndroidDebugBridge.createBridge("adb", false /* forceNewBridge */);
				
				System.out.println("AdbActivator.start : DDMS post-create done");
				
				return Status.OK_STATUS;
			}

		};
		
		adbBridgeJob.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		
		if(adbBridgeJob != null){
			
			AndroidDebugBridge.disconnectBridge();
			
			AndroidDebugBridge.terminate();
			
			adbBridgeJob.cancel();
			
		}//if
		
		plugin = null;
		super.stop(context);
	}

	public synchronized Adb getAdb() {
		
		if (adb == null) {
			adb = new Adb();
		}
		return adb;
	}

	@Override
	public void clientChanged(Client client, int changeMask) {
		System.out.println("AdbActivator.clientChanged");

	}

	@Override
	public void deviceConnected(IDevice device) {
		System.out.println("AdbActivator.deviceConnected");
		devices.add(device);
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		System.out.println("AdbActivator.deviceDisconnected");
		devices.remove(device);
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
