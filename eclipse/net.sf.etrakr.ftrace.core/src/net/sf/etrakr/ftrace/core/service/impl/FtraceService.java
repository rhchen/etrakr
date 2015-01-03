package net.sf.etrakr.ftrace.core.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.tukaani.xz.SeekableFileInputStream;
import org.tukaani.xz.SeekableXZInputStream;
import org.tukaani.xz.XZFormatException;
import org.tukaani.xz.XZInputStream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeBasedTable;

import net.sf.commonstringutil.StringUtil;
import net.sf.etrakr.ftrace.core.cache.TraceCache;
import net.sf.etrakr.ftrace.core.service.IFtraceService;

public class FtraceService implements IFtraceService {

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
	
	public void addTrace(URI fileUri) throws IOException{
		
		/* RH. Fix me.
		 * Should be in a separate thread*/
		createPageTable(fileUri);
		
		createCacheTable(fileUri);
		
		traceList.add(fileUri);
	}
	
	
    
	public TreeBasedTable<Integer, Long, Long> getPageTable(URI fileUri){
		
		return pageTables.get(fileUri);
		
	}
	
	public BiMap<Long, Integer> getRankTable(URI fileUri){
		
		return rankTables.get(fileUri);
	}
	
	private void createCacheTable(URI fileUri) throws FileNotFoundException{
		
		TraceCache cache = new TraceCache();
		
		cache.init(fileUri, pageTables.get(fileUri), rankTables.get(fileUri));
		
		cacheTables.put(fileUri, cache);
		
	}
	
	public ITmfEvent getTmfEvent(URI fileUri, long rank) throws ExecutionException{
		
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
	
	public static byte[] getByteArray(URI fileUri, long positionStart, long bufferSize) throws IOException{
		
		boolean isXz = isXZ(fileUri);
		
		byte[] buffer = new byte[(int) bufferSize];
		
		if(isXz){
			
			SeekableFileInputStream fis = new SeekableFileInputStream(fileUri.getPath());
	        
			SeekableXZInputStream in = new SeekableXZInputStream(fis);
	        
			in.seek(positionStart);
			
			in.read(buffer, 0, (int)bufferSize);
			
			fis.close();
			
			in.close();
			
		}else{
			
			FileInputStream fis = new FileInputStream(fileUri.getPath());

			FileChannel fileChannel = fis.getChannel();
			
			MappedByteBuffer mmb = fileChannel.map(FileChannel.MapMode.READ_ONLY, positionStart, bufferSize);

			mmb.get(buffer);
			
			fis.close();
			
			fileChannel.close();
		
		}
		
		return buffer;
	}
	
	/**
	 * 
	 * Create page table to avoid load all trace data into memory
	 * 
	 * @param fileUri The URI of the trace file
	 * @throws IOException
	 */
	private void createPageTable(URI fileUri) throws IOException{
		
		long size = FtraceService.getFileSize(fileUri);
		
		int M_BYTE = 1024 * 1024;
		
		int pages = (int) (size / M_BYTE);
		
		long rank          = -1L;
		long positionStart = 0L;
		long positionEnd   = 0L;
		String lastLine    = "";
		
		TreeBasedTable<Integer, Long, Long> pageTable = TreeBasedTable.<Integer, Long, Long>create();
		BiMap<Long, Integer> rankTable = HashBiMap.<Long, Integer>create();
				
		for(int i=0; i<=pages; i++){
			
			long limit = (i+1) * M_BYTE > size ? size : (i+1) * M_BYTE;
			
			//long bufferSize = limit - (i * M_BYTE);
			long bufferSize = limit - positionStart;
			
			byte[] buffer = FtraceService.getByteArray(fileUri, positionStart, bufferSize);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));
			//CRLFLineReader in = new CRLFLineReader(new InputStreamReader(new ByteArrayInputStream(buffer)));
			//LineInput in = new LineInput(new ByteArrayInputStream(buffer));
			
			/* Do a guess to skip the first 3 lines */
			positionStart = i == 0 ? skipLines(in, 3) : positionStart;
			
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				
				lastLine = line;
				
				rank = isLineMatch(line) == true ? rank+1 : rank;
				
			}//for
			
			in.close();
			
			positionEnd = i == pages ? limit : limit - lastLine.getBytes().length;
			
			pageTable.put(i, positionStart, positionEnd);
			
			rank--;
			
			rankTable.put(rank, i);
			
			positionStart = positionEnd;
		}
		
		pageTables.put(fileUri, pageTable);
		
		rankTables.put(fileUri, rankTable);
		
	}
	
	public static boolean isLineMatch(String line){
		
		/* ignore empty string */
		if(line.length() == 0) return false;
		
		/* Ignore space line */
		if(StringUtil.ltrim(line).length() == 0) return false;
		
		boolean isFind = StringUtil.startsWithIgnoreCase(line, "#");
		
		return !isFind;
	} 
	/**
	 * 
	 * Skips number of lines to reduce dummy check
	 * <p>
	 * In Systrace header
	 * </p>
	 * <pre>
	  
		  var linuxPerfData = "\
		# tracer: nop\n\
		#\n\
		#           TASK-PID    CPU#    TIMESTAMP  FUNCTION\n\
		#              | |       |          |         |\n\
		
	 * </pre>
	 * <p>
	 * In Ftrace header
	 * </p>
	 * <pre>
		# tracer: nop
		#
		#           TASK-PID    CPU#    TIMESTAMP  FUNCTION
		#              | |       |          |         |
	 
	 * </pre>
	 * 
	 * @param in BufferedReader
	 * @param linesToSkip Numbers of line to skip
	 * @return The position start after skip
	 * @throws IOException
	 */
	private long skipLines(BufferedReader in, int linesToSkip) throws IOException{
		
		long positionStart = 0;
		
		/*
		 * RH. Fix me
		 * CR = 13, LF = 10
		 * On window is CRLF
		 * On Linux is LF
		 * 
		 * When first byte is CR, the log is in window format
		 * There is assum the first line is empty string, should be reviewd
		 */
		int add = 13 == in.read()? 2 : 1;
		
		for(int i=0; i<linesToSkip; i++){
			
			String line = in.readLine();
			
			/*
			 * RH. Fix me
			 * A hack of the readline. case the systrace input contain \n\ in every line
			 * increase the length to workaround it
			 * Ex. 
			 *  # tracer: nop\n\
				#\n\
				#           TASK-PID    CPU#    TIMESTAMP  FUNCTION\n\
				#              | |       |          |         |\n\
			 */
			positionStart += line.getBytes().length + add; // +1 cause "\" character
		}
		
		/* +1 to return to next line start */
		return positionStart;
	}
}
