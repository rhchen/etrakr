package net.sf.etrakr.eventbus.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;

import net.sf.etrakr.eventbus.EventbusActivator;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Ref http://codeandme.blogspot.tw/2013/08/using-event-broker-without-di.html
 */
public class EventBrokerTest {

	
	BundleContext context;
	
	private static int mCounter = 0;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		context = EventbusActivator.getContext();
		
		Assert.assertNotNull(context);
	}

	@After
	public void tearDown() throws Exception {
		
		Thread.sleep(1000);
	}


	@Ignore
	public void test() {

		
		new Subscriber();
		
		@SuppressWarnings("rawtypes")
		ServiceReference service = context.getServiceReference(IEventBroker.class.getName());

		//Object service2 = PlatformUI.getWorkbench().getService(IEventBroker.class);
		
		Assert.assertNotNull(service);

		if (service instanceof IEventBroker) {
			((IEventBroker) service).post("com/example/eventbroker/basic", new Object());

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("Event origin", "unknown");
			data.put("Event number", mCounter++);
			((IEventBroker) service).post("com/example/eventbroker/advanced", data);

		}
	}
	
	public class Subscriber implements EventHandler {

		public Subscriber() {
			@SuppressWarnings("rawtypes")
			ServiceReference service = context.getServiceReference(IEventBroker.class.getName());
			if (service instanceof IEventBroker)
				((IEventBroker) service).subscribe("com/example/eventbroker/*", this);
		}

		@Override
		public void handleEvent(Event e) {
			System.out.println("-------------------------------------------");
			System.out.println(e.getTopic());
			for (String name : e.getPropertyNames())
				System.out.println("\t" + name + ": " + e.getProperty(name));
		}
	}

}
