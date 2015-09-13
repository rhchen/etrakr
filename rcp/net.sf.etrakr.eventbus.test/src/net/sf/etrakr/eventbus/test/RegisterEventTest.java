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
import net.sf.etrakr.eventbus.ITkrEvent;
import net.sf.etrakr.eventbus.TkrEvent;
import net.sf.etrakr.eventbus.TkrEventException;

/**
 * @author admin
 *
 */
public class RegisterEventTest {

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
		
		Thread.sleep(1000);
	}

	@Test
	public void test1() throws TkrEventException {
		
		final int[] value = new int[]{1, 2, 3};
		
		String SUBSCRIBE_ALL = ITkrEvent.TOPIC_ETRAKR + "/*";
		
		EventBus.registerEvent(new EventHandler(){

			@Override
			public void handleEvent(Event event) {
				
				String topic = event.getTopic();
				
				String data = TkrEvent.getDataByKey(topic);
				
				int[] m = (int[]) event.getProperty(data);
				
				Assert.assertArrayEquals(value, m);
			}
			
		}, SUBSCRIBE_ALL);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ITkrEvent.TOPIC_ETRAKR_COMMAND_DATA_KEY, value); 		
		
//		TkrEvent event = new TkrEvent(ITkrEvent.TOPIC_ETRAKR_COMMAND, map);
//		EventBus.getEventBus().postEvent(event);
		
		Event event = TkrEvent.newEvent().topic(ITkrEvent.TOPIC_ETRAKR_COMMAND).message(map).build();
		
		EventBus.getEventBus().postEvent(event);
	}

}
