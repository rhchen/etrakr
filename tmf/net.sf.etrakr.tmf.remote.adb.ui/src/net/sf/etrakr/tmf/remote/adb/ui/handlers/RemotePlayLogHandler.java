/*******************************************************************************
 * Copyright (c) 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernd Hufmann - Initial API and implementation
 *******************************************************************************/

package net.sf.etrakr.tmf.remote.adb.ui.handlers;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.tracecompass.tmf.core.signal.TmfSelectionRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
import org.eclipse.tracecompass.tmf.core.signal.TmfWindowRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfNanoTimestamp;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimeRange;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import net.sf.etrakr.eventbus.EventBus;
import net.sf.etrakr.eventbus.ITkrEvent;
import net.sf.etrakr.eventbus.TkrEvent;
import net.sf.etrakr.eventbus.TkrEventException;
import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService;
import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceOptions;
import net.sf.etrakr.tmf.remote.adb.ui.TmfAdbPlugin;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Command handler for opening the remote fetch wizard.
 *
 * @author Bernd Hufmann
 *
 */
public class RemotePlayLogHandler extends AbstractHandler {

	private ITmfTrace fTrace;
	private long fTraceStartTime;
    private long fTraceEndTime;
    private long fWindowStartTime;
    private long fWindowEndTime;
    private long fWindowSpan;
    private long fSelectionBeginTime;
    private long fSelectionEndTime;
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            return false;
        }

        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        IStructuredSelection sec = StructuredSelection.EMPTY;
        if (currentSelection instanceof IStructuredSelection) {
            sec = (IStructuredSelection) currentSelection;
        }

        TmfTimeRange fullRange = TmfAdbPlugin.getDefault().getTmfTimeRange();
        
        fTraceStartTime = fullRange.getStartTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE).getValue();
        fTraceEndTime = fullRange.getEndTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE).getValue();
        
        final ITmfTimestamp START_TIME = new TmfNanoTimestamp(fTraceStartTime);
        
        /* change window range to 10 ms */
        TmfTimeRange range = new TmfTimeRange(START_TIME, START_TIME.normalize(10000000L, ITmfTimestamp.NANOSECOND_SCALE));
        TmfSignalManager.dispatchSignal(new TmfWindowRangeUpdatedSignal(this, range));
        
        /* set selection to trace start time */
        TmfSignalManager.dispatchSignal(new TmfSelectionRangeUpdatedSignal(this, START_TIME));
        
        Timer timer = new Timer();
        
        /* the time must ref AdbSession.executeRequest, default is await 5 sec */
        timer.schedule(new Task("Play Start", START_TIME), 0, 40);

        return null;
    }
    
    private static int count = 0;
    		
	class Task extends TimerTask implements Runnable {
		
		private String name;

		private ITmfTimestamp start_time;
		
		public Task(String name, ITmfTimestamp start_time) {
			this.name = name;
			this.start_time = start_time;
		}

		public String getName() {
			return name;
		}

		@Override
		public void run() {
			
			count++;
			
			long t = start_time.getValue() + 400000L * count;
			
			final ITmfTimestamp START_TIME = new TmfNanoTimestamp(t);
	        
			/* change window range to 100 ms */
	        TmfTimeRange range = new TmfTimeRange(START_TIME, START_TIME.normalize(100000000L, ITmfTimestamp.NANOSECOND_SCALE));
	        TmfSignalManager.dispatchSignal(new TmfWindowRangeUpdatedSignal(this, range));
	        
	        /* set selection to trace start time */
	        TmfSignalManager.dispatchSignal(new TmfSelectionRangeUpdatedSignal(this, START_TIME));
	        
		}
	}
}
