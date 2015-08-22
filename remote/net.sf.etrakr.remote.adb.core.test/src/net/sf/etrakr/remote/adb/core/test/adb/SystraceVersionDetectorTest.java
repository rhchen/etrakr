package net.sf.etrakr.remote.adb.core.test.adb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

import net.sf.etrakr.remote.adb.core.AdbPlugin;

public class SystraceVersionDetectorTest {

	private AndroidDebugBridge adbBridge;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		int count = 0;
		
		while(true){
			
			adbBridge = AdbPlugin.getDefault().getAndroidDebugBridge();
			
			if(adbBridge != null | count > 10) break;
			
			System.out.println("SystraceVersionDetectorTest.setUp wait adb");
			
			Thread.sleep(500);
			
			count++;
		}
		
		
		
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		if(adbBridge == null) return;
		
		IDevice[] devices = adbBridge.getDevices();
		
		for(IDevice device : devices){
		
			List<SystraceTag> mTags = new ArrayList<SystraceTag>();
			
			SystraceVersionDetector detector = new SystraceVersionDetector("detect systrace version", device, mTags);
			
			detector.setSystem(true);
			
			detector.schedule();
			
		}
		
	}

}
