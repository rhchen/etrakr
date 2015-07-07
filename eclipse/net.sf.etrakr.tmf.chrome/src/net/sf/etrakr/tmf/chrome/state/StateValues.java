/*******************************************************************************
 * Copyright (c) 2012, 2013 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Alexandre Montplaisir - Initial API and implementation
 ******************************************************************************/

package net.sf.etrakr.tmf.chrome.state;

import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.statesystem.core.statevalue.TmfStateValue;

/**
 * State values that are used in the kernel event handler. It's much better to
 * use integer values whenever possible, since those take much less space in the
 * history file.
 *
 * @author alexmont
 *
 */
@SuppressWarnings("javadoc")
public interface StateValues {

    /* CPU Status */
    static final int CPU_STATUS_IDLE = 0;
    static final int CPU_STATUS_RUN_USERMODE = 1;
    static final int CPU_STATUS_RUN_SYSCALL = 2;
    
    static final ITmfStateValue CPU_STATUS_IDLE_VALUE = TmfStateValue.newValueInt(CPU_STATUS_IDLE);
    static final ITmfStateValue CPU_STATUS_RUN_USERMODE_VALUE = TmfStateValue.newValueInt(CPU_STATUS_RUN_USERMODE);
    static final ITmfStateValue CPU_STATUS_RUN_SYSCALL_VALUE = TmfStateValue.newValueInt(CPU_STATUS_RUN_SYSCALL);
    
    static final int PROCESS_STATUS_RUN_USERMODE = 2;
    static final int PROCESS_STATUS_RUN_SYSCALL = 3;
    
    static final ITmfStateValue PROCESS_STATUS_RUN_USERMODE_VALUE = TmfStateValue.newValueInt(PROCESS_STATUS_RUN_USERMODE);
    static final ITmfStateValue PROCESS_STATUS_RUN_SYSCALL_VALUE = TmfStateValue.newValueInt(PROCESS_STATUS_RUN_SYSCALL);
    
   
}
