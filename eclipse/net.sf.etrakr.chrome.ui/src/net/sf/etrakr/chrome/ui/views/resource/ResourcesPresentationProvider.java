/*******************************************************************************
 * Copyright (c) 2012, 2014 Ericsson, 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Patrick Tasse - Initial API and implementation
 *   Bastien - Move code to provide base classes for time graph view
 *******************************************************************************/

package net.sf.etrakr.chrome.ui.views.resource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.etrakr.chrome.ui.CtracePlugin;
import net.sf.etrakr.chrome.ui.Messages;
import net.sf.etrakr.chrome.ui.views.resource.ResourcesEntry.Type;
import net.sf.etrakr.tmf.chrome.analysis.CtraceAnalysisModule;
import net.sf.etrakr.tmf.chrome.state.Attributes;
//import net.sf.etrakr.tmf.chrome.state.StateValues;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateValueTypeException;
import org.eclipse.tracecompass.statesystem.core.exceptions.TimeRangeException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.StateItem;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.TimeGraphPresentationProvider;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.NullTimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.ITmfTimeGraphDrawingHelper;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils.Resolution;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils.TimeFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Presentation provider for the Resource view, based on the generic TMF
 * presentation provider.
 *
 * @author Patrick Tasse
 */
public class ResourcesPresentationProvider extends TimeGraphPresentationProvider {

    private long fLastThreadId = -1;

    private enum State {
        IDLE             (new RGB(200, 200, 200)),
        USERMODE         (new RGB(  0, 200,   0)),
        SYSCALL          (new RGB(  0,   0, 200)),
        IRQ              (new RGB(200,   0, 100)),
        SOFT_IRQ         (new RGB(200, 150, 100)),
        IRQ_ACTIVE       (new RGB(200,   0, 100)),
        SOFT_IRQ_RAISED  (new RGB(200, 200,   0)),
        SOFT_IRQ_ACTIVE  (new RGB(200, 150, 100));

        public final RGB rgb;

        private State(RGB rgb) {
            this.rgb = rgb;
        }
    }

    /**
     * Default constructor
     */
    public ResourcesPresentationProvider() {
        super();
    }

    private static State[] getStateValues() {
        return State.values();
    }

    private static State getEventState(TimeEvent event) {
        if (event.hasValue()) {
            ResourcesEntry entry = (ResourcesEntry) event.getEntry();
            int value = event.getValue();

            if (entry.getType() == Type.PROCESSS) {
            	return State.IDLE;
            } else if (entry.getType() == Type.THREADS) {
                return State.IRQ_ACTIVE;
            }
        }
        return null;
    }

    @Override
    public int getStateTableIndex(ITimeEvent event) {
        State state = getEventState((TimeEvent) event);
        if (state != null) {
            return state.ordinal();
        }
        if (event instanceof NullTimeEvent) {
            return INVISIBLE;
        }
        return TRANSPARENT;
    }

    @Override
    public StateItem[] getStateTable() {
        State[] states = getStateValues();
        StateItem[] stateTable = new StateItem[states.length];
        for (int i = 0; i < stateTable.length; i++) {
            State state = states[i];
            stateTable[i] = new StateItem(state.rgb, state.toString());
        }
        return stateTable;
    }

    @Override
    public String getEventName(ITimeEvent event) {
        State state = getEventState((TimeEvent) event);
        if (state != null) {
            return state.toString();
        }
        if (event instanceof NullTimeEvent) {
            return null;
        }
        return Messages.ResourcesView_multipleStates;
    }

