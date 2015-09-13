package net.sf.etrakr.eventbus;

public interface ITkrEvent {

	/* Topic Define */
	public static final String TOPIC_ROOT = "org/eclipse/equinox/events";
	
	public static final String TOPIC_ETRAKR = TOPIC_ROOT + "/etrakr";
	
	public static final String TOPIC_ETRAKR_COMMAND = TOPIC_ETRAKR + "/command";
	
	public static final String TOPIC_ETRAKR_COMMAND_OPEN_TRACE = TOPIC_ETRAKR_COMMAND + "/OPEN_TRACE";
	
	/* Data Key */
	public static final String TOPIC_ROOT_DATA_KEY = "org.eclipse.equinox.events";
	
	public static final String TOPIC_ETRAKR_DATA_KEY = TOPIC_ROOT + ".etrakr";
	
	public static final String TOPIC_ETRAKR_COMMAND_DATA_KEY = TOPIC_ETRAKR + ".command";
	
	public static final String TOPIC_ETRAKR_COMMAND_OPEN_TRACE_DATA_KEY = TOPIC_ETRAKR_COMMAND + ".OPEN_TRACE";
	
	/* Interface Methods */
	public String getDataKey(String topic);
}
