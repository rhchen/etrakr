package net.sf.etrakr.eventbus;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Ref http://www.vogella.com/tutorials/OSGiServices/article.html
 */
public class EventbusActivator implements BundleActivator {

	private static BundleContext context;

	private EventAdmin eventAdmin;
	
	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@SuppressWarnings("unchecked")
	public void start(BundleContext bundleContext) throws Exception {
		
		EventbusActivator.context = bundleContext;
		
		System.out.println("EventbusActivator.start Starting bundle");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		
		EventbusActivator.context = null;
		
		System.out.println("EventbusActivator.stop Stopping bundle");
	}

}
