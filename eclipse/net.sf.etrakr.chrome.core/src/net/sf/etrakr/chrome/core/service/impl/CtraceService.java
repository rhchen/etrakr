package net.sf.etrakr.chrome.core.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.tukaani.xz.SeekableFileInputStream;
import org.tukaani.xz.SeekableXZInputStream;
import org.tukaani.xz.XZFormatException;
import org.tukaani.xz.XZInputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.ImmutableList.Builder;

import net.sf.etrakr.chrome.core.cache.TraceCache;
import net.sf.etrakr.chrome.core.event.TraceEvent;
import net.sf.etrakr.chrome.core.service.ICtraceService;

public class CtraceService implements ICtraceService {

	private static final List<URI> traceList = Lists.<URI>newArrayList();
	
	/*
	 * PageTables is used to store the begin / start position of paged file
	 * Default per MB size is a page.
	 * 
	 * URI. file URI to identify the input trace
	 * TreeBasedTable<page index, start position, end position>
	 */
	private static final ConcurrentMap<URI, TreeBasedTable<Integer, Long, Long>> pageTables = Maps.<URI, TreeBasedTable<Integer, Long, Long>>newConcurrentMap();
	
	/*
	 * RankTables is used to store which rank of data store in which page
	 * A rank represents the order the a record in the trace
	 * 
	 * URI. file URI to identify the input trace
	 * BiMap<rank start of data, page number>
	 */
	private static final ConcurrentMap<URI, BiMap<Long, Integer>> rankTables = Maps.<URI, BiMap<Long, Integer>>newConcurrentMap();
	
	private static final ConcurrentMap<URI, TraceCache> cacheTables = Maps.<URI, TraceCache>newConcurrentMap();
	
	@Override
	public void addTrace(URI fileUri) throws IOException {
		
		/* RH. Fix me.
		 * Should be in a separate thread*/
		createPageTable(fileUri);
		
		createCacheTable(fileUri);
		
		traceList.add(fileUri);

	}

	@Override
	public TreeBasedTable<Integer, Long, Long> getPageTable(URI fileUri) {
		
		return pageTables.get(fileUri);
	
	}

	@Override
	public BiMap<Long, Integer> getRankTable(URI fileUri) {
		
		return rankTables.get(fileUri);
	
	}

	@Override
	public ITmfEvent getTmfEvent(URI fileUri, long rank)
			throws ExecutionException {
		
		BiMap<Long, Integer> bMap = rankTables.get(fileUri);
		
		TreeMap<Long, Integer> tMap = Maps.<Long, Integer>newTreeMap();
		
		tMap.putAll(bMap);
		
		long prevK = tMap.firstKey();
		
		if(rank >= prevK) {
			
			Iterator<Long> it = tMap.keySet().iterator();
			
			while(it.hasNext()){
				
				long k = it.next();
				
				prevK = k;
				
				if(rank <= k) break;
				
			}
			
		}
		
		int pageNumber = rankTables.get(fileUri).get(prevK);
		
		ImmutableMap<Long, ITmfEvent> data = cacheTables.get(fileUri).get(pageNumber);
		
		Assert.isNotNull(data);
		
		return data.get(rank);
	}

	private void createCacheTable(URI fileUri) throws FileNotFoundException{
		
		TraceCache cache = new TraceCache();
		
		cache.init(fileUri, pageTables.get(fileUri), rankTables.get(fileUri));
		
		cacheTables.put(fileUri, cache);
		
	}

	private void createPageTable(URI fileUri) throws IOException{
		
		TreeBasedTable<Integer, Long, Long> pageTable = TreeBasedTable.<Integer, Long, Long>create();
		BiMap<Long, Integer> rankTable = HashBiMap.<Long, Integer>create();
		TreeBasedTable<Integer, Long, Long> tmpTable = TreeBasedTable.<Integer, Long, Long>create();
		
		int recordsOfPage = 1000;
		int records = 0;
		int page = 0;
		long currentRank = 0;
		
		ObjectMapper mm = new ObjectMapper();
		mm.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mm.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		
    	JsonParser jp = mm.getFactory().createParser(fileUri.toURL());
    	jp.configure(JsonParser.Feature.ALLOW_COMMENTS,true);
    	jp.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS,true);
    	jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
    	jp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
    	
