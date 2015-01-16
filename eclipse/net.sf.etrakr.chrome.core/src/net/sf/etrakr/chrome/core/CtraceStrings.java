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

package net.sf.etrakr.chrome.core;

/**
 * This file defines all the known event and field names for LTTng 2.0 kernel
 * traces.
 *
 * Once again, these should not be externalized, since they need to match
 * exactly what the tracer outputs. If you want to localize them in a view, you
 * should do a mapping in the viewer itself.
 *
 * @author alexmont
 */
@SuppressWarnings({"javadoc", "nls"})
public interface CtraceStrings {

    /* Field names */
    static final String COMM     = "comm";
    static final String NAME     = "name";
    static final String PID      = "pid";
    static final String TID      = "tid";
    static final String PPID     = "ppid";
    static final String STATUS   = "status";
    
}
