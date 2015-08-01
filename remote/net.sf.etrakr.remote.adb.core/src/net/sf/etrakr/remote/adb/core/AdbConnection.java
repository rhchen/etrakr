package net.sf.etrakr.remote.adb.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionChangeListener;
import org.eclipse.remote.core.IRemoteConnectionControlService;
import org.eclipse.remote.core.IRemoteConnectionHostService;
import org.eclipse.remote.core.IRemoteConnectionPropertyService;
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
import net.sf.etrakr.remote.adb.core.internal.IAdbService;
import net.sf.etrakr.remote.adb.core.messages.Messages;

public class AdbConnection implements IRemoteConnectionControlService, IRemoteConnectionPropertyService,
IRemotePortForwardingService, IRemoteProcessService, IRemoteConnectionHostService, IRemoteConnectionChangeListener{

	private final IRemoteConnection fRemoteConnection;
	private final IAdbService fJSchService;
	private AdbChannelSftp fSftpChannel;
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
	
	/* IRemoteConnectionControlService */
	
	@Override
	public IRemoteConnection getRemoteConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open(IProgressMonitor monitor) throws RemoteConnectionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	/* IRemoteConnectionPropertyService */
	
	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnv(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRemoteProcessBuilder getProcessBuilder(List<String> command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRemoteProcessBuilder getProcessBuilder(String... command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorkingDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWorkingDirectory(String path) {
		// TODO Auto-generated method stub
		
	}

	/* IRemoteConnectionHostService */
	
	@Override
	public String getHostname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean useLoginShell() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHostname(String hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPassphrase(String passphrase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPassword(String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeout(int timeout) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
		 * @see org.eclipse.remote.core.IRemoteConnection.Service.Factory#getService(org.eclipse.remote.core.IRemoteConnection,
		 * java.lang.Class)
		 */
		@Override
		@SuppressWarnings("unchecked")
		public <T extends IRemoteConnection.Service> T getService(IRemoteConnection connection, Class<T> service) {
			// This little trick creates an instance of this class for a connection
			// then for each interface it implements, it returns the same object.
			// This works because the connection caches the service so only one gets created.
			// As a side effect, it makes this class a service too which can be used
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
					|| IRemoteConnectionPropertyService.class.equals(service) || IRemotePortForwardingService.class.equals(service)
					|| IRemoteProcessService.class.equals(service) || IRemoteConnectionHostService.class.equals(service)) {
				return (T) connection.getService(AdbConnection.class);
			} else {
				return null;
			}
		}
	}
	
}
