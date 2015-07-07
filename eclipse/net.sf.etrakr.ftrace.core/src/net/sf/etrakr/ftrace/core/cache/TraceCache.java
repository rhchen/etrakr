package net.sf.etrakr.ftrace.core.cache;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.TreeBasedTable;

public class TraceCache implements RemovalListener<Integer, ImmutableMap<Long, ITmfEvent>> {

	private TraceLoader traceLoader;
	
	private LoadingCache<Integer, ImmutableMap<Long, ITmfEvent>> cache;
	
	/**
	 * Client must call init to initialize the cache
	 * 
	 * @param fileChannel
	 * @param pageTable
	 * @param rankTable
	 */
	public void init(URI fileUri, TreeBasedTable<Integer, Long, Long> pageTable, BiMap<Long, Integer> rankTable){
		
		traceLoader = new TraceLoader(fileUri, pageTable, rankTable);
		
		cache = CacheBuilder.newBuilder().
				maximumSize(20).
				expireAfterAccess(20, TimeUnit.SECONDS).
				removalListener(this).
				build(traceLoader);
	}
	
	@Override
	public void onRemoval(RemovalNotification<Integer, ImmutableMap<Long, ITmfEvent>> notification) {
		
		System.out.println(" associated with the key("+ notification.getKey()+ ") is removed.");
		
	}

	/**
	 * Sync call to get data from cache
	 * 
	 * @param pageNumber
	 * @return
	 * @throws ExecutionException
	 */
	public ImmutableMap<Long, ITmfEvent> get(Integer pageNumber) throws ExecutionException{
		
		return cache.get(pageNumber);
	}

	/**
	 * Async call to get data from cache
	 * 
	 * @param pageNumber
	 * @param callable
	 * @return
	 * @throws ExecutionException
	 */
	public ImmutableMap<Long, ITmfEvent> get(Integer pageNumber, Callable<ImmutableMap<Long, ITmfEvent>> callable) throws ExecutionException{
		
		return cache.get(pageNumber, callable);
	}

}
