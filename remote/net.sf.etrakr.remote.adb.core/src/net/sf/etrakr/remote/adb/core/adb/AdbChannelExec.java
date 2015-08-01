package net.sf.etrakr.remote.adb.core.adb;

public class AdbChannelExec extends AdbChannelSession{

	byte[] command=new byte[0];

	  public void start() throws AdbException{
		  
	    AdbSession _session=getSession();
	    

	    if(io.in!=null){
	      thread=new Thread(this);
	      thread.setName("Exec thread "+_session.getHost());
	      if(_session.daemon_thread){
	        thread.setDaemon(_session.daemon_thread);
	      }
	      thread.start();
	    }
	  }

	  public void setCommand(String command){ 
	    this.command=Util.str2byte(command);
	  }
	  public void setCommand(byte[] command){ 
	    this.command=command;
	  }

	  void init() throws AdbException {
	    io.setInputStream(getSession().in);
	    io.setOutputStream(getSession().out);
	  }

	  public void setErrStream(java.io.OutputStream out){
	    setExtOutputStream(out);
	  }
	  
	  public void setErrStream(java.io.OutputStream out, boolean dontclose){
	    setExtOutputStream(out, dontclose);
	  }
	  
	  public java.io.InputStream getErrStream() throws java.io.IOException {
	    return getExtInputStream();
	  }
}
