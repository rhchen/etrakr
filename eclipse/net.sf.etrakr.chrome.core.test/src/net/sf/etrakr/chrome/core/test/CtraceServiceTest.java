package net.sf.etrakr.chrome.core.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;

import net.sf.etrakr.chrome.core.service.ICtraceService;
import net.sf.etrakr.chrome.core.service.impl.CtraceService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
	public void test() {
		
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
				
				System.out.println("row : "+ page +" : "+ posStart +" : "+ posEnd);
				
				long bufferSize = posEnd - posStart;
				
				byte[] buffer = new byte[(int) bufferSize];

				MappedByteBuffer mmb = fileChannel.map(FileChannel.MapMode.READ_ONLY, posStart, bufferSize);

				mmb.get(buffer);

				String tmp = new String(buffer);
				
				System.out.println("tmp : "+ tmp);
				
			}//while
			
			fileChannel.close();
			
			fis.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		String str = "";
		Assert.assertNotNull(str);
		
	}

}
