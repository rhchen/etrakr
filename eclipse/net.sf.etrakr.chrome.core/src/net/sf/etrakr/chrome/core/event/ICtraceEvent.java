package net.sf.etrakr.chrome.core.event;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

public interface ICtraceEvent extends ITmfEvent{

	public ICtraceEvent newEvent(final ITmfTrace fTrace);
	
	public String getSource();

	public String getReference();
}
