package net.sf.etrakr.tmf.remote.adb.core.test;

import org.junit.Assert;

import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService;
import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService.SystraceTag;

public class TmfAdbServiceTest {

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
	public void testGetSystraceSupportTags() throws ExecutionException, RemoteConnectionException, URISyntaxException {
		
		TmfAdbService adbService = new TmfAdbService();
		
		List<SystraceTag> tags = adbService.getSystraceSupportTags();
		
		System.out.println("TmfAdbServiceTest.testGetSystraceSupportTags tags.size "+ tags.size());
		
		Assert.assertNotNull(tags);
	}
	
	@Test
	public void testGetSystraceOutput() throws ExecutionException, RemoteConnectionException, URISyntaxException {
		
		TmfAdbService adbService = new TmfAdbService();
		
		String atraceOutput = adbService.getSystraceOutput();
		
		System.out.println("TmfAdbServiceTest.testGetSystraceOutput "+ atraceOutput);
		
		Assert.assertNotNull(atraceOutput);
	}

}