		jp.nextToken();// will return JsonToken.START_OBJECT
		
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			
			JsonToken token = jp.nextToken();// move to value, or START_OBJECT/START_ARRAY
			
			String fieldname = jp.getCurrentName();
			
			if(!"traceEvents".equalsIgnoreCase(fieldname)) continue;
			
			long startPos = 0;
			long endPos = 0;
			
			/* In depth to 0, that a end of a trace record */
			int eventDepth = 0;
			
			while (token != JsonToken.END_ARRAY){//move to the array which contains per record data
				
				token = jp.nextToken(); // move to value
				
				switch(token){
				
					case START_OBJECT :
						
						if(eventDepth == 0){
							startPos = jp.getCurrentLocation().getByteOffset();
							currentRank ++;
						}
						
						eventDepth ++;
						
						break;
						
					case END_OBJECT :
						
						eventDepth --;
						
						if(eventDepth == 0){ 
							
							endPos = jp.getCurrentLocation().getByteOffset();
							
							/* Fix me, should validate data */
							tmpTable.put(page, startPos - 1, endPos);
							
							records++;
							
							if(records == recordsOfPage){
								
								SortedMap<Long, Long> map = tmpTable.row(page);
								
								long firstKey = map.firstKey();
								
								long lastKey = map.lastKey();
								
								long lastValue = map.get(lastKey);
								
								pageTable.put(page, firstKey, lastValue);

								rankTable.put(currentRank, page);
								
								/* Reset counters */
								page++;
								records = 0;
								tmpTable = TreeBasedTable.<Integer, Long, Long>create();
								
							}//if
							
						}

						break;
						
					default :
						break;
					
				}//switch

			}//while
			
		}//while
		
		SortedMap<Long, Long> map = tmpTable.row(page);
		
		long firstKey = map.firstKey();
		
		long lastKey = map.lastKey();
		
		long lastValue = map.get(lastKey);
		
		pageTable.put(page, firstKey, lastValue);

		rankTable.put(currentRank, page);
		
		pageTables.put(fileUri, pageTable);
		
		rankTables.put(fileUri, rankTable);
	}
	
	public static byte[] getByteArray(URI fileUri, long positionStart, long bufferSize) throws IOException{
		
		byte[] buffer = new byte[(int) bufferSize];
		
		FileInputStream fis = new FileInputStream(fileUri.getPath());

		FileChannel fileChannel = fis.getChannel();
		
		MappedByteBuffer mmb = fileChannel.map(FileChannel.MapMode.READ_ONLY, positionStart, bufferSize);

		mmb.get(buffer);
		
		fis.close();
		
		fileChannel.close();
		
		return buffer;
	}
	
	private static boolean isXZ(URI fileUri){
	
		boolean isXZ = false;
		
		try{
			
			InputStream fis = new XZInputStream(new FileInputStream(fileUri.getPath()));
			
			fis.close();
			
			isXZ = true;
			
		}catch(XZFormatException e){
			
			//e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		return isXZ;
		
	}
	
	public static long getFileSize(URI fileUri) throws IOException{
		
		/* Fix Me, should be merged to abstract class*/
		
		long size;
		
		boolean isXz = isXZ(fileUri);
		
		if(isXz){
			
			SeekableFileInputStream fis = new SeekableFileInputStream(fileUri.getPath());
	        
			SeekableXZInputStream in = new SeekableXZInputStream(fis);
			
			size = in.length();
			
			fis.close();
			
			in.close();
			
		}else{
			
			FileInputStream fis = new FileInputStream(fileUri.getPath());
			
			FileChannel fileChannel = fis.getChannel();
			
			size = fileChannel.size();
			
			fis.close();
			
			fileChannel.close();
		}
		
		return size;
	}
}
