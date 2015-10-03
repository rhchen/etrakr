package net.sf.etrakr.remote.adb.core.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;
import net.sf.etrakr.remote.adb.core.AdbConnection;
import net.sf.etrakr.remote.adb.core.AdbPlugin;
import net.sf.etrakr.remote.adb.core.AdbProcessBuilder;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionType;
import org.eclipse.remote.core.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.IRemoteFileService;
import org.eclipse.remote.core.IRemoteProcess;
import org.eclipse.remote.core.IRemoteProcessBuilder;
import org.eclipse.remote.core.IRemoteProcessService;
import org.eclipse.remote.core.IRemoteServicesManager;
import org.junit.Ignore;
import org.junit.Test;

import com.android.ddmlib.IDevice;

public class SystraceTests extends TestCase {
	private static final String USERNAME = "test"; //$NON-NLS-1$
	private static final String PASSWORD = ""; //$NON-NLS-1$
	private static final String HOST = "localhost"; //$NON-NLS-1$

	private IRemoteConnectionType fConnectionType;
	private IRemoteConnection fRemoteConnection;

	IRemoteProcessService processService;
	
	private void startTrace(){
		
		IRemoteProcessBuilder builder = processService.getProcessBuilder("atrace", "sched", "--async_start"); //$NON-NLS-1$
		assertNotNull(builder);
		try {
			
			IRemoteProcess proc = builder.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = stdout.readLine();
			
			System.out.println("SystraceTests.startTrace "+ line);
			
			proc.destroy();
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	private void endTrace(){
		
		IRemoteProcessBuilder builder = processService.getProcessBuilder("atrace", "--async_stop"); //$NON-NLS-1$
		assertNotNull(builder);
		try {
			
			IRemoteProcess proc = builder.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = stdout.readLine();
			
			System.out.println("SystraceTests.endTrace "+ line);
			
			proc.destroy();
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	private void tracePipe(){
		
		assertNotNull(processService);
		String var = processService.getEnv("PATH"); //$NON-NLS-1$
		assertNotNull(var);

		var = processService.getEnv("FOO_VAR_SHOULD_NOT_BE_DEFINED"); //$NON-NLS-1$
		assertNull(var);

		final IRemoteProcessBuilder builder = processService.getProcessBuilder("cat","/sys/kernel/debug/tracing/trace_pipe"); //$NON-NLS-1$
		assertNotNull(builder);
		builder.environment().put("FOO", "BAR"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.environment().put("USER", "FOO"); //$NON-NLS-1$ //$NON-NLS-2$
		IRemoteProcess proc = null;
		try {
			
			proc = builder.start(AdbProcessBuilder.ADB_CMD_NONBLOCKING_MODE);
			
			BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			int count = 5000;
			while ((line = stdout.readLine()) != null) {
				
				count--;
				if(count == 0){
					System.out.println("SystraceTests.testEnv.line "+ line);
					count = 5000;
				}
				
			}
			
		} catch (IOException e) {
			
			/*
			 * The exception is expected Read end dead
			 * We user atrace stop to force close the pipe
			 */
			
//			e.printStackTrace();
//			fail(e.getMessage());
		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
		}finally{
			
			if(proc != null) proc.destroy();
			
		}
	}
	
	@Test
	public void testEnv() throws InterruptedException {
		
		startTrace();
		
		Thread t = new Thread(){

			@Override
			public void run() {
				
				tracePipe();
			
			}//run
			
		};
		
		t.start();
		
		t.join(3000);
		
		t.interrupt();
		
		endTrace();
	}

	@Override
	protected void setUp() throws Exception {
		
		List<IDevice> devices = AdbPlugin.getDefault().getDevices();
		
		int count = 0;
		while(count < 10){
			
			if(devices.size() > 0) break;
			
			count ++;
			
			System.out.println("ConnectionTests.setUp wait count "+ count);
			Thread.sleep(1000);
			
		}
		
		IRemoteServicesManager manager = AdbPlugin.getService(IRemoteServicesManager.class);
		fConnectionType = manager.getConnectionType("net.sf.etrakr.remote.adb"); //$NON-NLS-1$
		assertNotNull(fConnectionType);

		IRemoteConnectionWorkingCopy wc = fConnectionType.newConnection("test_connection"); //$NON-NLS-1$

		String host = System.getenv("TEST_HOST");
		if (host == null) {
			host = HOST;
		}
		wc.setAttribute(AdbConnection.ADDRESS_ATTR, host);

		String username = System.getenv("TEST_USERNAME");
		if (username == null) {
			username = USERNAME;
		}
		wc.setAttribute(AdbConnection.USERNAME_ATTR, username);

		String password = System.getenv("TEST_PASSWORD");
		if (password == null) {
			password = PASSWORD;
		}
		wc.setSecureAttribute(AdbConnection.PASSWORD_ATTR, password);

		fRemoteConnection = wc.save();
		assertNotNull(fRemoteConnection);

		fRemoteConnection.open(new NullProgressMonitor());
		assertTrue(fRemoteConnection.isOpen());
		
		processService = fRemoteConnection.getService(IRemoteProcessService.class);
		assertNotNull(processService);
	}

	@Override
	protected void tearDown() throws Exception {
		fConnectionType.removeConnection(fRemoteConnection);
	}

}
