package net.sf.etrakr.remote.adb.core.adb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.remote.core.exception.RemoteConnectionException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import net.sf.etrakr.remote.adb.core.AdbPlugin;
import net.sf.etrakr.remote.adb.core.commands.ExecCommand;
import net.sf.etrakr.remote.adb.core.messages.Messages;

public class RequestExec extends Request {

	private byte[] command = new byte[0];

	RequestExec(byte[] command) {
		this.command = command;
	}
	
	public void request(AdbSession session, AdbChannel channel) throws Exception {

		super.request(session, channel);

		String strCmd = new String(command);
		System.out.println("AdbChannelExec.start strCmd : " + strCmd);

		executeRequest(strCmd);
	}
	
//	public void request(AdbSession session, AdbChannel channel) throws Exception {
//
//		super.request(session, channel);
//
//		try {
//
//			String strCmd = new String(command);
//			System.out.println("AdbChannelExec.start strCmd : " + strCmd);
//
//			
//			/* Fix me. RH */
//			CountDownLatch setTagLatch = new CountDownLatch(1);
//			CollectingOutputReceiver receiver = new CollectingOutputReceiver(setTagLatch);
//			// String cmd = "atrace --list_categories";
//			IDevice mDevice = AdbPlugin.getDefault().getAndroidDebugBridge().getDevices()[0];
//
//			mDevice.executeShellCommand(strCmd, receiver, 5000, TimeUnit.MILLISECONDS);
//
//			setTagLatch.await(5, TimeUnit.SECONDS);
//
//			// String shellOutput = result.toString();
//
//			String shellOutput = receiver.getOutput();
//
//			//System.out.println("shellOutput : " + shellOutput);
//
//			boolean shoulddisconnect = true;
//			
//			if(channel.io.out == null){
//				
//				System.out.println("RequestExec.request : "+ strCmd);
//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				ByteArrayOutputStream err = new ByteArrayOutputStream();
//				((AdbChannelExec) channel).setOutputStream(stream);
//				((AdbChannelExec) channel).setErrStream(err);
//				
//				shoulddisconnect = false;
//			}
//			
//			channel.io.out.write(shellOutput.getBytes());
//
//			channel.io.out.flush();
//			
//			channel.setExitStatus(0);
//
//			if(shoulddisconnect) channel.disconnect();
//			
//
//		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException
//				| InterruptedException e) {
//
//			e.printStackTrace();
//
//			throw new AdbException(e.getMessage());
//		}
//	}
}
