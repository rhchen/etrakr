package net.sf.etrakr.tmf.remote.adb.core.test;

import static org.junit.Assert.*;

import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService;

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
		
		String r = adbService.getSystraceSupportTags();
		
		Assert.assertNotNull(r);
	}

}
