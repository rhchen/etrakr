/*******************************************************************************
 * Copyright (c) 2014 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   François Rajotte - Initial API and implementation
 *   Geneviève Bastien - Revision of the initial implementation
 *******************************************************************************/

package net.sf.etrakr.tmf.chrome.state;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.linuxtools.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.linuxtools.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.linuxtools.statesystem.core.statevalue.TmfStateValue;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.statesystem.AbstractTmfStateProvider;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;

/**
 * Creates a state system with the total time spent on CPU for each thread and
 * for each CPU from a kernel trace.
 *
 * This state system in itself keeps the total time on CPU since last time the
 * process was scheduled out. The state system queries will only be accurate
 * when the process is not in a running state. To have exact CPU usage when
 * running, this state system needs to be used along the LTTng Kernel analysis.
 *
 * It requires only the 'sched_switch' events enabled on the trace.
 *
 * @author François Rajotte
 * @since 3.0
 */
public class CtraceCpuStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* For each CPU, maps the last time a thread was scheduled in */
    private final Map<String, Long> fLastStartTimes = new HashMap<String, Long>();
    private final long fTraceStart;

    /**
     * Constructor
     *
     * @param trace
     *            The trace from which to get the CPU usage
     */
    public CtraceCpuStateProvider(ITmfTrace trace) {
        super(trace, ITmfEvent.class, "Ftrace CPU usage"); //$NON-NLS-1$
        fTraceStart = trace.getStartTime().getValue();
    }

    // ------------------------------------------------------------------------
    // ITmfStateProvider
    // ------------------------------------------------------------------------

    @Override
    public int getVersion() {
        return VERSION;
    }

    @Override
    public CtraceCpuStateProvider getNewInstance() {
        return new CtraceCpuStateProvider(this.getTrace());
    }

    @Override
    protected void eventHandle(ITmfEvent event) {
        final String eventName = event.getType().getName();

       
    }

    /* Shortcut for the "current CPU" attribute node */
    private int getNodeCPUs() {
        return ss.getQuarkAbsoluteAndAdd(Attributes.CPUS);
    }

}
