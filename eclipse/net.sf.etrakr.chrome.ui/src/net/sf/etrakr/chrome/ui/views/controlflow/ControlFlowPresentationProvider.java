/*******************************************************************************
 * Copyright (c) 2012, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Patrick Tasse - Initial API and implementation
 *******************************************************************************/

package net.sf.etrakr.chrome.ui.views.controlflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.etrakr.chrome.ui.CtracePlugin;
import net.sf.etrakr.chrome.ui.Messages;
import net.sf.etrakr.tmf.chrome.analysis.CtraceAnalysisModule;
import net.sf.etrakr.tmf.chrome.state.Attributes;
//import net.sf.etrakr.tmf.chrome.state.StateValues;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;

import com.google.common.collect.Maps;

/**
 * Presentation provider for the control flow view
 */
public class ControlFlowPresentationProvider extends TimeGraphPresentationProvider {

    private enum State {
        UNKNOWN        (new RGB(100, 100, 100)),
        WAIT_BLOCKED   (new RGB(200, 200, 0)),
        WAIT_FOR_CPU   (new RGB(200, 100, 0)),
        USERMODE       (new RGB(0,   200, 0)),
        SYSCALL        (new RGB(0,     0, 200)),
        INTERRUPTED    (new RGB(200,   0, 100));

        public final RGB rgb;

        private State(RGB rgb) {
            this.rgb = rgb;
        }

    }

    /**
     * Default constructor
     */
    public ControlFlowPresentationProvider() {
        super(Messages.ControlFlowView_stateTypeName);
    }

    private static State[] getStateValues() {
        return State.values();
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
    public int getStateTableIndex(ITimeEvent event) {
        if (event instanceof TimeEvent && ((TimeEvent) event).hasValue()) {
            int status = ((TimeEvent) event).getValue();
            return getMatchingState(status).ordinal();
        }
        return TRANSPARENT;
    }

    @Override
    public String getEventName(ITimeEvent event) {
        if (event instanceof TimeEvent) {
            TimeEvent ev = (TimeEvent) event;
            if (ev.hasValue()) {
                return getMatchingState(ev.getValue()).toString();
            }
        }
        return Messages.ControlFlowView_multipleStates;
    }

    private static State getMatchingState(int status) {
        switch (status) {
        /* Fix me, RH */
//        case StateValues.PROCESS_STATUS_WAIT_BLOCKED:
//            return State.WAIT_BLOCKED;
//        case StateValues.PROCESS_STATUS_WAIT_FOR_CPU:
//            return State.WAIT_FOR_CPU;
//        case StateValues.PROCESS_STATUS_RUN_USERMODE:
//            return State.USERMODE;
//        case StateValues.PROCESS_STATUS_RUN_SYSCALL:
//            return State.SYSCALL;
//        case StateValues.PROCESS_STATUS_INTERRUPTED:
//            return State.INTERRUPTED;
        default:
            return State.UNKNOWN;
        }
    }

    @Override
    public Map<String, String> getEventHoverToolTipInfo(ITimeEvent event) {
    	
        Map<String, String> retMap = Maps.<String, String>newLinkedHashMap();
        if (!(event instanceof TimeEvent) || !((TimeEvent) event).hasValue() ||
                !(event.getEntry() instanceof ControlFlowEntry)) {
            return retMap;
        }
        ControlFlowEntry entry = (ControlFlowEntry) event.getEntry();
        ITmfStateSystem ssq = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), CtraceAnalysisModule.ID);
        if (ssq == null) {
            return retMap;
        }
        int tid = entry.getThreadId();

        try {
            // Find every CPU first, then get the current thread
            int threadsQuark = ssq.getQuarkAbsolute(Attributes.THREADS);
            int currentThreadQuark = ssq.getQuarkRelative(threadsQuark, String.valueOf(tid));
            ITmfStateInterval interval = ssq.querySingleState(event.getTime(), currentThreadQuark);
            if (!interval.getStateValue().isNull()) {
                ITmfStateValue state = interval.getStateValue();
                int currentThreadId = state.unboxInt();
                if (tid == currentThreadId) {
                    retMap.put(Messages.ControlFlowView_attributeCpuName, ssq.getAttributeName(currentThreadQuark));
                }
            }

        } catch (AttributeNotFoundException e) {
        	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
        } catch (TimeRangeException e) {
        	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
        } catch (StateValueTypeException e) {
        	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
        } catch (StateSystemDisposedException e) {
            /* Ignored */
        }
        int status = ((TimeEvent) event).getValue();
        /* Fix me, RH */
//        if (status == StateValues.PROCESS_STATUS_RUN_SYSCALL) {
//            try {
//                int syscallQuark = ssq.getQuarkRelative(entry.getThreadQuark(), Attributes.SYSTEM_CALL);
//                ITmfStateInterval value = ssq.querySingleState(event.getTime(), syscallQuark);
//                if (!value.getStateValue().isNull()) {
//                    ITmfStateValue state = value.getStateValue();
//                    retMap.put(Messages.ControlFlowView_attributeSyscallName, state.toString());
//                }
//
//            } catch (AttributeNotFoundException e) {
//            	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
//            } catch (TimeRangeException e) {
//            	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
//            } catch (StateSystemDisposedException e) {
//                /* Ignored */
//            }
//        }

        return retMap;
    }

    @Override
    public void postDrawEvent(ITimeEvent event, Rectangle bounds, GC gc) {
        if (bounds.width <= gc.getFontMetrics().getAverageCharWidth()) {
            return;
        }
        if (!(event instanceof TimeEvent)) {
            return;
        }
        ControlFlowEntry entry = (ControlFlowEntry) event.getEntry();
        ITmfStateSystem ss = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), CtraceAnalysisModule.ID);
        if (ss == null) {
            return;
        }
        int status = ((TimeEvent) event).getValue();

        /* Fix me, RH */
//        if (status != StateValues.PROCESS_STATUS_RUN_SYSCALL) {
//            return;
//        }
//        try {
//            int syscallQuark = ss.getQuarkRelative(entry.getThreadQuark(), Attributes.SYSTEM_CALL);
//            ITmfStateInterval value = ss.querySingleState(event.getTime(), syscallQuark);
//            if (!value.getStateValue().isNull()) {
//                ITmfStateValue state = value.getStateValue();
//                gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
//                Utils.drawText(gc, state.toString().substring(4), bounds.x, bounds.y - 2, bounds.width, true, true);
//            }
//        } catch (AttributeNotFoundException e) {
//        	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
//        } catch (TimeRangeException e) {
//        	CtracePlugin.getDefault().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
//        } catch (StateSystemDisposedException e) {
//            /* Ignored */
//        }
    }
}
