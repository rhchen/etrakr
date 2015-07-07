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

package net.sf.etrakr.ftrace.ui.views.controlflow;

import org.eclipse.core.runtime.Assert;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;

/**
 * An entry in the Control Flow view
 */
public class ControlFlowEntry extends TimeGraphEntry {

    private final ITmfTrace fTrace;
    private final int fThreadId;
    private final int fParentThreadId;
    private final int fThreadQuark;

    /**
     * Constructor
     *
     * @param quark
     *            The attribute quark matching the thread
     * @param trace
     *            The trace on which we are working
     * @param execName
     *            The exec_name of this entry
     * @param threadId
     *            The TID of the thread
     * @param parentThreadId
     *            the Parent_TID of this thread
     * @param startTime
     *            The start time of this process's lifetime
     * @param endTime
     *            The end time of this process
     */
    public ControlFlowEntry(int quark, ITmfTrace trace, String execName, int threadId, int parentThreadId, long startTime, long endTime) {
        super(execName, startTime, endTime);
        Assert.isNotNull(trace);
        fTrace = trace;
        fThreadId = threadId;
        fParentThreadId = parentThreadId;
        fThreadQuark = quark;
    }

    /**
     * Get this entry's thread ID
     *
     * @return The TID
     */
    public int getThreadId() {
        return fThreadId;
    }

    /**
     * Get the entry's trace
     *
     * @return the entry's trace
     */
    public ITmfTrace getTrace() {
    	Assert.isNotNull(fTrace);
    	return fTrace;
    }

    /**
     * Get this thread's parent TID
     *
     * @return The "PTID"
     */
    public int getParentThreadId() {
        return fParentThreadId;
    }

    /**
     * Get the quark of the attribute matching this thread's TID
     *
     * @return The quark
     */
    public int getThreadQuark() {
        return fThreadQuark;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + getName() + '[' + fThreadId + "])"; //$NON-NLS-1$
    }
}
