package net.sf.etrakr.ftrace.core.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;
import org.eclipse.tracecompass.tmf.core.event.TmfEventField;
import org.eclipse.tracecompass.tmf.core.event.TmfEventType;
import org.eclipse.tracecompass.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimestamp;

import net.sf.commonstringutil.StringUtil;
import net.sf.etrakr.ftrace.core.FtraceStrings;
import net.sf.etrakr.ftrace.core.event.impl.FtraceEvent;
import net.sf.etrakr.ftrace.core.service.impl.FtraceService;


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
		
		byte[] buffer = FtraceService.getByteArray(this._fileUri, positionStart, bufferSize);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));
		
		String line;
		
		/* Another way is to use regular expression, but seems slower
		 * 
		Pattern pt_sched_switch = Pattern.compile("(?i).*sched_switch.*", Pattern.CASE_INSENSITIVE);
		*/
		
		for (line = in.readLine(); line != null; line = in.readLine()) {
			
			//boolean isFind = pt_sched_switch.matcher(line).find();
			
			boolean isFind = FtraceService.isLineMatch(line);
			
			if(isFind){
				
				this._currentRank ++;
				
				/* RH. Fix me
				 * Should have better way to do it */
				if(StringUtil.countText(line, FtraceStrings.SCHED_SWITCH) > 0){
					
					ITmfEvent event = handleSchedleSwitchEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
					
				}else if(StringUtil.countText(line, FtraceStrings.SCHED_WAKEUP) > 0 ||
						 StringUtil.countText(line, FtraceStrings.SCHED_WAKEUP_NEW) > 0){
					
					ITmfEvent event = handleSchedleWakeupEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
					
				}else if(StringUtil.countText(line, FtraceStrings.SOFTIRQ_RAISE) > 0 ||
						 StringUtil.countText(line, FtraceStrings.SOFTIRQ_ENTRY) > 0 ||
						 StringUtil.countText(line, FtraceStrings.SOFTIRQ_EXIT) > 0){
					
					ITmfEvent event = handleSoftIrqEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
					
				}else if(StringUtil.countText(line, FtraceStrings.IRQ_HANDLER_ENTRY) > 0 ||
				         StringUtil.countText(line, FtraceStrings.IRQ_HANDLER_EXIT) > 0){
					
					ITmfEvent event = handleIrqEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
					
				}else if(StringUtil.countText(line, FtraceStrings.SCHED_PROCESS_FORK) > 0){
					
					ITmfEvent event = handleSchedleProcessForkEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
					
				}else if(StringUtil.countText(line, FtraceStrings.SCHED_PROCESS_EXIT) > 0 ||
						 StringUtil.countText(line, FtraceStrings.SCHED_PROCESS_FREE) > 0){
					
					ITmfEvent event = handleSchedleProcessFreeEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
					
				}else if(StringUtil.countText(line, "trace_event_clock_sync") > 0){
					
					/* 
					 * The dummy event is the last event of the trace
					 * The type is trace_event_clock_sync, just ignore it, ex
					 * 
					 * 	dummy-0000  [000] 0.0: 0: trace_event_clock_sync: parent_ts=0.0\n";
					 * 
					 * When trace iterate to this, return null to lead to escape the trace parse
					 * Here we do nothing
					 */
					continue;
					
				}else{
					
					ITmfEvent event = handleUndefinedEvent(line);
					
					dataMap.put(this._currentRank, event);
					
					continue;
				}
				
			}else{
				
				System.out.println("line ignore "+ line);
			}
			
		}//for
		
		in.close();
		
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
	
	private final Head parseHead(String line){
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(line);
		
		short cpuId    = 0;
		long timeStamp = 0L;
		String title   = "undefine";
		String suffStr = "undefine";
		
		while(scan.hasNext()){
			
			String sn = scan.next();
			
			/* Expect to read [00.] */
			if(StringUtil.countText(sn, "[0") > 0){
				
				/* Get CPU id, Ex. [000] */
				sn = StringUtil.remove(sn, "[");
				sn = StringUtil.remove(sn, "]");
				cpuId = Short.parseShort(sn);
				
				/* Get Timestamp, Ex. 50260.647833: */
				sn = scan.next();
				
				if(sn.length() == 4) sn = scan.next();
				
				sn = StringUtil.remove(sn, ":");
				sn = StringUtil.remove(sn, ".");
				timeStamp =Long.parseLong(sn) * 1000L;
				
				/* Get Event Type Ex. sched_wakeup: */
				sn = scan.next();
				title = StringUtil.remove(sn, ":").trim().intern();
			
				/* Content of the event depends */
				@SuppressWarnings("unchecked")
				List<String> list = StringUtil.splitAsList(line, sn);
				
				suffStr = list.get(1).trim();
				
				break;
				
			}//if
			
			
		}//while
		
		return new Head(cpuId, timeStamp, title, suffStr);
	}
	
	private final ITmfEvent handleSchedleProcessFreeEvent(String line){
		
		Head head = parseHead(line);

		String suffStr = head.suffStr;
		suffStr = StringUtil.replace(suffStr, "comm" , "||");
		suffStr = StringUtil.replace(suffStr, "pid"  , "||");
		suffStr = StringUtil.replace(suffStr, "prio"  , "||");
		
		@SuppressWarnings("unchecked")
		List<String> rlist = StringUtil.splitAsList(suffStr, "||=");
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));
		
		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		/* The comm is optional, could be safe removed */
		final TmfEventField tmfEventField_COMM = new TmfEventField(FtraceStrings.COMM, rlist.get(1).trim(), null); //$NON-NLS-1$
		eventList.add(tmfEventField_COMM);
		
		final TmfEventField tmfEventField_TID = new TmfEventField(FtraceStrings.TID, Long.parseLong(rlist.get(2).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_TID);
		
		/* The prio is optional, could be safe removed */
		final TmfEventField tmfEventField_PRIO = new TmfEventField(FtraceStrings.PRIO, Long.parseLong(rlist.get(3).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_PRIO);
		
		/* the field must be in an array */
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);		
		final TmfEventField  content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);
		
		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, suffStr, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, suffStr, head.cpuId, head.title);
		
		return event;
	}
	
	private final ITmfEvent handleSchedleProcessForkEvent(String line){
		
		Head head = parseHead(line);

		String suffStr = head.suffStr;
		
		/* The order of replacement is tricky */
		suffStr = StringUtil.replace(suffStr, "child_comm"  , "||");
		suffStr = StringUtil.replace(suffStr, "child_pid"  , "||");
		suffStr = StringUtil.replace(suffStr, "comm" , "||");
		suffStr = StringUtil.replace(suffStr, "pid"  , "||");
		
		@SuppressWarnings("unchecked")
		List<String> rlist = StringUtil.splitAsList(suffStr, "||=");
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));
		
		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		/* The comm is optional, could be safe removed */
		final TmfEventField tmfEventField_COMM = new TmfEventField(FtraceStrings.COMM, rlist.get(1).trim(), null); //$NON-NLS-1$
		eventList.add(tmfEventField_COMM);
		
		final TmfEventField tmfEventField_PARENT_TID = new TmfEventField(FtraceStrings.PARENT_TID, Long.parseLong(rlist.get(2).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_PARENT_TID);
		
		final TmfEventField tmfEventField_CHILD_COMM = new TmfEventField(FtraceStrings.CHILD_COMM, rlist.get(3).trim(), null); //$NON-NLS-1$
		eventList.add(tmfEventField_CHILD_COMM);
		
		final TmfEventField tmfEventField_CHILD_TID = new TmfEventField(FtraceStrings.CHILD_TID, Long.parseLong(rlist.get(4).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_CHILD_TID);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);
		
		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, suffStr, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, suffStr, head.cpuId, head.title);
		
		return event;
	}

	private final ITmfEvent handleIrqEvent(String line){
		
		Head head = parseHead(line);

		String suffStr = head.suffStr;
		suffStr = StringUtil.replace(suffStr, "irq" , "||");
		suffStr = StringUtil.replace(suffStr, "name"  , "||");
		suffStr = StringUtil.replace(suffStr, "ret"  , "||");
		
		@SuppressWarnings("unchecked")
		List<String> rlist = StringUtil.splitAsList(suffStr, "||=");
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));
		
		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		final TmfEventField tmfEventField_IRQ = new TmfEventField(FtraceStrings.IRQ, Long.parseLong(rlist.get(1).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_IRQ);
		
		/* The name is optional, could be safe removed */
		final TmfEventField tmfEventField_NAME = new TmfEventField(FtraceStrings.IRQ_NAME, rlist.get(2).trim(), null); //$NON-NLS-1$
		eventList.add(tmfEventField_NAME);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);
		
		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, suffStr, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, suffStr, head.cpuId, head.title);
		
		return event;
	}

	private final ITmfEvent handleSoftIrqEvent(String line){
		
		Head head = parseHead(line);

		String suffStr = head.suffStr;
		suffStr = StringUtil.remove(suffStr, "[");
		suffStr = StringUtil.remove(suffStr, "]");
		suffStr = StringUtil.replace(suffStr, "vec" , "||");
		suffStr = StringUtil.replace(suffStr, "action"  , "||");
		
		@SuppressWarnings("unchecked")
		List<String> rlist = StringUtil.splitAsList(suffStr, "||=");
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));
		
		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		final TmfEventField tmfEventField_VEC = new TmfEventField(FtraceStrings.VEC, Long.parseLong(rlist.get(1).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_VEC);
		
		/* The action field is optional, could be safe remove */
		final TmfEventField tmfEventField_ACTION = new TmfEventField(FtraceStrings.ACTION, rlist.get(2).trim(), null); //$NON-NLS-1$
		eventList.add(tmfEventField_ACTION);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);
		
		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, suffStr, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, suffStr, head.cpuId, head.title);
		
		return event;
	}
	
	private final ITmfEvent handleUndefinedEvent(String line){
		
		Head head = parseHead(line);
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));
		
		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		// put the value in a field
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);
		
		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, line, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, line, head.cpuId, head.title);
		
		return event;
	}
	
	private final ITmfEvent handleSchedleWakeupEvent(String line){
		
		Head head = parseHead(line);

		String suffStr = head.suffStr;
		suffStr = StringUtil.replace(suffStr, "comm" , "||");
		suffStr = StringUtil.replace(suffStr, "pid"  , "||");
		suffStr = StringUtil.replace(suffStr, "prio" , "||");
		suffStr = StringUtil.replace(suffStr, "success", "||");
		suffStr = StringUtil.replace(suffStr, "target_cpu" , "||");
		
		@SuppressWarnings("unchecked")
		List<String> rlist = StringUtil.splitAsList(suffStr, "||=");
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));
		
		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		final TmfEventField tmfEventField_TID = new TmfEventField(FtraceStrings.TID, Long.parseLong(rlist.get(2).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_TID);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);
		
		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, suffStr, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, suffStr, head.cpuId, head.title);
		
		return event;
	}
	
	private ITmfEvent handleSchedleSwitchEvent(String line){
		
		Head head = parseHead(line);
		
		String suffStr = head.suffStr;
		suffStr = StringUtil.replace(suffStr, "==>", "");
		suffStr = StringUtil.replace(suffStr, "prev_comm" , "||");
		suffStr = StringUtil.replace(suffStr, "prev_pid"  , "||");
		suffStr = StringUtil.replace(suffStr, "prev_prio" , "||");
		suffStr = StringUtil.replace(suffStr, "prev_state", "||");
		suffStr = StringUtil.replace(suffStr, "next_comm" , "||");
		suffStr = StringUtil.replace(suffStr, "next_pid"  , "||");
		suffStr = StringUtil.replace(suffStr, "next_prio" , "||");
		
		@SuppressWarnings("unchecked")
		List<String> rlist = StringUtil.splitAsList(suffStr, "||=");
		
		TmfTimestamp ts = new TmfTimestamp(head.timeStamp,ITmfTimestamp.NANOSECOND_SCALE);
		Random rnd = new Random();
		long payload = Long.valueOf(rnd.nextInt(10));

		final List<TmfEventField> eventList = Lists.<TmfEventField>newArrayList();
		
		/* Put the value in a field
		 * The field is required by SystraceStateProvider.eventHandle()*/
		final TmfEventField tmfEventField = new TmfEventField("value", payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField);
		
		final TmfEventField tmfEventField_PREV_TID = new TmfEventField(FtraceStrings.PREV_TID, Long.parseLong(rlist.get(2).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_PREV_TID);
		
		final TmfEventField tmfEventField_PREV_STATE = new TmfEventField(FtraceStrings.PREV_STATE, payload, null); //$NON-NLS-1$
		eventList.add(tmfEventField_PREV_STATE);
		
		final TmfEventField tmfEventField_NEXT_COMM = new TmfEventField(FtraceStrings.NEXT_COMM, rlist.get(5).trim(), null); //$NON-NLS-1$
		eventList.add(tmfEventField_NEXT_COMM);
		
		final TmfEventField tmfEventField_NEXT_TID = new TmfEventField(FtraceStrings.NEXT_TID, Long.parseLong(rlist.get(6).trim()), null); //$NON-NLS-1$
		eventList.add(tmfEventField_NEXT_TID);
		
		final TmfEventField[] fields = eventList.toArray(new TmfEventField[eventList.size()]);
		final TmfEventField content = new TmfEventField(ITmfEventField.ROOT_FIELD_ID, null, fields);

		/* Fix me
		 * The source and sourceCPU is the same, should fix
		 */
		//FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(this._currentRank),new TmfEventType(head.title, head.title, null), content, suffStr, head.cpuId, head.title);
		FtraceEvent event = new FtraceEvent(null, _currentRank, ts, String.valueOf(head.cpuId),new TmfEventType(head.title, null), content, suffStr, head.cpuId, head.title);
				
		return event;
	}

}
