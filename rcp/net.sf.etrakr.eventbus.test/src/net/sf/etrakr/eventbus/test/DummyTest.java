/**
 * 
 */
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import net.sf.etrakr.eventbus.EventBus;

/**
 * @author admin
 *
 */
public class DummyTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		String str = "";
		Assert.assertNotNull(str);
	}
	
	@Test
	public void test1() {
		
		EventBus.registerEvent(new EventHandler(){

			@Override
			public void handleEvent(Event event) {
				
				System.out.println("DummyTest.EventHandler : "+ event);
			}
			
		}, "org/eclipse/equinox/events/systrace/*");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IEventBroker.DATA, 2); 		
		
		Event event = new Event("org/eclipse/equinox/events/systrace/CRITICAL", map);
		EventBus.getEventBus().postEvent(event);
	}

}
