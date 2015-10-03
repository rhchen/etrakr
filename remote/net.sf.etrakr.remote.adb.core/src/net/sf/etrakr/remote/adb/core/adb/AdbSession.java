package net.sf.etrakr.remote.adb.core.adb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.google.common.primitives.Bytes;

import net.sf.etrakr.remote.adb.core.AdbPlugin;
import net.sf.etrakr.remote.adb.core.adb.AdbChannel.PassiveOutputStream;
import net.sf.etrakr.remote.adb.core.adb.AdbChannel._PipedInputStream;

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

	/* Fix me. RH */
	private byte[] shellOutput;
	private AdbChannel channel;
	
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

	public void executeRequest(final String strCmd, final AdbChannel channel) throws AdbException{

		try {
			
			this.channel = channel;
			
			/* Fix me. RH */
			CountDownLatch setTagLatch = new CountDownLatch(1);
			//CollectingOutputReceiver receiver = new CollectingOutputReceiver(setTagLatch);

			// String cmd = "atrace --list_categories";
			final IDevice mDevice = AdbPlugin.getDefault().getAndroidDebugBridge().getDevices()[0];
			
			if(this.channel.isBlock_mode()){
				
				final Receiver receiver = new Receiver(setTagLatch);
				
				mDevice.executeShellCommand(strCmd, receiver, 1, TimeUnit.MINUTES);
				
				setTagLatch.await(100, TimeUnit.MILLISECONDS);
				
				shellOutput = receiver.getAtraceOutput();
				
			}else{
				
				final Receiver receiver = new Receiver(setTagLatch){

					@Override
					public void addOutput(byte[] data, int offset, int length) {
						
						if(channel.io.out != null){
		                	
		                	try {
		    					channel.io.out.write(data);
		    				} catch (IOException e) {
		    					e.printStackTrace();
		    				}
		                }
						
					}
					
				};
				
				Thread t = new Thread(){

					@Override
					public void run() {
						try {
							mDevice.executeShellCommand(strCmd, receiver, 1, TimeUnit.MINUTES);
						} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
							e.printStackTrace();
						}
					}

				};
				
				t.start();
				
			}//if
			
			
//			mDevice.executeShellCommand(strCmd, receiver, 1, TimeUnit.MINUTES);
//		
//			setTagLatch.await(100, TimeUnit.MILLISECONDS);
//
//			shellOutput = receiver.getAtraceOutput();

		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException | InterruptedException e) {
			e.printStackTrace();
			throw new AdbException(e.getMessage());
		}

	}
	
	private class Receiver implements IShellOutputReceiver {
		
		private final Object mLock = new Object();
		private boolean mTraceComplete;
	    private byte[] mBuffer = new byte[1024];
	    private int mDataLength = 0;
	    private volatile boolean mCancel;
	    
	    private CountDownLatch mCompletionLatch;
	    
	    public Receiver(CountDownLatch commandCompleteLatch) {
	        mCompletionLatch = commandCompleteLatch;
	    }
	    
	    public void cancel() {
	        mCancel = true;
	    }
	    
		public byte[] getAtraceOutput() {
	        synchronized (mLock) {
	            return mTraceComplete ? mBuffer : null;
	        }
	    }
		
        @Override
        public void addOutput(byte[] data, int offset, int length) {
            synchronized (mLock) {
                if (mDataLength + length > mBuffer.length) {
                    mBuffer = Bytes.ensureCapacity(mBuffer, mDataLength + length + 1, 1024);
                }

                for (int i = 0; i < length; i++) {
                    mBuffer[mDataLength + i] = data[offset + i];
                }
                mDataLength += length;
            }
        }

        @Override
        public void flush() {
            synchronized (mLock) {
                // trim mBuffer to its final size
                byte[] copy = new byte[mDataLength];
                for (int i = 0; i < mDataLength; i++) {
                    copy[i] = mBuffer[i];
                }
                mBuffer = copy;

                mTraceComplete = true;
            }
            
            if (mCompletionLatch != null) {
                mCompletionLatch.countDown();
            }
        }

        @Override
        public boolean isCancelled() {
            return mCancel;
        }
    }

	/* Runnable */
	@Override
	public void run() {

		this.thread = this;

		/* Fix me. RH 
		 * 
		 * Need to review
		 * here we wait the io.out to be ready, since in exe proc mode
		 * must wait inputstream to be created before return the command output
		 * */
		while (isConnected && thread != null) {

			if(channel != null && channel.io.out != null && shellOutput != null){
				
				try {
					
					channel.io.out.write(shellOutput);
					
					channel.io.out.flush();
					
					channel.setExitStatus(0);

					channel.disconnect();
					
					shellOutput = null;
					
					System.out.println("AdbSession.run isConnected : "+ isConnected + " thread : "+ thread);
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}else{
				
				
				
			}

		} // while

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
