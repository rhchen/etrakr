package net.sf.etrakr.eventbus;

import org.osgi.service.event.EventAdmin;

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
