package net.sf.etrakr.tmf.remote.adb.ui;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tracecompass.tmf.core.signal.TmfSelectionRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceClosedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceOpenedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfWindowRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimeRange;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TmfAdbPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.etrakr.tmf.remote.adb.ui"; //$NON-NLS-1$

	// The shared instance
	private static TmfAdbPlugin plugin;
	
	/**
	 * The constructor
	 */
	public TmfAdbPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		super.start(context);
		
		plugin = this;
		
		TmfSignalManager.register(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		TmfSignalManager.deregister(this);
		
		plugin = null;
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TmfAdbPlugin getDefault() {
		return plugin;
	}

	
	// ------------------------------------------------------------------------
    // Signal handlers
    // ------------------------------------------------------------------------

	private ITmfTrace fTrace;
	private long fTraceStartTime;
    private long fTraceEndTime;
    private long fWindowStartTime;
    private long fWindowEndTime;
    private long fWindowSpan;
    private long fSelectionBeginTime;
    private long fSelectionEndTime;
    
    
    /**
     * Handles trace opened signal. Loads histogram if new trace time range is not
     * equal <code>TmfTimeRange.NULL_RANGE</code>
     * @param signal the trace opened signal
     */
    @TmfSignalHandler
    public void traceOpened(TmfTraceOpenedSignal signal) {
        assert (signal != null);
        fTrace = signal.getTrace();
        
        System.out.println("TmfAdbPlugin.traceOpened "+ signal);
    }

    /**
     * Handles trace selected signal. Loads histogram if new trace time range is not
     * equal <code>TmfTimeRange.NULL_RANGE</code>
     * @param signal the trace selected signal
     */
    @TmfSignalHandler
    public void traceSelected(TmfTraceSelectedSignal signal) {
        assert (signal != null);
        if (fTrace != signal.getTrace()) {
            fTrace = signal.getTrace();
        }
        
        System.out.println("TmfAdbPlugin.traceSelected "+ signal);
    }
    
    /**
     * Handles trace closed signal. Clears the view and data model and cancels requests.
     * @param signal the trace closed signal
     */
    @TmfSignalHandler
    public void traceClosed(TmfTraceClosedSignal signal) {

        if (signal.getTrace() != fTrace) {
            return;
        }

        // Initialize the internal data
        fTrace = null;
        fTraceStartTime = 0L;
        fTraceEndTime = 0L;
        fWindowStartTime = 0L;
        fWindowEndTime = 0L;
        fWindowSpan = 0L;
        fSelectionBeginTime = 0L;
        fSelectionEndTime = 0L;

        System.out.println("TmfAdbPlugin.traceClosed "+ signal);
    }

    /**
     * Handles trace range updated signal. Extends histogram according to the new time range. If a
     * HistogramRequest is already ongoing, it will be cancelled and a new request with the new range
     * will be issued.
     *
     * @param signal the trace range updated signal
     */
    @TmfSignalHandler
    public void traceRangeUpdated(TmfTraceRangeUpdatedSignal signal) {

        if (signal.getTrace() != fTrace) {
            return;
        }

        TmfTimeRange fullRange = signal.getRange();

        fTraceStartTime = fullRange.getStartTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE).getValue();
        fTraceEndTime = fullRange.getEndTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE).getValue();

        System.out.println("TmfAdbPlugin.traceRangeUpdated "+ signal);
        
    }

    private TmfTimeRange fullRange;
    
    public TmfTimeRange getTmfTimeRange(){
    
    	return fullRange;
    	
    }
    
    /**
     * Handles the trace updated signal. Used to update time limits (start and end time)
     * @param signal the trace updated signal
     */
    @TmfSignalHandler
    public void traceUpdated(TmfTraceUpdatedSignal signal) {
        if (signal.getTrace() != fTrace) {
            return;
        }
        fullRange = signal.getTrace().getTimeRange();
        fTraceStartTime = fullRange.getStartTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE).getValue();
        fTraceEndTime = fullRange.getEndTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE).getValue();

        System.out.println("TmfAdbPlugin.traceUpdated "+ signal);
    }

    /**
     * Handles the selection range updated signal. Sets the current time
     * selection in the time range histogram as well as the full histogram.
     *
     * @param signal
     *            the signal to process
     * @since 1.0
     */
    @TmfSignalHandler
    public void selectionRangeUpdated(final TmfSelectionRangeUpdatedSignal signal) {
        
    	// Update the selected time range
        ITmfTimestamp beginTime = signal.getBeginTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE);
        ITmfTimestamp endTime = signal.getEndTime().normalize(0, ITmfTimestamp.NANOSECOND_SCALE);
        
        System.out.println("TmfAdbPlugin.selectionRangeUpdated "+ signal);
    }

    /**
     * Updates the current window time range in the time range histogram and
     * full range histogram.
     *
     * @param signal
     *            the signal to process
     * @since 1.0
     */
    @TmfSignalHandler
    public void windowRangeUpdated(final TmfWindowRangeUpdatedSignal signal) {
        
    	if (fTrace != null) {
            // Validate the time range
            TmfTimeRange range = signal.getCurrentRange().getIntersection(fTrace.getTimeRange());
            if (range == null) {
                return;
            }

        }
    	
    	System.out.println("TmfAdbPlugin.windowRangeUpdated "+ signal);
    }
}
