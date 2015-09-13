package net.sf.etrakr.eventbus;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TkrEvent implements ITkrEvent{

	protected String topic;
	protected Object message;
	
	private static BiMap<String, String> map;
	
	private static void initMap(){
		
		if(map == null){
			
			map = HashBiMap.<String, String>create();
			
			map.put(TOPIC_ROOT, TOPIC_ROOT_DATA_KEY);
			map.put(TOPIC_ETRAKR, TOPIC_ETRAKR_DATA_KEY);
			map.put(TOPIC_ETRAKR_COMMAND, TOPIC_ETRAKR_COMMAND_DATA_KEY);
			map.put(TOPIC_ETRAKR_COMMAND_OPEN_TRACE, TOPIC_ETRAKR_COMMAND_OPEN_TRACE_DATA_KEY);
			
		}

	}
	
	public static String getDataByKey(String topic){
		
		initMap();
		
		return map.get(topic);
	}
	
	public static String getKeyByData(String data){

		initMap();
		
		return map.inverse().get(data);
	}
	
	public static TkrEvent newEvent(){
		
		TkrEvent event = new TkrEvent();
		
		return event;
	}
	
	public TkrEvent topic(String topic){
		
		this.topic = topic;
		
		return this;
	}
	
	public TkrEvent message(Object message){
		
		this.message = message;
		
		return this;
	}
	
	public Event build() throws Exception{
		
		if(!checkValid()) throw new Exception("event format not valid");
		
		Event event = constructEvent(topic, message);
		
		return event;
	}
	
	private boolean checkValid(){
		
		if(this.topic != null && this.message != null) return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private Event constructEvent(String topic, Object data) {
		Event event;
		if (data instanceof Dictionary<?,?>) {
			event = new Event(topic, (Dictionary<String,?>)data);
		} else if (data instanceof Map<?,?>) {
			event = new Event(topic, (Map<String,?>)data);
		} else {
			Dictionary<String, Object> d = new Hashtable<String, Object>(2);
			d.put(EventConstants.EVENT_TOPIC, topic);
			if (data != null)
				d.put(IEventBroker.DATA, data);
			event = new Event(topic, d);
		}
		return event;
	}
}