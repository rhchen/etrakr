package net.sf.etrakr.eventbus;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;

public class TkrEvent extends Event implements ITkrEvent{

	Map<String, String> map;
	
	public TkrEvent(String topic, Map<String, ?> properties) {
		super(topic, properties);
	}
	
	public String getDataKey(String topic){
		
		if(map == null){
			
			map = new HashMap<String, String>();
			
			map.put(TOPIC_ROOT, TOPIC_ROOT_DATA_KEY);
			map.put(TOPIC_ETRAKR, TOPIC_ETRAKR_DATA_KEY);
			map.put(TOPIC_ETRAKR_COMMAND, TOPIC_ETRAKR_COMMAND_DATA_KEY);
			map.put(TOPIC_ETRAKR_COMMAND_OPEN_TRACE, TOPIC_ETRAKR_COMMAND_OPEN_TRACE_DATA_KEY);
			
		}
		
		return map.get(topic);
	}
}