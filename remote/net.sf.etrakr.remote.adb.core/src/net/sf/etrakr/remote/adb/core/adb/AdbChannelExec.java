package net.sf.etrakr.remote.adb.core.adb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import net.sf.etrakr.remote.adb.core.AdbPlugin;

public class AdbChannelExec extends AdbChannelSession {

	byte[] command = new byte[0];

	private List<String> result = new ArrayList<String>();

	public void start() throws AdbException {

		AdbSession _session = getSession();

		try {
			
			Request request = new RequestExec(command);
			
			request.request(_session, this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		/* Fix me. suppose not get in here */
		if (io.in != null) {
			thread = new Thread(this);
			thread.setName("Exec thread " + _session.getHost());
			if (_session.daemon_thread) {
				thread.setDaemon(_session.daemon_thread);
			}
			thread.start();
		}

		

	}

	public void setCommand(String command) {
		this.command = Util.str2byte(command);
	}

	public void setCommand(byte[] command) {
		this.command = command;
	}

	void init() throws AdbException {
		io.setInputStream(getSession().in);
		io.setOutputStream(getSession().out);
	}

	public void setErrStream(java.io.OutputStream out) {
		setExtOutputStream(out);
	}

	public void setErrStream(java.io.OutputStream out, boolean dontclose) {
		setExtOutputStream(out, dontclose);
	}

	public java.io.InputStream getErrStream() throws java.io.IOException {
		return getExtInputStream();
	}
}
