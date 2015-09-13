package net.sf.etrakr.eventbus;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

public class EventBus {

	/**
	 * The eventBus.
	 */
	private static EventAdmin eventBus;

	/**
	 * @return
	 */
	public static EventAdmin getEventBus() {
		return eventBus;
	}
	
	

	public static void registerEvent(EventHandler handler, String topic) {

		String[] topics = new String[] { topic };
		
		Hashtable<String, String[]> ht = new Hashtable<String, String[]>();
		
		ht.put(EventConstants.EVENT_TOPIC, topics);
		
		EventbusActivator.getContext().registerService(EventHandler.class.getName(), handler, ht);

	}
	/**
	 * Method will be used by DS to set the
	 * <code>org.osgi.service.event.EventAdmin</code> service.
	 * 
	 * @param eventBus
	 */
	public synchronized void setEventBus(EventAdmin eventBus) {
		EventBus.eventBus = eventBus;
		System.out.println("EventBus.EventAdmin service is set!"); //$NON-NLS-1$
	}

	/**
	 * Method will be used by DS to unset the
	 * <code>org.osgi.service.event.EventAdmin</code> service.
	 * 
	 * @param eventBus
	 */
	public synchronized void unsetEventBus(EventAdmin eventBus) {
		if (EventBus.eventBus == eventBus) {
			EventBus.eventBus = null;
		}
		System.out.println("EventBus.EventAdmin service is unset!"); //$NON-NLS-1$
	}
}
