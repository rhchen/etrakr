package net.sf.etrakr.tmf.adb.core.internal;

public class Adb {

	private java.util.Vector sessionPool = new java.util.Vector();
	
	private static final AdbLogger DEVNULL=new AdbLogger(){
	      public boolean isEnabled(int level){return false;}
	      public void log(int level, String message){}
	    };
	  static AdbLogger logger=DEVNULL;
	public AdbSession getSession(String username, String host, int port) throws AdbException {
		
	    if(host==null){
	      throw new AdbException("host must not be null.");
	    }
	    AdbSession s = new AdbSession(this, username, host, port); 
	    return s;
	  }

	  protected void addSession(AdbSession session){
	    synchronized(sessionPool){
	      sessionPool.addElement(session);
	    }
	  }

	  protected boolean removeSession(AdbSession session){
	    synchronized(sessionPool){
	      return sessionPool.remove(session);
	    }
	  }
	  
	  /**
	   * Sets the logger
	   *
	   * @param logger logger
	   *
	   * @see com.jcraft.jsch.Logger
	   */
	  public static void setLogger(AdbLogger logger){
	    if(logger==null) logger=DEVNULL;
	    Adb.logger=logger;
	  }

	  static AdbLogger getLogger(){
	    return logger;
	  }
}
