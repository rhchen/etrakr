package net.sf.etrakr.tmf.ctrace.trace;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.sf.etrakr.ctrace.core.event.ICtraceEvent;
import net.sf.etrakr.ctrace.core.service.ICtraceService;
import net.sf.etrakr.ctrace.core.service.impl.CtraceService;
import net.sf.etrakr.tmf.ctrace.TmfCtraceActivator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfTraceException;
import org.eclipse.tracecompass.tmf.core.trace.ITmfContext;
import org.eclipse.tracecompass.tmf.core.trace.ITmfEventParser;
import org.eclipse.tracecompass.tmf.core.trace.TmfContext;
import org.eclipse.tracecompass.tmf.core.trace.TmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.location.ITmfLocation;
import org.eclipse.tracecompass.tmf.core.trace.location.TmfLongLocation;

public class ChromeTrace extends TmfTrace implements ITmfEventParser{

	private static final TmfLongLocation NULLLOCATION = new TmfLongLocation((Long) null);
	
	private static final TmfContext NULLCONTEXT = new TmfContext(NULLLOCATION,-1L);
	
	private ICtraceService service = new CtraceService();
	
	private File fFile;
	
	private TmfLongLocation fCurrentLocation;
	
	@Override
	public IStatus validate(IProject project, String path) {
		
		File f = new File(path);
		if (!f.exists()) {
			return new Status(IStatus.ERROR, TmfCtraceActivator.PLUGIN_ID, "File does not exist"); //$NON-NLS-1$
		}
		if (!f.isFile()) {
			return new Status(IStatus.ERROR, TmfCtraceActivator.PLUGIN_ID, path + " is not a file"); //$NON-NLS-1$
		}
		
		return Status.OK_STATUS;
	}

	@Override
	public void initTrace(IResource resource, String path,
			Class<? extends ITmfEvent> type) throws TmfTraceException {
		
		super.initTrace(resource, path, type);
		
		fFile = new File(path);
		
		try {
			
			long fSize = CtraceService.getFileSize(fFile.toURI());
			
			if (fSize == 0) throw new TmfTraceException("file is empty"); //$NON-NLS-1$
			
			int nbEvents = 1 + (int)fSize/1024/1024;
			
			/* A guess to number of events */
			setNbEvents(nbEvents * 10000);
			
			if (getNbEvents() < 1) throw new TmfTraceException("Trace does not have any events"); //$NON-NLS-1$

			service.addTrace(fFile.toURI());
			
			/* trigger service to load data into cache */
			seek(0);
			
		} catch (IOException e) {
			
			throw new TmfTraceException(e.getMessage(), e);
			
		} catch (ExecutionException e) {
			
			throw new TmfTraceException(e.getMessage(), e);
		}
	
		
	}
	
	@Override
	public ITmfLocation getCurrentLocation() {
		return fCurrentLocation;
	}

	@Override
	public double getLocationRatio(ITmfLocation location) {
		return ((TmfLongLocation) location).getLocationInfo().doubleValue() / getNbEvents();
	}

	@Override
	public ITmfContext seekEvent(ITmfLocation location) {
		
		TmfLongLocation nl = (TmfLongLocation) location;
		
		if (location == null) nl = new TmfLongLocation(0L);
		
		try {
			
			seek(nl.getLocationInfo());
		
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return new TmfContext(nl, nl.getLocationInfo());
	}

	@Override
	public ITmfContext seekEvent(double ratio) {
		
		long rank = (long) (ratio * getNbEvents());
		
		try {
			
			seek(rank);
		
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return new TmfContext(new TmfLongLocation(rank), rank);
	}

	@Override
	public ITmfEvent parseEvent(ITmfContext context) {
		
		if ((context == null) || (context.getRank() == -1)) return null;

		long pos = context.getRank();
		
		/* Escape on count == NBevents */
		if(pos == getNbEvents()) return null;
		
		fCurrentLocation = new TmfLongLocation(pos);
		
		try {
			
			ITmfEvent event = service.getTmfEvent(fFile.toURI(), pos);

			if(event == null) return null;
			
			event = ((ICtraceEvent) event).newEvent(this);
			
			return event;
			
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private void seek(long rank) throws ExecutionException{
		
		service.getTmfEvent(fFile.toURI(), rank);
		
	}

}
