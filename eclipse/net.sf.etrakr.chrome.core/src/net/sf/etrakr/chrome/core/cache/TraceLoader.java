package net.sf.etrakr.chrome.core.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.commonstringutil.StringUtil;
import net.sf.etrakr.chrome.core.CtraceStrings;
import net.sf.etrakr.chrome.core.event.TraceEvent;
import net.sf.etrakr.chrome.core.event.Zoo;
import net.sf.etrakr.chrome.core.event.impl.CtraceEvent;
import net.sf.etrakr.chrome.core.service.impl.CtraceService;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.event.TmfEventField;
import org.eclipse.linuxtools.tmf.core.event.TmfEventType;
import org.eclipse.linuxtools.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.linuxtools.tmf.core.timestamp.TmfTimestamp;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.UnmodifiableIterator;

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
		
		byte[] buffer = CtraceService.getByteArray(this._fileUri, positionStart, bufferSize);
		
		ObjectMapper mm = new ObjectMapper();
		mm.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mm.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		
		/* Fix me, append root to a valid json format */
		String tmp = new String(buffer);
		tmp = "{\"traceEvents\" : [" + tmp + "]}";
		
		JsonParser jp = mm.getFactory().createParser(tmp);
		
		Zoo z = jp.readValueAs(Zoo.class);
		
		ImmutableList<TraceEvent> list = ImmutableList.<TraceEvent>builder().addAll(z.traceEvents).build();;
		
		UnmodifiableIterator<TraceEvent> it = list.iterator();
		
		while(it.hasNext()){
			
			this._currentRank ++;
			
			ITmfEvent evt = handleJsonObjectToTmfEvent(it.next());
			
			builder.put(this._currentRank, evt);
			
		}
		
		
		return builder.build();
	}
	
	private ITmfEvent handleJsonObjectToTmfEvent(TraceEvent evt){
		
		TmfTimestamp ts = new TmfTimestamp(evt.ts,ITmfTimestamp.NANOSECOND_SCALE);
		
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));

		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		final TmfEventField tmfEventField_NAME = new TmfEventField(CtraceStrings.NAME, evt.name, null); //$NON-NLS-1$
		eventList.add(tmfEventField_NAME);
		
		final TmfEventField tmfEventField_TID = new TmfEventField(CtraceStrings.TID, evt.tid, null); //$NON-NLS-1$
		eventList.add(tmfEventField_TID);
		
		final TmfEventField tmfEventField_PID = new TmfEventField(CtraceStrings.PID, evt.pid, null); //$NON-NLS-1$
		eventList.add(tmfEventField_PID);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);

		CtraceEvent event = new CtraceEvent(null, _currentRank, ts, 
				String.valueOf(this._currentRank),
				new TmfEventType(evt.cat, evt.cat, null), 
				content, evt.ph, 0, evt.cat);
		
		return event;
		
	}
	
}
