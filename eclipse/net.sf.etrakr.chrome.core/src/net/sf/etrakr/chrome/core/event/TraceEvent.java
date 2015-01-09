package net.sf.etrakr.chrome.core.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceEvent {

	public String cat;
	public String pid;
	public String tid;
	public long ts;
	public String ph;
	public String name;
	
}