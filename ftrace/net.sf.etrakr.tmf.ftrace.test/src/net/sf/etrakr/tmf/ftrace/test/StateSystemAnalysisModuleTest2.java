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

package net.sf.etrakr.tmf.ftrace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import net.sf.etrakr.tmf.ftrace.analysis.FtraceAnalysisModule;
import net.sf.etrakr.tmf.ftrace.state.Attributes;
import net.sf.etrakr.tmf.ftrace.trace.FtraceTrace;

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
public class StateSystemAnalysisModuleTest2 {

    /** Time-out tests after 20 seconds */
//    @Rule
//    public TestRule globalTimeout= new Timeout(60000);

    /** ID of the test state system analysis module */
    public static final String MODULE_SS = FtraceAnalysisModule.ID;

    private static final File _file = new File("data/sched_07_irq_switch_simple.log");
    private static final File _file2 = new File("data/android_systrace.txt");
    
    private TmfStateSystemAnalysisModule module;

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
    	
    	FtraceTrace trace = new FtraceTrace();
		TmfSignalManager.deregister(trace);

		try {

			trace.initTrace(null, _file.getAbsolutePath(), null);

			/* 
			 * Force delete the ht files in junit testcases 
			 * default path is java.io.tmpdir
			 */
			String directory = TmfTraceManager.getSupplementaryFileDir(trace);
			deleteRecursive(new File(directory));
			
			trace.traceOpened(new TmfTraceOpenedSignal(this, trace, null));

			module = (TmfStateSystemAnalysisModule) trace.getAnalysisModule(MODULE_SS);

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
        List<Integer> irqQuarks = ssq.getQuarks(Attributes.RESOURCES, Attributes.IRQS, "*"); //$NON-NLS-1$
        for (Integer irqQuark : irqQuarks) {
            int irq = Integer.parseInt(ssq.getAttributeName(irqQuark));
           
        }
        List<Integer> softIrqQuarks = ssq.getQuarks(Attributes.RESOURCES, Attributes.SOFT_IRQS, "*"); //$NON-NLS-1$
        for (Integer softIrqQuark : softIrqQuarks) {
            int softIrq = Integer.parseInt(ssq.getAttributeName(softIrqQuark));
            
        }
        
        final long realStart = ssq.getStartTime();
        final long realEnd =ssq.getCurrentEndTime() + 1;
    }
}
