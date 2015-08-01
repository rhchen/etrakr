package net.sf.etrakr.remote.adb.core.adb;

public class Adb {

	private java.util.Vector sessionPool = new java.util.Vector();
	
	private static final ILogger DEVNULL = new ILogger() {
		public boolean isEnabled(int level) {
			return false;
		}

		public void log(int level, String message) {
		}
	};
	
	static ILogger logger = DEVNULL;

	public static void setLogger(ILogger logger) {
		if (logger == null)
			logger = DEVNULL;
		Adb.logger = logger;
	}

	static ILogger getLogger() {
		return logger;
	}

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
}
