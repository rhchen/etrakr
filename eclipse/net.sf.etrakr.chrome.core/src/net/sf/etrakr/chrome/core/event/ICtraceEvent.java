package net.sf.etrakr.chrome.core.event;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;

public interface ICtraceEvent extends ITmfEvent{

	public ICtraceEvent newEvent(final ITmfTrace fTrace);
}
