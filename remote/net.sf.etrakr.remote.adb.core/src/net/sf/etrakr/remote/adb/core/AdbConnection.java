package net.sf.etrakr.remote.adb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionChangeListener;
import org.eclipse.remote.core.IRemoteConnectionControlService;
import org.eclipse.remote.core.IRemoteConnectionHostService;
import org.eclipse.remote.core.IRemoteConnectionPropertyService;
import org.eclipse.remote.core.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.IRemotePortForwardingService;
import org.eclipse.remote.core.IRemoteProcessBuilder;
import org.eclipse.remote.core.IRemoteProcessService;
import org.eclipse.remote.core.RemoteConnectionChangeEvent;
import org.eclipse.remote.core.exception.RemoteConnectionException;

import net.sf.etrakr.remote.adb.core.adb.AdbChannelExec;
import net.sf.etrakr.remote.adb.core.adb.AdbChannelSftp;
import net.sf.etrakr.remote.adb.core.adb.AdbChannelShell;
import net.sf.etrakr.remote.adb.core.adb.AdbException;
import net.sf.etrakr.remote.adb.core.adb.AdbSession;
import net.sf.etrakr.remote.adb.core.commands.ExecCommand;
import net.sf.etrakr.remote.adb.core.internal.IAdbService;
import net.sf.etrakr.remote.adb.core.messages.Messages;

