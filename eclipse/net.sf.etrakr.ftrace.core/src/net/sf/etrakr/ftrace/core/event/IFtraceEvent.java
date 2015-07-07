package net.sf.etrakr.ftrace.core.event;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

public interface IFtraceEvent extends ITmfEvent {

	public IFtraceEvent newEvent(final ITmfTrace fTrace);
	
	public String getSource();

	public String getReference();
}
