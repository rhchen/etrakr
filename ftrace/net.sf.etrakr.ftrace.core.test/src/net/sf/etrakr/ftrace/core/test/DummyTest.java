package net.sf.etrakr.ftrace.core.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import net.sf.etrakr.ftrace.core.service.IFtraceService;
import net.sf.etrakr.ftrace.core.service.impl.FtraceService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.TreeBasedTable;

public class DummyTest {

	private static final File _file = new File("data/android_systrace.txt");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, ExecutionException {
		
		IFtraceService service = new FtraceService();
		
		Assert.assertNotNull(service);
		
		service.addTrace(_file.toURI());
		
		TreeBasedTable<Integer, Long, Long> pageTable = service.getPageTable(_file.toURI());
		
		int pages = pageTable.size();
		
		BiMap<Long, Integer> rankTable = service.getRankTable(_file.toURI());
		
		Iterator<Entry<Long, Integer>> it = rankTable.entrySet().iterator();
		
		while(it.hasNext()){
			
			Entry<Long, Integer> entry = it.next();
			
			String entryData = entry.getKey() +" "+ entry.getValue();
			
			System.out.println("IFtraceService entryData = "+ entryData);
		}
		
		service.getTmfEvent(_file.toURI(), 0);
	}
	
}
