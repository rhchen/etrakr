package net.sf.etrakr.chrome.core.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.commonstringutil.StringUtil;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.event.TmfEventField;
import org.eclipse.linuxtools.tmf.core.event.TmfEventType;
import org.eclipse.linuxtools.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.linuxtools.tmf.core.timestamp.TmfTimestamp;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeBasedTable;

public class TraceLoader extends CacheLoader<Integer, ImmutableMap<Long, ITmfEvent>>{

	private URI _fileUri;
	
	/*
	 * RankTables is used to store which rank of data store in which page
	 * A rank represents the order the a record in the trace
	 * 
	 * BiMap<rank start of data, page number>
	 */
	private BiMap<Long, Integer> _rankTable;
	
	/*
	 * PageTable is used to store the begin / start position of paged file
	 * Default per MB size is a page.
	 * 
	 * TreeBasedTable<page index, start position, end position>
	 */
	private TreeBasedTable<Integer, Long, Long> _pageTable;
	
	private long _currentRank;
	
	public TraceLoader(URI fileUri, TreeBasedTable<Integer, Long, Long> pageTable, BiMap<Long, Integer> rankTable) {
		
		super();
		this._fileUri = fileUri;
		this._pageTable   = pageTable;
		this._rankTable   = rankTable;
	}
	
	@Override
	public ImmutableMap<Long, ITmfEvent> load(Integer pageNum) throws Exception {
		
		Builder<Long, ITmfEvent> builder = ImmutableMap.<Long, ITmfEvent>builder();
		
		ConcurrentMap<Long, ITmfEvent> dataMap = Maps.<Long, ITmfEvent>newConcurrentMap();
		
		/* Set the current rank map to the page number 
		 * This would be the position of the TMF event instance */
		this._currentRank = pageNum == 0 ? -1 : _rankTable.inverse().get(pageNum-1);
				
		SortedMap<Long, Long> map = _pageTable.row(pageNum);
		
		long positionStart = map.firstKey();
		long positionEnd   = map.get(positionStart);
		long bufferSize = positionEnd - positionStart;
		
		
		return builder.putAll(dataMap).build();
	}
	
	/* Inner class to store header of line */
	private final class Head{
	
		public short cpuId    = 0;
		public long timeStamp = 0L;
		public String title   = "undefine";
		public String suffStr = "undefine";
		
		public Head(final short cpuId, final long timeStamp, final String title, final String suffStr){
			
			this.cpuId     = cpuId;
			this.timeStamp = timeStamp;
			this.title     = title;
			this.suffStr   = suffStr;
		}
	}
	
}
