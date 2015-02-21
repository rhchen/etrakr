package net.sf.etrakr.tmf.chrome.state;

import java.util.HashMap;

import net.sf.etrakr.chrome.core.CtraceStrings;
import net.sf.etrakr.chrome.core.event.ICtraceEvent;
import net.sf.etrakr.chrome.core.event.impl.CtraceEvent;

import org.eclipse.linuxtools.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.linuxtools.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.linuxtools.statesystem.core.statevalue.TmfStateValue;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.statesystem.AbstractTmfStateProvider;
import org.eclipse.linuxtools.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;

public class CtraceStateProvider extends AbstractTmfStateProvider {

	/**
     * Version number of this state provider. Please bump this if you modify the
     * contents of the generated state history in some way.
     */
    private static final int VERSION = 4;
    
    /* Event names HashMap. TODO: This can be discarded once we move to Java 7 
     * Still we use Java 6 */
    private final HashMap<String, Integer> knownEventNames;
    
    public CtraceStateProvider(ITmfTrace trace) {
        super(trace, ITmfEvent.class, "Ctrace"); //$NON-NLS-1$
        knownEventNames = fillEventNames();
    }

	@Override
	public int getVersion() {
		return VERSION;
	}

	@Override
	public ITmfStateProvider getNewInstance() {
		return new CtraceStateProvider(this.getTrace());
	}

	@Override
	protected void eventHandle(ITmfEvent ev) {
		
		/*
         * AbstractStateChangeInput should have already checked for the correct
         * class type
         */

    	ICtraceEvent event = (CtraceEvent) ev;
    	
        final String eventName = event.getType().getName();
        final long ts = event.getTimestamp().getValue();
        
        try {
			
//			/* Shortcut for the "current CPU" attribute node */
//	        final Integer currentCPUNode = ss.getQuarkRelativeAndAdd(getNodeCPUs(), event.getSource());
//
//	       
//	        /*
//	         * Shortcut for the "current thread" attribute node. It requires
//	         * querying the current CPU's current thread.
//	         */
//	        int quark = ss.getQuarkRelativeAndAdd(currentCPUNode, Attributes.CURRENT_THREAD);
//	        
//			ITmfStateValue value = ss.queryOngoingState(quark);
//			
//			int thread = value.isNull() ? -1 : value.unboxInt();
//	        final Integer currentThreadNode = ss.getQuarkRelativeAndAdd(getNodeThreads(), String.valueOf(thread));

	        ITmfEventField content = event.getContent();
	        String _name = content.getField(CtraceStrings.NAME).getFormattedValue();
	        String _pid = content.getField(CtraceStrings.PID).getFormattedValue();
	        String _tid = content.getField(CtraceStrings.TID).getFormattedValue();
	        System.out.println("_tid : "+ _tid);
	        /* Shortcut for the "current Process" attribute node */
	        final Integer currentProcessNode = ss.getQuarkRelativeAndAdd(getNodeProcesss(), _pid);
	        
	        int currentThreadQuark = ss.getQuarkRelativeAndAdd(currentProcessNode, Attributes.CURRENT_THREAD);
	        ITmfStateValue value = TmfStateValue.newValueInt(Integer.parseInt(_tid));
            ss.modifyAttribute(ts, value, currentThreadQuark);
            
            final Integer currentThreadsNode = ss.getQuarkRelativeAndAdd(getNodeThreads(), _tid);
        	
        	int currentExecQuark = ss.getQuarkRelativeAndAdd(currentThreadsNode, Attributes.EXEC_NAME);
        	TmfStateValue sv_name = TmfStateValue.newValueString(_name);
            ss.modifyAttribute(ts, sv_name, currentExecQuark);

            int currentPhQuark = ss.getQuarkRelativeAndAdd(currentThreadsNode, Attributes.PH);
            TmfStateValue sv_ph = TmfStateValue.newValueString(eventName);
            ss.modifyAttribute(ts, sv_ph, currentPhQuark);
            
	        /*
	         * Feed event to the history system if it's known to cause a state
	         * transition.
	         */
	        switch (getEventIndex(eventName)) {
	        
		        case 0 : /* X event */
		        	//System.out.println("eventName : "+ eventName);
		            int currentDurQuark = ss.getQuarkRelativeAndAdd(currentThreadsNode, Attributes.DUR);
		        	String _dur = content.getField(CtraceStrings.DUR).getFormattedValue();
		            value = TmfStateValue.newValueInt(Integer.parseInt(_dur));
		            ss.modifyAttribute(ts, value, currentDurQuark);
		            break;
	        	
		        case 1 : /* B event */
		        	
		        	break;
		        	
		        case 2 : /* E event */
		        	//System.out.println("eventName : "+ eventName);
		        	break;
		        	
	        	default :
	        		//System.out.println("eventName : "+ eventName);
	        		break;
	        	
	        }
	        
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		}
        
	}

	// ------------------------------------------------------------------------
	// Workaround for the lack of switch-on-strings in Java < 7
	// ------------------------------------------------------------------------
	private int getEventIndex(String eventName) {
		Integer ret = knownEventNames.get(eventName);
		return (ret != null) ? ret : -1;
	}
        
	private static HashMap<String, Integer> fillEventNames() {
		
        /*
         * Fix Me, Replace with straight strings in the switch/case once we move to
         * Java 7
         */
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        map.put(CtraceStrings.PH_UPPER_X, 0);
        map.put(CtraceStrings.PH_UPPER_B, 1);
        map.put(CtraceStrings.PH_UPPER_E, 2);
        map.put(CtraceStrings.PH_UPPER_I, 3);
        map.put(CtraceStrings.PH_UPPER_C, 4);
        map.put(CtraceStrings.PH_UPPER_S, 5);
        map.put(CtraceStrings.PH_UPPER_T, 6);
        map.put(CtraceStrings.PH_UPPER_F, 7);
        map.put(CtraceStrings.PH_LOWER_S, 8);
        map.put(CtraceStrings.PH_LOWER_T, 9);
        map.put(CtraceStrings.PH_LOWER_F, 10);
        map.put(CtraceStrings.PH_UPPER_M, 11);
        map.put(CtraceStrings.PH_UPPER_P, 12);
        map.put(CtraceStrings.PH_UPPER_O, 13);
        map.put(CtraceStrings.PH_UPPER_N, 14);
        map.put(CtraceStrings.PH_UPPER_D, 15);
        
        return map;
    }
	
	// ------------------------------------------------------------------------
    // Convenience methods for commonly-used attribute tree locations
    // ------------------------------------------------------------------------

    private int getNodeCPUs() {
        return ss.getQuarkAbsoluteAndAdd(Attributes.CPUS);
    }

    private int getNodeThreads() {
    	return ss.getQuarkAbsoluteAndAdd(Attributes.THREADS);
    }
    
    private int getNodeProcesss() {
        return ss.getQuarkAbsoluteAndAdd(Attributes.PROCESSS);
    }
    
    private void _handleEvent(){
    	
    }
}
