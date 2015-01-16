package net.sf.etrakr.chrome.core.event;

import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;

public interface ICtraceEvent {

	public ICtraceEvent newEvent(final ITmfTrace fTrace);
}
