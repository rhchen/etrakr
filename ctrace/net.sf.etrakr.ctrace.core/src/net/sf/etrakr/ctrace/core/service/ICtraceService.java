package net.sf.etrakr.ctrace.core.service;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.TreeBasedTable;

public interface ICtraceService {

	public void addTrace(URI fileURI) throws IOException;
	
	public TreeBasedTable<Integer, Long, Long> getPageTable(URI fileUri);
	
	public BiMap<Long, Integer> getRankTable(URI fileUri);
	
	public ITmfEvent getTmfEvent(URI fileUri, long rank) throws ExecutionException;
}
