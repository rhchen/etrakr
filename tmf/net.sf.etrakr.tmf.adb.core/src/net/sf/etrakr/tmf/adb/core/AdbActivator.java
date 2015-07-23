package net.sf.etrakr.tmf.adb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;

public class AdbActivator implements BundleActivator, IDebugBridgeChangeListener, IDeviceChangeListener, IClientChangeListener{

	public static final String DdmsPlugin_DDMS_Post_Create_Init = "DDMS post-create init";
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		
		AdbActivator.context = bundleContext;
		
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
        
		AdbActivator.context = null;
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
