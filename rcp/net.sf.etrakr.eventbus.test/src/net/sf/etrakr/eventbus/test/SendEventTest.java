package net.sf.etrakr.eventbus.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import net.sf.etrakr.eventbus.EventBus;
import net.sf.etrakr.eventbus.EventbusActivator;
import net.sf.etrakr.eventbus.ITkrEvent;

public class SendEventTest {

	BundleContext context;
	
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

	@Test
	public void test1() {
		
		ServiceReference reference = context.getServiceReference(EventAdmin.class.getName());
		
		Assert.assertNotNull(reference);
		
		EventAdmin eventAdmin = (EventAdmin) context.getService(reference);
		
		Assert.assertNotNull(eventAdmin);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IEventBroker.DATA, 1); 		
		
		Event event = new Event(ITkrEvent.TOPIC_ETRAKR_COMMAND, map);
		eventAdmin.sendEvent(event);
		
		System.out.println("SendEventTest.test1 done");
		
	}
	
	@Test
	public void test2() {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IEventBroker.DATA, 2); 		
		
		Event event = new Event(ITkrEvent.TOPIC_ETRAKR_COMMAND, map);
		EventBus.getEventBus().sendEvent(event);
		
		System.out.println("SendEventTest.test2 done");
	}

}
