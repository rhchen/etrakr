/*******************************************************************************
 * Copyright (c) 2013, 2014 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bastien - Initial API and implementation
 *******************************************************************************/

package net.sf.etrakr.tmf.ctrace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import net.sf.etrakr.tmf.ctrace.analysis.CtraceAnalysisModule;
import net.sf.etrakr.tmf.ctrace.state.Attributes;
import net.sf.etrakr.tmf.ctrace.trace.ChromeTrace;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.tmf.core.analysis.TmfAnalysisManager;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfTraceException;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceOpenedSignal;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

/**
 * The testcase must be run under plugin test
 * 
 * Due to requires init the extentions of Analysis define
 */
public class StateSystemAnalysisModuleTest {

    /** Time-out tests after 20 seconds */
//    @Rule
//    public TestRule globalTimeout= new Timeout(60000);

    /** ID of the test state system analysis module */
    public static final String MODULE_SS = CtraceAnalysisModule.ID;

    private static final File _file = new File("data/perf_sampling_trace_with_trace_events.json");
    
    private CtraceAnalysisModule module;

    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && f.delete();
            }
        }
        return ret && path.delete();
    }
    
    /**
     * Setup test trace
     */
    @Before
    public void setupTraces() {
    	
    	
    	ChromeTrace trace = new ChromeTrace();
		TmfSignalManager.deregister(trace);

		try {

			/* Init the trace first, ht files not yet locked */
			trace.initTrace(null, _file.getAbsolutePath(), null);
			
			/* 
			 * Force delete the ht files in junit testcases 
			 * default path is java.io.tmpdir
			 */
			String directory = TmfTraceManager.getSupplementaryFileDir(trace);
			deleteRecursive(new File(directory));
			
			/* trigger the state provider to handle event */
			trace.traceOpened(new TmfTraceOpenedSignal(this, trace, null));

			module = (CtraceAnalysisModule) trace.getAnalysisModule(MODULE_SS);

		} catch (TmfTraceException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
       
    }

    /**
     * Some tests use traces, let's clean them here
     */
    @After
    public void cleanupTraces() {
    }

    /**
     * Test the state system module execution and result
     */
    @Test
    public void testSsModule() {
        ITmfStateSystem ss = module.getStateSystem();
        assertNull(ss);
        module.schedule();
        if (module.waitForCompletion()) {
            ss = module.getStateSystem();
            assertNotNull(ss);
        } else {
            fail("Module did not complete properly");
        }
    }

    /**
     * Make sure that the state system is initialized after calling
     * {@link TmfStateSystemAnalysisModule#waitForInitialization()}.
     */
    @Test
    public void testInitialization() {
        assertNull(module.getStateSystem());
        module.schedule();

        module.waitForInitialization();
        assertNotNull(module.getStateSystem());
    }

    @Test
    public void testQuark(){
    	
    	ITmfStateSystem ssq = module.getStateSystem();
        assertNull(ssq);
        module.schedule();
        if (module.waitForCompletion()) {
        	ssq = module.getStateSystem();
            assertNotNull(ssq);
        } else {
            fail("Module did not complete properly");
        }
        
    	List<Integer> cpuQuarks = ssq.getQuarks(Attributes.CPUS, "*"); //$NON-NLS-1$
        for (Integer cpuQuark : cpuQuarks) {
            int cpu = Integer.parseInt(ssq.getAttributeName(cpuQuark));
            
        }
        
        List<Integer> processQuarks = ssq.getQuarks(Attributes.PROCESSS, "*"); //$NON-NLS-1$
        for (Integer processQuark : processQuarks) {
            int cpu = Integer.parseInt(ssq.getAttributeName(processQuark));
            
        }
        
        List<Integer> threadsQuarks = ssq.getQuarks(Attributes.THREADS, "*"); //$NON-NLS-1$
        for (Integer threadsQuark : threadsQuarks) {
            int cpu = Integer.parseInt(ssq.getAttributeName(threadsQuark));
            
        }
        
        final long realStart = ssq.getStartTime();
        final long realEnd =ssq.getCurrentEndTime() + 1;
    }
}
