package net.sf.etrakr.remote.adb.core.adb;

import java.io.InputStream;
import java.io.OutputStream;

public class AdbSession implements Runnable {

	protected Runnable thread;
	private volatile boolean isConnected = false;
	protected boolean daemon_thread = false;
	InputStream in = null;
	OutputStream out = null;
	static final int buffer_margin = 32 + // maximum padding length
			20 + // maximum mac length
			32; // margin for deflater; deflater may inflate data
	String host = "127.0.0.1";
	String org_host = "127.0.0.1";
	int port = 22;

	String username = null;
	private Object lock = new Object();
	private Thread connectThread = null;
	private int timeout = 0;
	Adb adb;
	private AdbIO io;

	AdbSession(Adb adb, String username, String host, int port) throws AdbException {
		super();
		this.adb = adb;

		this.username = username;
		this.org_host = this.host = host;
		this.port = port;

		if (this.username == null) {
			try {
				this.username = (String) (System.getProperties().get("user.name"));
			} catch (SecurityException e) {
				// ignore e
			}
		}

		if (this.username == null) {
			throw new AdbException("username is not given.");
		}
	}

	/* Runnable */
	@Override
	public void run() {

		this.thread = this;

	}

	public boolean isConnected() {
		return isConnected;
	}

	public String getHost() {
		return host;
	}

	public AdbChannel openChannel(String type) throws AdbException {
		if (!isConnected) {
			throw new AdbException("session is down");
		}
		try {
			AdbChannel channel = AdbChannel.getChannel(type);
			addChannel(channel);
			channel.init();
			return channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	void addChannel(AdbChannel channel) {
		channel.setSession(this);
	}

	public void disconnect() {

		if (!isConnected)
			return;

		if (Adb.getLogger().isEnabled(ILogger.INFO)) {
			Adb.getLogger().log(ILogger.INFO, "Disconnecting from " + host + " port " + port);
		}

		AdbChannel.disconnect(this);

		isConnected = false;

		synchronized (lock) {
			if (connectThread != null) {
				Thread.yield();
				connectThread.interrupt();
				connectThread = null;
			}
		}
		thread = null;
		try {
			if (io != null) {
				if (io.in != null)
					io.in.close();
				if (io.out != null)
					io.out.close();
				if (io.out_ext != null)
					io.out_ext.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		io = null;

		adb.removeSession(this);

	}

	public void connect() throws AdbException {
		connect(timeout);
	}

	public void connect(int connectTimeout) throws AdbException {
		if (isConnected) {
			throw new AdbException("session is already connected");
		}

		io = new AdbIO();

		if (Adb.getLogger().isEnabled(ILogger.INFO)) {
			Adb.getLogger().log(ILogger.INFO, "Connecting to " + host + " port " + port);
		}

		isConnected = true;

		if (Adb.getLogger().isEnabled(ILogger.INFO)) {
			Adb.getLogger().log(ILogger.INFO, "Connection established");
		}

		adb.addSession(this);

		synchronized (lock) {
			if (isConnected) {
				connectThread = new Thread(this);
				connectThread.setName("Connect thread " + host + " session");
				if (daemon_thread) {
					connectThread.setDaemon(daemon_thread);
				}
				connectThread.start();

			} else {
				// The session has been already down and
				// we don't have to start new thread.
			}
		}
	}
}
