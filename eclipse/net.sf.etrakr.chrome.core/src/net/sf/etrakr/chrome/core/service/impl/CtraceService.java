package net.sf.etrakr.chrome.core.service.impl;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.TreeBasedTable;

import net.sf.etrakr.chrome.core.service.ICtraceService;

public class CtraceService implements ICtraceService {

	@Override
	public void addTrace(URI fileURI) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeBasedTable<Integer, Long, Long> getPageTable(URI fileUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BiMap<Long, Integer> getRankTable(URI fileUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITmfEvent getTmfEvent(URI fileUri, long rank)
			throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

}
