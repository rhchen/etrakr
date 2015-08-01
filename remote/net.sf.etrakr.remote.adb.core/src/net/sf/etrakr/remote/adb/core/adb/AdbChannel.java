package net.sf.etrakr.remote.adb.core.adb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AdbChannel implements Runnable {

	private static java.util.Vector pool = new java.util.Vector();

	int id;
	private AdbSession session;
	AdbIO io = null;
	Thread thread = null;
	volatile int connectTimeout = 0;
	volatile boolean connected = false;
	volatile boolean close = false;
	volatile int exitstatus = -1;

	void init() throws AdbException {
	}

	public void start() throws AdbException {
	}

	static AdbChannel getChannel(String type) {
		if (type.equals("session")) {
			return new AdbChannelSession();
		}
		if (type.equals("shell")) {
			return new AdbChannelShell();
		}
		if (type.equals("exec")) {
			return new AdbChannelExec();
		}
		if (type.equals("sftp")) {
			return new AdbChannelSftp();
		}
		return null;
	}

	static AdbChannel getChannel(int id, AdbSession session) {
		synchronized (pool) {
			for (int i = 0; i < pool.size(); i++) {
				AdbChannel c = (AdbChannel) (pool.elementAt(i));
				if (c.id == id && c.session == session)
					return c;
			}
		}
		return null;
	}

	static void del(AdbChannel c) {
		synchronized (pool) {
			pool.removeElement(c);
		}
	}

	public boolean isConnected() {
		AdbSession _session = this.session;
		if (_session != null) {
			return _session.isConnected() && connected;
		}
		return false;
	}

	void setSession(AdbSession session) {
		this.session = session;
	}

	public AdbSession getSession() throws AdbException {
		AdbSession _session = session;
		if (_session == null) {
			throw new AdbException("session is not available");
		}
		return _session;
	}

	static void disconnect(AdbSession session) {
		AdbChannel[] channels = null;
		int count = 0;
		synchronized (pool) {
			channels = new AdbChannel[pool.size()];
			for (int i = 0; i < pool.size(); i++) {
				try {
					AdbChannel c = ((AdbChannel) (pool.elementAt(i)));
					if (c.session == session) {
						channels[count++] = c;
					}
				} catch (Exception e) {
				}
			}
		}
		for (int i = 0; i < count; i++) {
			channels[i].disconnect();
		}
	}

	public void connect() throws AdbException {
		connect(0);
	}

	public void connect(int connectTimeout) throws AdbException {
		this.connectTimeout = connectTimeout;
		try {
			start();
		} catch (Exception e) {
			connected = false;
			disconnect();
			if (e instanceof AdbException)
				throw (AdbException) e;
			throw new AdbException(e.toString(), e);
		}
	}

	public void disconnect() {

		try {

			synchronized (this) {
				if (!connected) {
					return;
				}
				connected = false;
			}

			thread = null;

			try {
				if (io != null) {
					io.close();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}

		} finally {
			AdbChannel.del(this);
		}
	}

	public boolean isClosed() {
		return close;
	}

	public void setOutputStream(OutputStream out) {
		io.setOutputStream(out, false);
	}

	public void setOutputStream(OutputStream out, boolean dontclose) {
		io.setOutputStream(out, dontclose);
	}

	public void setExtOutputStream(OutputStream out) {
		io.setExtOutputStream(out, false);
	}

	public void setExtOutputStream(OutputStream out, boolean dontclose) {
		io.setExtOutputStream(out, dontclose);
	}

	public InputStream getExtInputStream() throws IOException {

		/* RH. TBD */
		return null;
	}

	public InputStream getInputStream() throws IOException {

		/* RH. TBD */
		return null;
	}

	public OutputStream getOutputStream() throws IOException {

		/* RH. TBD */
		return null;
	}

	public void sendSignal(String signal) throws Exception {
		/* RH. TBD */
	}

	void setExitStatus(int status) {
		exitstatus = status;
	}

	public int getExitStatus() {
		return exitstatus;
	}

	public void setXForwarding(boolean foo) {

	}
}