    @Override
    public Map<String, String> getEventHoverToolTipInfo(ITimeEvent event, long hoverTime) {

        Map<String, String> retMap = new LinkedHashMap<String, String>();
        if (event instanceof TimeEvent && ((TimeEvent) event).hasValue()) {

            TimeEvent tcEvent = (TimeEvent) event;
            ResourcesEntry entry = (ResourcesEntry) event.getEntry();

            if (tcEvent.hasValue()) {
                ITmfStateSystem ss = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), CtraceAnalysisModule.ID);
                if (ss == null) {
                    return retMap;
                }
                
                int cpu = tcEvent.getValue();
                retMap.put(Messages.ResourcesView_attributeCpuName, String.valueOf(cpu));
                
            }
        }

        return retMap;
    }

    @Override
    public void postDrawEvent(ITimeEvent event, Rectangle bounds, GC gc) {
        ITmfTimeGraphDrawingHelper drawingHelper = getDrawingHelper();
        if (bounds.width <= gc.getFontMetrics().getAverageCharWidth()) {
            return;
        }

        if (!(event instanceof TimeEvent)) {
            return;
        }
        TimeEvent tcEvent = (TimeEvent) event;
        if (!tcEvent.hasValue()) {
            return;
        }

        /* Fix me, RH */
//        ResourcesEntry entry = (ResourcesEntry) event.getEntry();
//        if (!entry.getType().equals(Type.CPU)) {
//            return;
//        }
//
//        int status = tcEvent.getValue();
//        if (status != StateValues.CPU_STATUS_RUN_USERMODE && status != StateValues.CPU_STATUS_RUN_SYSCALL) {
//            return;
//        }
//
//        ITmfStateSystem ss = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), FtraceAnalysisModule.ID);
//        if (ss == null) {
//            return;
//        }
//        long time = event.getTime();
//        try {
//            while (time < event.getTime() + event.getDuration()) {
//                int cpuQuark = entry.getQuark();
//                int currentThreadQuark = ss.getQuarkRelative(cpuQuark, Attributes.CURRENT_THREAD);
//                ITmfStateInterval tidInterval = ss.querySingleState(time, currentThreadQuark);
//                if (!tidInterval.getStateValue().isNull()) {
//                    ITmfStateValue value = tidInterval.getStateValue();
//                    int currentThreadId = value.unboxInt();
//                    if (status == StateValues.CPU_STATUS_RUN_USERMODE && currentThreadId != fLastThreadId) {
//                        int execNameQuark = ss.getQuarkAbsolute(Attributes.THREADS, Integer.toString(currentThreadId), Attributes.EXEC_NAME);
//                        ITmfStateInterval interval = ss.querySingleState(time, execNameQuark);
//                        if (!interval.getStateValue().isNull()) {
//                            value = interval.getStateValue();
//                            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
//                            long startTime = Math.max(tidInterval.getStartTime(), event.getTime());
//                            long endTime = Math.min(tidInterval.getEndTime() + 1, event.getTime() + event.getDuration());
//                            if (drawingHelper.getXForTime(endTime) > bounds.x) {
//                                int x = Math.max(drawingHelper.getXForTime(startTime), bounds.x);
//                                int width = Math.min(drawingHelper.getXForTime(endTime), bounds.x + bounds.width) - x;
//                                int drawn = Utils.drawText(gc, value.unboxStr(), x + 1, bounds.y - 2, width - 1, true, true);
//                                if (drawn > 0) {
//                                    fLastThreadId = currentThreadId;
//                                }
//                            }
//                        }
//                    } else if (status == StateValues.CPU_STATUS_RUN_SYSCALL) {
//                        int syscallQuark = ss.getQuarkAbsolute(Attributes.THREADS, Integer.toString(currentThreadId), Attributes.SYSTEM_CALL);
//                        ITmfStateInterval interval = ss.querySingleState(time, syscallQuark);
//                        if (!interval.getStateValue().isNull()) {
//                            value = interval.getStateValue();
//                            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
//                            long startTime = Math.max(tidInterval.getStartTime(), event.getTime());
//                            long endTime = Math.min(tidInterval.getEndTime() + 1, event.getTime() + event.getDuration());
//                            if (drawingHelper.getXForTime(endTime) > bounds.x) {
//                                int x = Math.max(drawingHelper.getXForTime(startTime), bounds.x);
//                                int width = Math.min(drawingHelper.getXForTime(endTime), bounds.x + bounds.width) - x;
//                                Utils.drawText(gc, value.unboxStr().substring(4), x + 1, bounds.y - 2, width - 1, true, true);
//                            }
//                        }
//                    }
//                }
//                time = tidInterval.getEndTime() + 1;
//                if (time < event.getTime() + event.getDuration()) {
//                    int x = drawingHelper.getXForTime(time);
//                    if (x >= bounds.x) {
//                        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
//                        gc.drawLine(x, bounds.y + 1, x, bounds.y + bounds.height - 2);
//                    }
//                }
//            }
//        } catch (AttributeNotFoundException e) {
//        	CtracePlugin.getDefault().logError("Error in ResourcesPresentationProvider", e); //$NON-NLS-1$
//        } catch (TimeRangeException e) {
//        	CtracePlugin.getDefault().logError("Error in ResourcesPresentationProvider", e); //$NON-NLS-1$
//        } catch (StateValueTypeException e) {
//        	CtracePlugin.getDefault().logError("Error in ResourcesPresentationProvider", e); //$NON-NLS-1$
//        } catch (StateSystemDisposedException e) {
//            /* Ignored */
//        }
    }

    @Override
    public void postDrawEntry(ITimeGraphEntry entry, Rectangle bounds, GC gc) {
        fLastThreadId = -1;
    }
}
