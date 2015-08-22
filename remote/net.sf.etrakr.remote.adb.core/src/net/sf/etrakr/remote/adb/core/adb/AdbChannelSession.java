package net.sf.etrakr.remote.adb.core.adb;

public class AdbChannelSession extends AdbChannel {

	private static byte[] _session = Util.str2byte("session");
	protected byte[] type = Util.str2byte("foo");

	protected String ttype = "vt100";
	protected int tcol = 80;
	protected int trow = 24;
	protected int twp = 640;
	protected int thp = 480;

	protected boolean pty = false;

	AdbChannelSession() {
		super();
		type = _session;
		io = new AdbIO();
	}

	@Override
	public void run() {

		
	}

	public void setPty(boolean enable) {
		pty = enable;
	}

	public void setPtyType(String ttype) {
		setPtyType(ttype, 80, 24, 640, 480);
	}

	public void setPtyType(String ttype, int col, int row, int wp, int hp) {
		this.ttype = ttype;
		this.tcol = col;
		this.trow = row;
		this.twp = wp;
		this.thp = hp;
	}
	
	public void setPtySize(int col, int row, int wp, int hp){
	    setPtyType(this.ttype, col, row, wp, hp);
	    if(!pty || !isConnected()){
	      return;
	    }
	    
	  }
}
