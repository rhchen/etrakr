package net.sf.etrakr.eventbus;

public interface ITkrEvent {

	public static final String TOPIC_ROOT = "org/eclipse/equinox/events";
	
	public static final String TOPIC_ETRAKR = TOPIC_ROOT + "/etrakr";
	
	public static final String TOPIC_ETRAKR_COMMAND = TOPIC_ETRAKR + "/command";
	
	public static final String TOPIC_ETRAKR_COMMAND_OPEN_TRACE = TOPIC_ETRAKR_COMMAND + "/OPEN_TRACE";
	
	
}
