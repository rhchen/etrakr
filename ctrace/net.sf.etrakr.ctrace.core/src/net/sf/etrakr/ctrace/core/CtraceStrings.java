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

package net.sf.etrakr.ctrace.core;

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

	/* 
	 * Event Types 
	 * Available events are B, E, I, C, S, T, F, s, t, f, M, P, O, N, D
	 */
	static final String PH_UPPER_X     = "X"; /* Replace of B/E Event */
	static final String PH_UPPER_B     = "B";
	static final String PH_UPPER_E     = "E";
	static final String PH_UPPER_I     = "I";
	static final String PH_UPPER_C     = "C";
	static final String PH_UPPER_S     = "S";
	static final String PH_UPPER_T     = "T";
	static final String PH_UPPER_F     = "F";
	static final String PH_LOWER_S     = "s";
	static final String PH_LOWER_T     = "t";
	static final String PH_LOWER_F     = "f";
	static final String PH_UPPER_M     = "m";
	static final String PH_UPPER_P     = "P";
	static final String PH_UPPER_O     = "O";
	static final String PH_UPPER_N     = "N";
	static final String PH_UPPER_D     = "D";
	
    /* Field Names */
    static final String NAME     = "name";
    static final String PID      = "pid";
    static final String TID      = "tid";
    static final String DUR      = "dur";
    //static final String PPID     = "ppid";
    //static final String STATUS   = "status";
    
}
