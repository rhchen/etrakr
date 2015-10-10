package ncsa.hdf.object;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class HdfActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		HdfActivator.context = bundleContext;
		System.out.println("HdfActivator.start");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		HdfActivator.context = null;
	}

}
