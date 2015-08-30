package net.sf.etrakr.remote.adb.core.adb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteStreams;

public abstract class AdbChannel implements Runnable {

	static int index = 0;

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

	AdbChannel() {
		synchronized (pool) {
			id = index++;
			pool.addElement(this);
		}
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

	public void disconnect() {

		try {

			synchronized (this) {
				if (!connected) {
					return;
				}
				connected = false;
			}

			close();
			
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

	void close(){
		
	    if(close)return;
	    close=true;
	    
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

	public InputStream getInputStream() throws IOException {
		int max_input_buffer_size = 32 * 1024;
		PipedInputStream in = new _PipedInputStream(32 * 1024, max_input_buffer_size);
		boolean resizable = 32 * 1024 < max_input_buffer_size;
		io.setOutputStream(new PassiveOutputStream(in, resizable), false);
		
		
//		String str = io.out.toString();
//		
//		InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		
		//System.out.println("AdbChannel.getInputStream "+ str);
		//ByteStreams.copy(in, io.out);
		//System.out.println("AdbChannel.getInputStream");
		//PipedInputStream in =  new  PipedInputStream(); 
		//PipedOutputStream out =  new  PipedOutputStream();  
		//in.connect(out);
		//out.connect(in);
		//out.write(str.getBytes());
//		io.setOutputStream(out);
		
		//this.disconnect();
		
		return in;
	}

	public InputStream getExtInputStream() throws IOException {
		int max_input_buffer_size = 32 * 1024;
		PipedInputStream in = new _PipedInputStream(32 * 1024, max_input_buffer_size);
		boolean resizable = 32 * 1024 < max_input_buffer_size;
		io.setExtOutputStream(new PassiveOutputStream(in, resizable), false);
		return in;
	}

	public OutputStream getOutputStream() throws IOException {

		/* RH. Fix me */
		PipedOutputStream channelOutputStream = new PipedOutputStream();
		PipedInputStream channelInputStream = new PipedInputStream(channelOutputStream);
		io.setInputStream(channelInputStream);
		return channelOutputStream;
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

	class PassiveOutputStream extends PipedOutputStream {
		private _PipedInputStream _sink = null;

		PassiveOutputStream(PipedInputStream in, boolean resizable_buffer) throws IOException {
			super(in);
			if (resizable_buffer && (in instanceof _PipedInputStream)) {
				this._sink = (_PipedInputStream) in;
			}
		}

		public void write(int b) throws IOException {
			if (_sink != null) {
				_sink.checkSpace(1);
			}
			super.write(b);
		}

		public void write(byte[] b, int off, int len) throws IOException {
			if (_sink != null) {
				_sink.checkSpace(len);
			}
			super.write(b, off, len);
		}
	}

	class _PipedInputStream extends PipedInputStream {
		private int BUFFER_SIZE = 1024;
		private int max_buffer_size = BUFFER_SIZE;

		_PipedInputStream() throws IOException {
			super();
		}

		_PipedInputStream(int size) throws IOException {
			super();
			buffer = new byte[size];
			BUFFER_SIZE = size;
			max_buffer_size = size;
		}

		_PipedInputStream(int size, int max_buffer_size) throws IOException {
			this(size);
			this.max_buffer_size = max_buffer_size;
		}

		_PipedInputStream(PipedOutputStream out) throws IOException {
			super(out);
		}

		_PipedInputStream(PipedOutputStream out, int size) throws IOException {
			super(out);
			buffer = new byte[size];
			BUFFER_SIZE = size;
		}

		/*
		 * TODO: We should have our own Piped[I/O]Stream implementation. Before
		 * accepting data, JDK's PipedInputStream will check the existence of
		 * reader thread, and if it is not alive, the stream will be closed.
		 * That behavior may cause the problem if multiple threads make access
		 * to it.
		 */
		public synchronized void updateReadSide() throws IOException {
			if (available() != 0) { // not empty
				return;
			}
			in = 0;
			out = 0;
			buffer[in++] = 0;
			read();
		}

		private int freeSpace() {
			int size = 0;
			if (out < in) {
				size = buffer.length - in;
			} else if (in < out) {
				if (in == -1)
					size = buffer.length;
				else
					size = out - in;
			}
			return size;
		}

		synchronized void checkSpace(int len) throws IOException {
			int size = freeSpace();
			if (size < len) {
				int datasize = buffer.length - size;
				int foo = buffer.length;
				while ((foo - datasize) < len) {
					foo *= 2;
				}

				if (foo > max_buffer_size) {
					foo = max_buffer_size;
				}
				if ((foo - datasize) < len)
					return;

				byte[] tmp = new byte[foo];
				if (out < in) {
					System.arraycopy(buffer, 0, tmp, 0, buffer.length);
				} else if (in < out) {
					if (in == -1) {
					} else {
						System.arraycopy(buffer, 0, tmp, 0, in);
						System.arraycopy(buffer, out, tmp, tmp.length - (buffer.length - out), (buffer.length - out));
						out = tmp.length - (buffer.length - out);
					}
				} else if (in == out) {
					System.arraycopy(buffer, 0, tmp, 0, buffer.length);
					in = buffer.length;
				}
				buffer = tmp;
			} else if (buffer.length == size && size > BUFFER_SIZE) {
				int i = size / 2;
				if (i < BUFFER_SIZE)
					i = BUFFER_SIZE;
				byte[] tmp = new byte[i];
				buffer = tmp;
			}
		}
	}
	
	protected void sendChannelOpen() throws Exception {
	    AdbSession _session = getSession();
	    if(!_session.isConnected()){
	      throw new AdbException("session is down");
	    }

	    connected = true;
	  }
}
