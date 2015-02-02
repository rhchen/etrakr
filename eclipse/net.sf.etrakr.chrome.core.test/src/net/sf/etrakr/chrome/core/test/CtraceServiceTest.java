package net.sf.etrakr.chrome.core.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import net.sf.etrakr.chrome.core.service.ICtraceService;
import net.sf.etrakr.chrome.core.service.impl.CtraceService;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.TreeBasedTable;

public class CtraceServiceTest {

	private static final File _file = new File("data/flow.json");
	
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
	public void test_0() {
		
		try {
			
			ICtraceService service = new CtraceService();
			
			service.addTrace(_file.toURI());
			
			ITmfEvent event = service.getTmfEvent(_file.toURI(), 0);
			
			Assert.assertNotNull(event);
			
			TreeBasedTable<Integer, Long, Long> pageTable = service.getPageTable(_file.toURI());
			
			BiMap<Long, Integer> ranks = service.getRankTable(_file.toURI());
			
			Long[] lAr = ranks.keySet().toArray(new Long[ranks.size()]);
			
			for(int i=0; i<lAr.length; i++){
				
				long rank = lAr[i];
				
				System.out.println("rank "+ rank);
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		String str = "";
		Assert.assertNotNull(str);
	}
	
	@Test
	public void test_1() {
		
		try {
			
			FileInputStream fis = new FileInputStream(_file.getPath());
			
			FileChannel fileChannel = fis.getChannel();
			
			ICtraceService service = new CtraceService();
			
			service.addTrace(_file.toURI());
			
			TreeBasedTable<Integer, Long, Long> table = service.getPageTable(_file.toURI());
			
			SortedSet<Integer> set = table.rowKeySet();
			
			Iterator<Integer> it = set.iterator();
			
			while(it.hasNext()){
				
				Integer page = it.next();
				
				SortedMap<Long, Long> map = table.row(page);
				
				long posStart = map.firstKey();
				
				long posEnd = map.get(posStart);
				
				//System.out.println("row : "+ page +" : "+ posStart +" : "+ posEnd);
				
				long bufferSize = posEnd - posStart;
				
				byte[] buffer = new byte[(int) bufferSize];

				MappedByteBuffer mmb = fileChannel.map(FileChannel.MapMode.READ_ONLY, posStart, bufferSize);

				mmb.get(buffer);

				String tmp = new String(buffer);
				
				//System.out.println("tmp : "+ tmp);
				
			}//while
			
			fileChannel.close();
			
			fis.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String str = "";
		Assert.assertNotNull(str);
		
	}

	@Test
	public void test_2() {
		
		ICtraceService service = new CtraceService();
		
		try {
			
			service.addTrace(_file.toURI());
			
			ITmfEvent event = service.getTmfEvent(_file.toURI(), 0);
			
			Assert.assertNotNull(event);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		
	}
}
