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

	private boolean hasDeviceToTest;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		List<IDevice> devices = AdbPlugin.getDefault().getDevices();
		
		int count = 0;
		
		while(count < 10){
			
			if(devices.size() > 0) {
				
				hasDeviceToTest = true;
				
				break;
				
			}
			
			count ++;
			
			System.out.println("SystraceVersionDetectorTest.setUp wait count "+ count);
			Thread.sleep(1000);
			
		}
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		
		if(!hasDeviceToTest) return;
		
		IDevice[] devices = AdbPlugin.getDefault().getAndroidDebugBridge().getDevices();
		
		for(IDevice device : devices){
		
			List<SystraceTag> mTags = new ArrayList<SystraceTag>();
			
			SystraceVersionDetector detector = new SystraceVersionDetector("detect systrace version", device, mTags);
			
			detector.setSystem(true);
			
			detector.schedule();
			
			detector.join();
			
			List<SystraceTag> mSupportedTags = detector.getTags();
			
			for (SystraceTag tag : mSupportedTags) {
				
				String sTag = tag.info;
				
	        }
			
		}
		
	}

}
