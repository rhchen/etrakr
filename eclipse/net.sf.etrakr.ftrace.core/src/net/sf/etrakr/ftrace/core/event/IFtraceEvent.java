package net.sf.etrakr.ftrace.core.event;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;

public interface IFtraceEvent extends ITmfEvent {

	public IFtraceEvent newEvent(final ITmfTrace fTrace);
}
