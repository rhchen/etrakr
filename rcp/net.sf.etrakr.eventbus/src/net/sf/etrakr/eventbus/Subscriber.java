package net.sf.etrakr.eventbus;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class Subscriber implements EventHandler{

	/* 
     * (non-Javadoc)
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
	@Override
	public void handleEvent(Event event) {
		
		System.out.println("Subscriber.handleEvent ");
		
		String topic = event.getTopic();
		
		String data = TkrEvent.getDataByKey(topic);
		
		Object o =event.getProperty(data);
		
		System.out.println("Subscriber.handleEvent topic : "+ topic +" ,data key : "+ data);
	}

}
