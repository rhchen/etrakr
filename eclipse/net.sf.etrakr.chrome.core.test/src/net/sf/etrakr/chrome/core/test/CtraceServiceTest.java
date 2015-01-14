package net.sf.etrakr.chrome.core.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sf.etrakr.chrome.core.service.ICtraceService;
import net.sf.etrakr.chrome.core.service.impl.CtraceService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
			
			ICtraceService service = new CtraceService();
			
			service.addTrace(_file.toURI());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String str = "";
		Assert.assertNotNull(str);
		
	}

}