public class AdbConnection
		implements IRemoteConnectionControlService, IRemoteConnectionPropertyService, IRemotePortForwardingService,
		IRemoteProcessService, IRemoteConnectionHostService, IRemoteConnectionChangeListener {

	public static final String ADDRESS_ATTR = "ADB_ADDRESS_ATTR"; //$NON-NLS-1$
	public static final String USERNAME_ATTR = "ADB_USERNAME_ATTR"; //$NON-NLS-1$
	public static final String PASSWORD_ATTR = "ADB_PASSWORD_ATTR"; //$NON-NLS-1$
	public static final String PORT_ATTR = "ADB_PORT_ATTR"; //$NON-NLS-1$
	public static final String PROXYCONNECTION_ATTR = "ADB_PROXYCONNECTION_ATTR"; //$NON-NLS-1$
	public static final String PROXYCOMMAND_ATTR = "ADB_PROXYCOMMAND_ATTR"; //$NON-NLS-1$
	public static final String IS_PASSWORD_ATTR = "ADB_IS_PASSWORD_ATTR"; //$NON-NLS-1$
	public static final String PASSPHRASE_ATTR = "ADB_PASSPHRASE_ATTR"; //$NON-NLS-1$
	public static final String TIMEOUT_ATTR = "ADB_TIMEOUT_ATTR"; //$NON-NLS-1$
	public static final String USE_LOGIN_SHELL_ATTR = "ADB_USE_LOGIN_SHELL_ATTR"; //$NON-NLS-1$

	public static final int DEFAULT_TIMEOUT = 0;
	public static final int DEFAULT_PORT = 22;
	
	private final IRemoteConnection fRemoteConnection;
	private final IAdbService fJSchService;
	private AdbChannelSftp fSftpChannel;
	
	private final Map<String, String> fEnv = new HashMap<String, String>();
	private String fWorkingDir;
	
	private final Map<String, String> fProperties = new HashMap<String, String>();
	private final List<AdbSession> fSessions = new ArrayList<AdbSession>();

	public AdbConnection(IRemoteConnection connection) {
		fRemoteConnection = connection;
		fJSchService = AdbPlugin.getDefault().getService();
		connection.addConnectionChangeListener(this);
	}

	public AdbChannelShell getShellChannel() throws RemoteConnectionException {
		try {
			return (AdbChannelShell) fSessions.get(0).openChannel("shell"); //$NON-NLS-1$
		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
	}

	public AdbChannelExec getExecChannel() throws RemoteConnectionException {
		try {
			return (AdbChannelExec) fSessions.get(0).openChannel("exec"); //$NON-NLS-1$
		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
	}

	public AdbChannelSftp getSftpChannel() throws RemoteConnectionException {
		if (fSftpChannel == null || fSftpChannel.isClosed()) {
			AdbSession session = fSessions.get(0);
			if (fSessions.size() > 1) {
				session = fSessions.get(1);
			}
			fSftpChannel = openSftpChannel(session);
			if (fSftpChannel == null) {
				throw new RemoteConnectionException(Messages.JSchConnection_Unable_to_open_sftp_channel);
			}
		}
		return fSftpChannel;
	}

	private AdbChannelSftp openSftpChannel(AdbSession session) throws RemoteConnectionException {
		try {
			AdbChannelSftp channel = (AdbChannelSftp) session.openChannel("sftp"); //$NON-NLS-1$
			channel.connect();
			return channel;
		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
	}

	public boolean hasOpenSession() {
		boolean hasOpenSession = fSessions.size() > 0;
		if (hasOpenSession) {
			for (AdbSession session : fSessions) {
				hasOpenSession &= session.isConnected();
			}
		}
		if (!hasOpenSession) {
			cleanup(); // Cleanup if session is closed
		}
		return hasOpenSession;
	}

	private synchronized void cleanup() {
		if (fSftpChannel != null) {
			if (fSftpChannel.isConnected()) {
				fSftpChannel.disconnect();
			}
			fSftpChannel = null;
		}
		for (AdbSession session : fSessions) {
			if (session.isConnected()) {
				session.disconnect();
			}
		}
		fSessions.clear();
	}

	private AdbSession newSession(IProgressMonitor monitor) throws RemoteConnectionException {
		SubMonitor progress = SubMonitor.convert(monitor, 10);
		try {
			AdbSession session = fJSchService.createSession(getHostname(), getPort(), getUsername());
			fJSchService.connect(session, getTimeout() * 1000, progress.newChild(10)); // connect
																						// without
																						// proxy

			if (!progress.isCanceled()) {
				fSessions.add(session);
				return session;
			}

			return null;

		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
	}

	private void open(IProgressMonitor monitor, boolean setupFully) throws RemoteConnectionException {
		SubMonitor subMon = SubMonitor.convert(monitor, 60);
		if (!hasOpenSession()) {
			newSession(subMon.newChild(10));
			if (subMon.isCanceled()) {
				throw new RemoteConnectionException(Messages.JSchConnection_Connection_was_cancelled);
			}
		}
		
		loadEnv(subMon.newChild(10));
		
		fWorkingDir = getCwd(subMon.newChild(10));
		
		fRemoteConnection.fireConnectionChangeEvent(RemoteConnectionChangeEvent.CONNECTION_OPENED);
	}

	private void loadEnv(IProgressMonitor monitor) throws RemoteConnectionException {
		SubMonitor subMon = SubMonitor.convert(monitor, 10);
		String env = executeCommand("printenv", subMon.newChild(10)); //$NON-NLS-1$
		String[] vars = env.split("\n"); //$NON-NLS-1$
		for (String var : vars) {
			String[] kv = var.split("="); //$NON-NLS-1$
			if (kv.length == 2) {
				fEnv.put(kv[0], kv[1]);
			}
		}
	}
	
	private String getCwd(IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, 10);
		try {
			return executeCommand("pwd", subMon.newChild(10)); //$NON-NLS-1$
		} catch (RemoteConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String executeCommand(String cmd, IProgressMonitor monitor) throws RemoteConnectionException {
		ExecCommand exec = new ExecCommand(this);
		monitor.subTask(NLS.bind(Messages.JSchConnection_Executing_command, cmd));
		return exec.setCommand(cmd).getResult(monitor).trim();
	}
	
	/* IRemoteConnectionControlService */

	@Override
	public IRemoteConnection getRemoteConnection() {
		return fRemoteConnection;
	}

	@Override
	public void open(IProgressMonitor monitor) throws RemoteConnectionException {
		open(monitor, true);
	}

	@Override
	public synchronized void close() {
		cleanup();
		fRemoteConnection.fireConnectionChangeEvent(RemoteConnectionChangeEvent.CONNECTION_CLOSED);
	}

	/* IRemoteConnectionPropertyService */

	@Override
	public boolean isOpen() {
		return hasOpenSession();
	}

	@Override
	public String getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* IRemotePortForwardingService */

	@Override
	public void forwardLocalPort(int localPort, String fwdAddress, int fwdPort) throws RemoteConnectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public int forwardLocalPort(String fwdAddress, int fwdPort, IProgressMonitor monitor)
			throws RemoteConnectionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void forwardRemotePort(int remotePort, String fwdAddress, int fwdPort) throws RemoteConnectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public int forwardRemotePort(String fwdAddress, int fwdPort, IProgressMonitor monitor)
			throws RemoteConnectionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeLocalPortForwarding(int port) throws RemoteConnectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRemotePortForwarding(int port) throws RemoteConnectionException {
		// TODO Auto-generated method stub

	}

	/* IRemoteProcessService */

	@Override
	public Map<String, String> getEnv() {
		return Collections.unmodifiableMap(fEnv);
	}

	@Override
	public String getEnv(String name) {
		return getEnv().get(name);
	}

	@Override
	public IRemoteProcessBuilder getProcessBuilder(List<String> command) {
		return new AdbProcessBuilder(getRemoteConnection(), command);
	}

	@Override
	public IRemoteProcessBuilder getProcessBuilder(String... command) {
		return new AdbProcessBuilder(getRemoteConnection(), command);
	}

	@Override
	public String getWorkingDirectory() {
		if (!isOpen()) {
			return "/"; //$NON-NLS-1$
		}
		if (fWorkingDir == null) {
			return "/"; //$NON-NLS-1$
		}
		return fWorkingDir;
	}

	@Override
	public void setWorkingDirectory(String path) {
		fWorkingDir = path;
	}

	/* IRemoteConnectionHostService */

	@Override
	public String getHostname() {
		return fRemoteConnection.getAttribute(ADDRESS_ATTR);
	}

	@Override
	public int getPort() {
		String portStr = fRemoteConnection.getAttribute(PORT_ATTR);
		return !portStr.isEmpty() ? Integer.parseInt(portStr) : DEFAULT_PORT;
	}

	@Override
	public int getTimeout() {
		String str = fRemoteConnection.getAttribute(TIMEOUT_ATTR);
		return !str.isEmpty() ? Integer.parseInt(str) : DEFAULT_TIMEOUT;
	}

	@Override
	public boolean useLoginShell() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUsername() {
		return fRemoteConnection.getAttribute(USERNAME_ATTR);
	}

	@Override
	public void setHostname(String hostname) {
		if (fRemoteConnection instanceof IRemoteConnectionWorkingCopy) {
			IRemoteConnectionWorkingCopy wc = (IRemoteConnectionWorkingCopy) fRemoteConnection;
			wc.setAttribute(ADDRESS_ATTR, hostname);
		}
	}

	@Override
	public void setPassphrase(String passphrase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPassword(String password) {
		if (fRemoteConnection instanceof IRemoteConnectionWorkingCopy) {
			IRemoteConnectionWorkingCopy wc = (IRemoteConnectionWorkingCopy) fRemoteConnection;
			wc.setSecureAttribute(PASSWORD_ATTR, password);
		}
	}

	@Override
	public void setPort(int port) {
		if (fRemoteConnection instanceof IRemoteConnectionWorkingCopy) {
			IRemoteConnectionWorkingCopy wc = (IRemoteConnectionWorkingCopy) fRemoteConnection;
			wc.setAttribute(PORT_ATTR, Integer.toString(port));
		}
	}

	@Override
	public void setTimeout(int timeout) {
		if (fRemoteConnection instanceof IRemoteConnectionWorkingCopy) {
			IRemoteConnectionWorkingCopy wc = (IRemoteConnectionWorkingCopy) fRemoteConnection;
			wc.setAttribute(TIMEOUT_ATTR, Integer.toString(timeout));
		}
	}

	@Override
	public void setUseLoginShell(boolean useLogingShell) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUsePassword(boolean usePassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUsername(String username) {
		if (fRemoteConnection instanceof IRemoteConnectionWorkingCopy) {
			IRemoteConnectionWorkingCopy wc = (IRemoteConnectionWorkingCopy) fRemoteConnection;
			wc.setAttribute(USERNAME_ATTR, username);
		}
	}

	/* IRemoteConnectionChangeListener */

	@Override
	public void connectionChanged(RemoteConnectionChangeEvent event) {

		if (event.getType() == RemoteConnectionChangeEvent.CONNECTION_REMOVED) {
			synchronized (connectionMap) {
				connectionMap.remove(event.getConnection());
			}
		}

	}

	/* Factories */

	private static final Map<IRemoteConnection, AdbConnection> connectionMap = new HashMap<>();

	public static class Factory implements IRemoteConnection.Service.Factory {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.remote.core.IRemoteConnection.Service.Factory#getService(
		 * org.eclipse.remote.core.IRemoteConnection, java.lang.Class)
		 */
		@Override
		@SuppressWarnings("unchecked")
		public <T extends IRemoteConnection.Service> T getService(IRemoteConnection connection, Class<T> service) {
			// This little trick creates an instance of this class for a
			// connection
			// then for each interface it implements, it returns the same
			// object.
			// This works because the connection caches the service so only one
			// gets created.
			// As a side effect, it makes this class a service too which can be
			// used
			// by the this plug-in
			if (AdbConnection.class.equals(service)) {
				synchronized (connectionMap) {
					AdbConnection adbConnection = connectionMap.get(connection);
					if (adbConnection == null) {
						adbConnection = new AdbConnection(connection);
						connectionMap.put(connection, adbConnection);
					}
					return (T) adbConnection;
				}
			} else if (IRemoteConnectionControlService.class.equals(service)
					|| IRemoteConnectionPropertyService.class.equals(service)
					|| IRemotePortForwardingService.class.equals(service) || IRemoteProcessService.class.equals(service)
					|| IRemoteConnectionHostService.class.equals(service)) {
				return (T) connection.getService(AdbConnection.class);
			} else {
				return null;
			}
		}
	}

}
