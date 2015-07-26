package net.sf.etrakr.tmf.adb.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Hashtable;

public abstract class AdbChannel implements Runnable {

	protected Hashtable env = null;

	protected boolean pty = false;

	volatile int rmpsize=0;
	volatile int recipient=-1;
	protected String ttype = "vt100";
	protected int tcol = 80;
	protected int trow = 24;
	protected int twp = 640;
	protected int thp = 480;
	protected byte[] terminal_mode = null;

	private static java.util.Vector pool = new java.util.Vector();

	private AdbSession session;

	int id;

	AdbIO io = null;
	Thread thread = null;

	volatile boolean eof_local = false;
	volatile boolean eof_remote = false;

	volatile boolean close = false;
	volatile boolean connected = false;

	volatile int connectTimeout = 0;
	volatile int exitstatus = -1;

	static AdbChannel getChannel(String type) {

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

	public AdbSession getSession() throws AdbException {
		AdbSession _session = session;
		if (_session == null) {
			throw new AdbException("session is not available");
		}
		return _session;
	}

	protected void sendChannelOpen() throws Exception {
		AdbSession _session = getSession();
		if (!_session.isConnected()) {
			throw new AdbException("session is down");
		}

		// Packet packet = genChannelOpenPacket();
		// _session.write(packet);

		// int retry=2000;
		// long start=System.currentTimeMillis();
		// long timeout=connectTimeout;
		// if(timeout!=0L) retry = 1;
		// synchronized(this){
		// while(this.getRecipient()==-1 &&
		// _session.isConnected() &&
		// retry>0){
		// if(timeout>0L){
		// if((System.currentTimeMillis()-start)>timeout){
		// retry=0;
		// continue;
		// }
		// }
		// try{
		// long t = timeout==0L ? 10L : timeout;
		// this.notifyme=1;
		// wait(t);
		// }
		// catch(java.lang.InterruptedException e){
		// }
		// finally{
		// this.notifyme=0;
		// }
		// retry--;
		// }
		// }
		if (!_session.isConnected()) {
			throw new AdbException("session is down");
		}
		// if(this.getRecipient()==-1){ // timeout
		// throw new AdbException("channel is not opened.");
		// }
		// if(this.open_confirmation==false){ // SSH_MSG_CHANNEL_OPEN_FAILURE
		// throw new AdbException("channel is not opened.");
		// }
		connected = true;
	}

	public void connect() throws AdbException {
		connect(0);
	}

	public void connect(int connectTimeout) throws AdbException {
		this.connectTimeout = connectTimeout;
		try {
			sendChannelOpen();
			start();
		} catch (Exception e) {
			connected = false;
			disconnect();
			if (e instanceof AdbException)
				throw (AdbException) e;
			throw new AdbException(e.toString(), e);
		}
	}

	static void del(AdbChannel c) {
		synchronized (pool) {
			pool.removeElement(c);
		}
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

	void close() {
		if (close)
			return;
		close = true;
		eof_local = eof_remote = true;

		// int i = getRecipient();
		// if(i == -1) return;
		//
		// try{
		// Buffer buf=new Buffer(100);
		// Packet packet=new Packet(buf);
		// packet.reset();
		// buf.putByte((byte)Session.SSH_MSG_CHANNEL_CLOSE);
		// buf.putInt(i);
		// synchronized(this){
		// getSession().write(packet);
		// }
		// }
		// catch(Exception e){
		// //e.printStackTrace();
		// }
	}

	public boolean isClosed() {
		return close;
	}

	public void disconnect() {
		// System.err.println(this+":disconnect "+io+" "+connected);
		// Thread.dumpStack();

		try {

			synchronized (this) {
				if (!connected) {
					return;
				}
				connected = false;
			}

			close();

			eof_remote = eof_local = true;

			thread = null;

			try {
				if (io != null) {
					io.close();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			// io=null;
		} finally {
			AdbChannel.del(this);
		}
	}

	public boolean isConnected() {
		AdbSession _session = this.session;
		if (_session != null) {
			return _session.isConnected() && connected;
		}
		return false;
	}

	public void start() throws AdbException {
	}

	void setExitStatus(int status) {
		exitstatus = status;
	}

	public int getExitStatus() {
		return exitstatus;
	}

	void setSession(AdbSession session) {
		this.session = session;
	}

	void init() throws AdbException {
	}

	public void setXForwarding(boolean foo) {
	}

	/**
	 * Set the environment variable. If <code>name</code> and <code>value</code>
	 * are needed to be passed to the remote in your favorite encoding, use
	 * {@link #setEnv(byte[], byte[])}. Refer to RFC4254 6.4 Environment
	 * Variable Passing.
	 *
	 * @param name
	 *            A name for environment variable.
	 * @param value
	 *            A value for environment variable.
	 */
	public void setEnv(String name, String value) {
		setEnv(AdbUtil.str2byte(name), AdbUtil.str2byte(value));
	}

	/**
	 * Set the environment variable. Refer to RFC4254 6.4 Environment Variable
	 * Passing.
	 *
	 * @param name
	 *            A name of environment variable.
	 * @param value
	 *            A value of environment variable.
	 * @see #setEnv(String, String)
	 */
	public void setEnv(byte[] name, byte[] value) {
		synchronized (this) {
			getEnv().put(name, value);
		}
	}

	private Hashtable getEnv() {
		if (env == null)
			env = new Hashtable();
		return env;
	}

	/**
	 * Allocate a Pseudo-Terminal. Refer to RFC4254 6.2. Requesting a
	 * Pseudo-Terminal.
	 *
	 * @param enable
	 */
	public void setPty(boolean enable) {
		pty = enable;
	}

	/**
	 * Set the terminal mode.
	 * 
	 * @param terminal_mode
	 */
	public void setTerminalMode(byte[] terminal_mode) {
		this.terminal_mode = terminal_mode;
	}

	/**
	 * Change the window dimension interactively. Refer to RFC4254 6.7. Window
	 * Dimension Change Message.
	 *
	 * @param col
	 *            terminal width, columns
	 * @param row
	 *            terminal height, rows
	 * @param wp
	 *            terminal width, pixels
	 * @param hp
	 *            terminal height, pixels
	 */
	public void setPtySize(int col, int row, int wp, int hp) {
		setPtyType(this.ttype, col, row, wp, hp);
		if (!pty || !isConnected()) {
			return;
		}
		// try{
		// RequestWindowChange request=new RequestWindowChange();
		// request.setSize(col, row, wp, hp);
		// request.request(getSession(), this);
		// }
		// catch(Exception e){
		// //System.err.println("ChannelSessio.setPtySize: "+e);
		// }
	}

	/**
	 * Set the terminal type. This method is not effective after
	 * Channel#connect().
	 *
	 * @param ttype
	 *            terminal type(for example, "vt100")
	 * @see #setPtyType(String, int, int, int, int)
	 */
	public void setPtyType(String ttype) {
		setPtyType(ttype, 80, 24, 640, 480);
	}

	/**
	 * Set the terminal type. This method is not effective after
	 * Channel#connect().
	 *
	 * @param ttype
	 *            terminal type(for example, "vt100")
	 * @param col
	 *            terminal width, columns
	 * @param row
	 *            terminal height, rows
	 * @param wp
	 *            terminal width, pixels
	 * @param hp
	 *            terminal height, pixels
	 */
	public void setPtyType(String ttype, int col, int row, int wp, int hp) {
		this.ttype = ttype;
		this.tcol = col;
		this.trow = row;
		this.twp = wp;
		this.thp = hp;
	}

	public void sendSignal(String signal) throws Exception {
		/* TBD */
		// RequestSignal request=new RequestSignal();
		// request.setSignal(signal);
		// request.request(getSession(), this);
	}

	public void setInputStream(InputStream in) {
		io.setInputStream(in, false);
	}

	public void setInputStream(InputStream in, boolean dontclose) {
		io.setInputStream(in, dontclose);
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

	public InputStream getInputStream() throws IOException {
		/* TBD */
		int max_input_buffer_size = 32 * 1024;

		PipedInputStream in = new PipedInputStream(32 * 1024);
		boolean resizable = 32 * 1024 < max_input_buffer_size;
		io.setOutputStream(new PipedOutputStream(in), false);
		return in;
	}

	public InputStream getExtInputStream() throws IOException {
		/* TBD */
		int max_input_buffer_size = 32 * 1024;

		PipedInputStream in = new PipedInputStream(32 * 1024);
		boolean resizable = 32 * 1024 < max_input_buffer_size;
		io.setExtOutputStream(new PipedOutputStream(in), false);
		return in;
	}
	
	public OutputStream getOutputStream() throws IOException {

	    final AdbChannel channel=this;
	    OutputStream out=new OutputStream(){

			@Override
			public void write(int b) throws IOException {
				/* TBD */
				
			}};
	    return out;
	  }
}
