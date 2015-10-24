package net.sf.etrakr.persistent.hdf.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class DummyTest {

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
		
		String str = "";
		Assert.assertNotNull(str);
		
		
	}
	
//	@Rule
//    public ResourceFile res = new ResourceFile("/target/h5/res.txt");
//
//    @Ignore
//    public void test2() throws Exception
//    {
//    	File f = res.getFile();
//        assertTrue(res.getContent().length() == 0);
//        assertTrue(res.getFile().exists());
//    }

}
