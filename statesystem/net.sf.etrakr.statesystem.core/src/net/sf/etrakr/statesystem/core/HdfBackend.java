package net.sf.etrakr.statesystem.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.backend.IStateHistoryBackend;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.exceptions.TimeRangeException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.interval.TmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;

public class HdfBackend implements IStateHistoryBackend {

	private final @NonNull String ssid;
	private final long startTime;
    private volatile long latestTime;
    
    public HdfBackend(@NonNull String ssid, long startTime) {
        this.ssid = ssid;
        this.startTime = startTime;
        this.latestTime = startTime;
    }
    
	@Override
	public String getSSID() {
		return ssid;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return latestTime;
	}

	@Override
	public void insertPastState(long stateStartTime, long stateEndTime, int quark, ITmfStateValue value)
			throws TimeRangeException {
		
		/* Make sure the passed start/end times make sense */
        if (stateStartTime > stateEndTime || stateStartTime < startTime) {
            throw new TimeRangeException(ssid + " Interval Start:" + stateStartTime + ", Interval End:" + stateEndTime + ", Backend Start:" + startTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        ITmfStateInterval interval = new TmfStateInterval(stateStartTime, stateEndTime, quark, value);

        /* Add the interval into the tree */
//        synchronized (intervals) {
//            intervals.add(interval);
//        }

        /* Update the "latest seen time" */
        if (stateEndTime > latestTime) {
            latestTime = stateEndTime;
        }

	}

	@Override
    public void doQuery(List<ITmfStateInterval> currentStateInfo, long t)
            throws TimeRangeException {
        if (!checkValidTime(t)) {
            throw new TimeRangeException(ssid + " Time:" + t + ", Start:" + startTime + ", End:" + latestTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        /*
         * The intervals are sorted by end time, so we can binary search to get
         * the first possible interval, then only compare their start times.
         */
//        synchronized (intervals) {
//            Iterator<ITmfStateInterval> iter = serachforEndTime(intervals, t);
//            for (int modCount = 0; iter.hasNext() && modCount < currentStateInfo.size();) {
//                ITmfStateInterval entry = iter.next();
//                final long entryStartTime = entry.getStartTime();
//                if (entryStartTime <= t) {
//                    /* Add this interval to the returned values */
//                    currentStateInfo.set(entry.getAttribute(), entry);
//                    modCount++;
//                }
//            }
//        }
    }
	
	@Override
    public ITmfStateInterval doSingularQuery(long t, int attributeQuark)
            throws TimeRangeException, AttributeNotFoundException {
        if (!checkValidTime(t)) {
            throw new TimeRangeException(ssid + " Time:" + t + ", Start:" + startTime + ", End:" + latestTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        /*
         * The intervals are sorted by end time, so we can binary search to get
         * the first possible interval, then only compare their start times.
         */
//        synchronized (intervals) {
//            Iterator<ITmfStateInterval> iter = serachforEndTime(intervals, t);
//            while (iter.hasNext()) {
//                ITmfStateInterval entry = iter.next();
//                final boolean attributeMatches = (entry.getAttribute() == attributeQuark);
//                final long entryStartTime = entry.getStartTime();
//                if (attributeMatches) {
//                    if (entryStartTime <= t) {
//                        /* This is the droid we are looking for */
//                        return entry;
//                    }
//                }
//            }
//        }
        throw new AttributeNotFoundException(ssid + " Quark:" + attributeQuark); //$NON-NLS-1$
    }
	 
	private boolean checkValidTime(long t) {
        if (t >= startTime && t <= latestTime) {
            return true;
        }
        return false;
    }
	 
	@Override
	public void finishedBuilding(long endTime) throws TimeRangeException {
		/* Nothing to do */
	}

	@Override
	public FileInputStream supplyAttributeTreeReader() {
		
		/* RH. Fix me */
		/* Saving to disk not supported */
        return null;
	}

	@Override
	public File supplyAttributeTreeWriterFile() {
		
		/* RH. Fix me */
		/* Saving to disk not supported */
        return null;
	}

	@Override
	public long supplyAttributeTreeWriterFilePosition() {
		
		/* RH. Fix me */
		/* Saving to disk not supported */
        return -1;
	}

	@Override
	public void removeFiles() {
		/* Nothing to do */
	}

	@Override
	public void dispose() {
		/* Nothing to do */
	}

	@Override
    public void debugPrint(PrintWriter writer) {
//        synchronized (intervals) {
//            writer.println(intervals.toString());
//        }
    }

}
