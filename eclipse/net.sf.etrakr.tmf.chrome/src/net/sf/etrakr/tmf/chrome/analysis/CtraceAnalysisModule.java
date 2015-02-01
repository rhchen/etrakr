/*******************************************************************************
 * Copyright (c) 2013, 2014 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Geneviève Bastien - Initial API and implementation
 *   Mathieu Rail - Provide the requirements of the analysis
 *******************************************************************************/

package net.sf.etrakr.tmf.chrome.analysis;

import net.sf.etrakr.chrome.core.CtraceStrings;
import net.sf.etrakr.tmf.chrome.state.CtraceStateProvider;

import org.eclipse.linuxtools.tmf.core.analysis.TmfAnalysisRequirement;
import org.eclipse.linuxtools.tmf.core.analysis.TmfAnalysisRequirement.ValuePriorityLevel;
import org.eclipse.linuxtools.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.linuxtools.tmf.core.statesystem.TmfStateSystemAnalysisModule;

import com.google.common.collect.ImmutableSet;

/**
 * State System Module for lttng kernel traces
 *
 * @author Geneviève Bastien
 * @since 3.0
 */
public class CtraceAnalysisModule extends TmfStateSystemAnalysisModule {

    /**
     * The file name of the History Tree
     */
    public static final String HISTORY_TREE_FILE_NAME = "stateHistory.ht"; //$NON-NLS-1$

    /** The ID of this analysis module */
    public static final String ID = "net.sf.etrakr.tmf.ctrace.analysis.ctrace"; //$NON-NLS-1$

    /*
     * TODO: Decide which events should be mandatory for the analysis, once the
     * appropriate error messages and session setup are in place.
     */
    private static final ImmutableSet<String> REQUIRED_EVENTS = ImmutableSet.of();

    private static final ImmutableSet<String> OPTIONAL_EVENTS = ImmutableSet.of(
    		CtraceStrings.PH_UPPER_B,
    		CtraceStrings.PH_UPPER_E,
    		CtraceStrings.PH_UPPER_I,
    		CtraceStrings.PH_UPPER_C,
    		CtraceStrings.PH_UPPER_S,
    		CtraceStrings.PH_UPPER_T,
    		CtraceStrings.PH_UPPER_F,
    		CtraceStrings.PH_LOWER_S,
    		CtraceStrings.PH_LOWER_T,
    		CtraceStrings.PH_LOWER_F,
    		CtraceStrings.PH_UPPER_M,
    		CtraceStrings.PH_UPPER_P,
    		CtraceStrings.PH_UPPER_O,
    		CtraceStrings.PH_UPPER_N,
    		CtraceStrings.PH_UPPER_D

            );

    /** The requirements as an immutable set */
    private static final ImmutableSet<TmfAnalysisRequirement> REQUIREMENTS;

    /* Since not tend to depend on SessionConfigStrings, move the define here */
    private static final String CONFIG_ELEMENT_DOMAIN = "domain";
    private static final String CONFIG_DOMAIN_TYPE_KERNEL = "KERNEL";
    private static final String CONFIG_ELEMENT_EVENT = "event";
    
    static {
        /* initialize the requirement: domain and events */
        TmfAnalysisRequirement domainReq = new TmfAnalysisRequirement(CONFIG_ELEMENT_DOMAIN);
        domainReq.addValue(CONFIG_DOMAIN_TYPE_KERNEL, ValuePriorityLevel.MANDATORY);

        TmfAnalysisRequirement eventReq = new TmfAnalysisRequirement(CONFIG_ELEMENT_EVENT, REQUIRED_EVENTS, ValuePriorityLevel.MANDATORY);
        eventReq.addValues(OPTIONAL_EVENTS, ValuePriorityLevel.OPTIONAL);

        REQUIREMENTS = ImmutableSet.of(domainReq, eventReq);
    }

    @Override
    protected ITmfStateProvider createStateProvider() {
        return new CtraceStateProvider(getTrace());
    }

    @Override
    protected String getSsFileName() {
        return HISTORY_TREE_FILE_NAME;
    }

    @Override
    protected String getFullHelpText() {
        return "Builds the Ftrace kernel state system to populate the Control Flow view and the Resources View";
    }

    @Override
    public Iterable<TmfAnalysisRequirement> getAnalysisRequirements() {
        return REQUIREMENTS;
    }
}
