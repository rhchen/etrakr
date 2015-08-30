package net.sf.etrakr.remote.adb.core.adb;

public abstract class Request {

	private AdbSession session = null;
	private AdbChannel channel = null;

	void request(AdbSession session, AdbChannel channel) throws Exception {
		this.session = session;
		this.channel = channel;
	}
	
	protected void executeRequest(String strCmd) throws AdbException{
		
		session.executeRequest(strCmd, channel);
	}
}
