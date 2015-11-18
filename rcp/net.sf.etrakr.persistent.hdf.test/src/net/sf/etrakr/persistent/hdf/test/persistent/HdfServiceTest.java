package net.sf.etrakr.persistent.hdf.test.persistent;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import net.sf.etrakr.persistent.hdf.serivce.impl.HdfService;

public class HdfServiceTest {

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
	
	@Test
	public void test1() throws HDF5Exception, Exception {
		
		HdfService hdfService = HdfService.instance();
		
		Assert.assertNotNull(hdfService);
		
		hdfService.open().close();
	}

}
