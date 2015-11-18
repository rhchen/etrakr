package net.sf.etrakr.persistent.hdf;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ncsa.hdf.hdf5lib.HdfDLLLoader;

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
		
		/* Load platform depend dll */
		HdfDLLLoader.loadDLL();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		HdfActivator.context = null;
	}

}
