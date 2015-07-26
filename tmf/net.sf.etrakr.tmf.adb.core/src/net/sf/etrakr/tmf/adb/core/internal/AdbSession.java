package net.sf.etrakr.tmf.adb.core.internal;

import java.io.InputStream;
import java.io.OutputStream;

public class AdbSession implements Runnable {

	String host = "127.0.0.1";
	String org_host = "127.0.0.1";
	int port = 22;
	static final int SSH_MSG_CHANNEL_DATA=                   94;
	InputStream in = null;
	OutputStream out = null;

	private Proxy proxy = null;

	String username = null;
	byte[] password = null;

	private int timeout = 0;

	private volatile boolean isConnected = false;

	static final int buffer_margin = 32 + // maximum padding length
			20 + // maximum mac length
			32; // margin for deflater; deflater may inflate data

	Adb adb;

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

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	void setUserName(String username) {
		this.username = username;
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	Runnable thread;

	@Override
	public void run() {

		thread = this;

	}

	public void connect() throws AdbException {
		connect(timeout);
	}

	public void connect(int connectTimeout) throws AdbException {

		if (isConnected) {
			throw new AdbException("session is already connected");
		}

		isConnected = true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void disconnect() {
		if (!isConnected)
			return;
		// System.err.println(this+": disconnect");
		// Thread.dumpStack();
		if (Adb.getLogger().isEnabled(AdbLogger.INFO)) {
			Adb.getLogger().log(AdbLogger.INFO, "Disconnecting from " + host + " port " + port);
		}

		AdbChannel.disconnect(this);

		isConnected = false;

		thread = null;

		adb.removeSession(this);

		// System.gc();
	}

	public AdbChannel openChannel(String type) throws AdbException {
		if (!isConnected) {
			throw new AdbException("session is down");
		}
		try {
			AdbChannel channel = AdbChannel.getChannel(type);
			addChannel(channel);
			channel.init();
			// if(channel instanceof AdbChannelSession){
			// applyConfigChannel((AdbChannelSession)channel);
			// }
			return channel;
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	void addChannel(AdbChannel channel) {
		channel.setSession(this);
	}
	
	public int setPortForwardingL(int lport, String host, int rport) throws AdbException{
	    return setPortForwardingL("127.0.0.1", lport, host, rport);
	  }
	
	public int setPortForwardingL(String bind_address, int lport, String host, int rport) throws AdbException{
	    return setPortForwardingL(bind_address, lport, host, rport, null);
	  }
	
	public int setPortForwardingL(String bind_address, int lport, String host, int rport, AdbServerSocketFactory ssf) throws AdbException{
	    return setPortForwardingL(bind_address, lport, host, rport, ssf, 0);
	  }
	
	public int setPortForwardingL(String bind_address, int lport, String host, int rport, AdbServerSocketFactory ssf, int connectTimeout) throws AdbException{
	    
		/* TBD */
		return lport;
	  }
	
	public void setPortForwardingR(int rport, String host, int lport) throws AdbException{
	    setPortForwardingR(null, rport, host, lport, (AdbSocketFactory)null);
	  }
	
	public void setPortForwardingR(String bind_address, int rport, String host, int lport) throws AdbException{
	    setPortForwardingR(bind_address, rport, host, lport, (AdbSocketFactory)null);
	  }
	
	public void setPortForwardingR(int rport, String host, int lport, AdbSocketFactory sf) throws AdbException{
	    setPortForwardingR(null, rport, host, lport, sf);
	  }
	
	public void setPortForwardingR(String bind_address, int rport, String host, int lport, AdbSocketFactory sf) throws AdbException{
	    int allocated=_setPortForwardingR(bind_address, rport);
//	    ChannelForwardedTCPIP.addPort(this, bind_address,
//	                                  rport, allocated, host, lport, sf);
	  }
	
	public void setPortForwardingR(int rport, String daemon) throws AdbException{
	    setPortForwardingR(null, rport, daemon, null);
	  }
	
	public void setPortForwardingR(int rport, String daemon, Object[] arg) throws AdbException{
	    setPortForwardingR(null, rport, daemon, arg);
	  }
	
	public void setPortForwardingR(String bind_address, int rport, String daemon, Object[] arg) throws AdbException{
	    int allocated = _setPortForwardingR(bind_address, rport);
//	    ChannelForwardedTCPIP.addPort(this, bind_address,
//	                                  rport, allocated, daemon, arg);
	  }
	
	private int _setPortForwardingR(String bind_address, int rport) throws AdbException{
		/* TBD */
		return rport;
	}
	
	public void delPortForwardingL(int lport) throws AdbException{
	    delPortForwardingL("127.0.0.1", lport);
	  }
	
	public void delPortForwardingL(String bind_address, int lport) throws AdbException{
//	    PortWatcher.delPort(this, bind_address, lport);
	  }
	
	public void delPortForwardingR(int rport) throws AdbException{
	    this.delPortForwardingR(null, rport);
	  }
	
	public void delPortForwardingR(String bind_address, int rport) throws AdbException{
//	    ChannelForwardedTCPIP.delPort(this, bind_address, rport);
	  }
	
	public AdbChannel getStreamForwarder(String host, int port) throws AdbException {
		/* TBD */
//		ChannelDirectTCPIP channel = new ChannelDirectTCPIP();
		AdbChannel channel = new AdbChannel(){

			@Override
			public void run() {
				
				
			}};
	    channel.init();
	    this.addChannel(channel);
//	    channel.setHost(host);
//	    channel.setPort(port);
	    return channel;
	  } 
}
