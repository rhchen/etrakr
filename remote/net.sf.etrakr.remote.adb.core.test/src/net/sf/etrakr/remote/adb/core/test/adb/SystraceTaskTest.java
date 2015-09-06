package net.sf.etrakr.remote.adb.core.test.adb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;

import net.sf.etrakr.remote.adb.core.AdbPlugin;

public class SystraceTaskTest {

	private boolean hasDeviceToTest;
	
	private static Set<String> sEnabledTags = new HashSet<String>();
	
	private List<SystraceTag> mSupportedTags;
	
	private final SystraceOptions mOptions = new SystraceOptions();
	
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
			
			System.out.println("SystraceTaskTest.setUp wait count "+ count);
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
			
			mSupportedTags = detector.getTags();

			for (SystraceTag tag : mSupportedTags) {
				
				String sTag = tag.info;

	        }
			
			sEnabledTags.clear();
			
	        for (int i = 0; i < mSupportedTags.size(); i++) {
	        	
	        	sEnabledTags.add(mSupportedTags.get(i).tag);
	        
	        }
	        
	        mOptions.mTraceDuration = 5;
	        mOptions.mTraceBufferSize = 1024;
	        
	        String tag = mOptions.getTags();
	        
	        if (tag != null) {
	            CountDownLatch setTagLatch = new CountDownLatch(1);
	            CollectingOutputReceiver receiver = new CollectingOutputReceiver(setTagLatch);
	            try {
	                String cmd = "setprop debug.atrace.tags.enableflags " + tag;
	                device.executeShellCommand(cmd, receiver);
	                setTagLatch.await(5, TimeUnit.SECONDS);
	            } catch (Exception e) {
	               e.printStackTrace();
	            }

	            String shellOutput = receiver.getOutput();
	            if (shellOutput.contains("Error type")) {                   //$NON-NLS-1$
	                throw new RuntimeException(receiver.getOutput());
	            }
	        }
	        
	        boolean COMPRESS_DATA = true;
			
			final String atraceOptions = mOptions.getOptions() + (COMPRESS_DATA ? " -z" : "");
			
			SystraceTask task = new SystraceTask(device, atraceOptions);
			Thread t = new Thread(task, "Systrace Output Receiver");
			t.start();
			t.join();
			
			byte[] b = task.getAtraceOutput();
			
//			String s = new String(b);
//			System.out.println("SystraceTaskTest.test : "+ s);
			
			handleData(b);
			
			String s = getSystraceData(COMPRESS_DATA);
			
			System.out.println("SystraceTaskTest.test : "+ s);
		}
		
	}
	
	private static final String TRACE_START = "TRACE:\n"; //$NON-NLS-1$

    private byte[] mAtraceOutput;
    private int mAtraceLength;
    private int mSystraceIndex = -1;
    
	private void handleData(byte[] atraceOutput){
		
		mAtraceOutput = atraceOutput;
        mAtraceLength = atraceOutput.length;
        
        removeCrLf();
        
        // locate the trace start marker within the first hundred bytes
        String header = new String(mAtraceOutput, 0, Math.min(100, mAtraceLength));
        mSystraceIndex = locateSystraceData(header);

        if (mSystraceIndex < 0) {
            throw new RuntimeException("Unable to find trace start marker 'TRACE:':\n" + header);
        }
	}
	
	/** Replaces \r\n with \n in {@link #mAtraceOutput}. */
    private void removeCrLf() {
        int dst = 0;
        for (int src = 0; src < mAtraceLength - 1; src++, dst++) {
            byte copy;
            if (mAtraceOutput[src] == '\r' && mAtraceOutput[src + 1] == '\n') {
                copy = '\n';
                src++;
            } else {
                copy = mAtraceOutput[src];
            }
            mAtraceOutput[dst] = copy;
        }

        mAtraceLength = dst;
    }
    
    private int locateSystraceData(String header) {
        int index = header.indexOf(TRACE_START);
        if (index < 0) {
            return -1;
        } else {
            return index + TRACE_START.length();
        }
    }
    
    public String getSystraceData(boolean mUncompress) {
        if (mSystraceIndex < 0) {
            return "";
        }

        String trace = "";
        if (mUncompress) {
            Inflater decompressor = new Inflater();
            decompressor.setInput(mAtraceOutput, mSystraceIndex, mAtraceLength - mSystraceIndex);

            byte[] buf = new byte[4096];
            int n;
            StringBuilder sb = new StringBuilder(1000);
            try {
                while ((n = decompressor.inflate(buf)) > 0) {
                    sb.append(new String(buf, 0, n));
                }
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }
            decompressor.end();

            trace = sb.toString();
        } else {
            trace = new String(mAtraceOutput, mSystraceIndex, mAtraceLength - mSystraceIndex);
        }

        // each line should end with the characters \n\ followed by a newline
        String html_out = trace.replaceAll("\n", "\\\\n\\\\\n");
        
        return html_out;
    }
	class SystraceOptions implements ISystraceOptions {
		
        private int mTraceBufferSize;
        private int mTraceDuration;
        private String mTraceApp;

        @Override
        public String getTags() {
            return null;
        }

        @Override
        public String getOptions() {
        	
            StringBuilder sb = new StringBuilder(5 * mSupportedTags.size());

            if (mTraceApp != null) {
                sb.append("-a ");   //$NON-NLS-1$
                sb.append(mTraceApp);
                sb.append(' ');
            }

            if (mTraceDuration > 0) {
                sb.append("-t");    //$NON-NLS-1$
                sb.append(mTraceDuration);
                sb.append(' ');
            }

            if (mTraceBufferSize > 0) {
                sb.append("-b ");   //$NON-NLS-1$
                sb.append(mTraceBufferSize);
                sb.append(' ');
            }

            for (String s : sEnabledTags) {
                sb.append(s);
                sb.append(' ');
            }

            return sb.toString().trim();
        }
    }

}
