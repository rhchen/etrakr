package net.sf.etrakr.eventbus;

import java.util.Map;

import org.osgi.service.event.Event;

public class TkrEvent extends Event implements ITkrEvent{

	
	public TkrEvent(String topic, Map<String, ?> properties) {
		super(topic, properties);
	}

}
