package net.sf.etrakr.remote.adb.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;

import net.sf.etrakr.remote.adb.core.AdbPlugin;
import net.sf.etrakr.remote.adb.core.adb.Adb;
import net.sf.etrakr.remote.adb.core.adb.AdbException;
import net.sf.etrakr.remote.adb.core.adb.AdbSession;
import net.sf.etrakr.remote.adb.core.adb.IProxy;
import net.sf.etrakr.remote.adb.core.adb.IUserInfo;

public class AdbService implements IAdbService {

	private static AdbService instance;

	public static IAdbService getInstance() {
		if (instance == null)
			instance = new AdbService();
		return instance;
	}

	@Override
	public AdbSession createSession(String host, int port, String username) throws AdbException {

		AdbSession session = getAdb().getSession(username, host, port);
		return session;
	}

	@Override
	public AdbSession createSession(IAdbLocation location, IUserInfo uinfo) throws AdbException {

		AdbSession session = createSession(location.getHost(), location.getPort(), location.getUsername());
		return session;
	}

	public AdbSession createSession(IAdbLocation location) throws AdbException {
		return createSession(location, null);
	}

	@Override
	public void connect(AdbSession session, int timeout, IProgressMonitor monitor) throws AdbException {
		
		session.connect();

	}

	@Override
	public IProxy getProxyForHost(String host, String proxyType) {
		
		/* RH. TBD */
		return null;
	}

	@Override
	public void connect(IProxy proxy, String host, int port, int timeout, IProgressMonitor monitor)
			throws AdbException {
		
		/* RH. TBD */
	}

	@Override
	public IAdbLocation getLocation(String user, String host, int port) {
		
		IAdbLocation location=null;
	    location = new AdbLocation(user, host, port);
	    return location;
	}

	@Override
	public Adb getAdb() {
		Adb adb = AdbPlugin.getDefault().getAdb();
		return adb;
	}

}
