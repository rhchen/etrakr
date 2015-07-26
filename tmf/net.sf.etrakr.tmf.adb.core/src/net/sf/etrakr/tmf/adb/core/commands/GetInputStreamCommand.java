package net.sf.etrakr.tmf.adb.core.commands;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import net.sf.etrakr.tmf.adb.core.AdbConnection;
import net.sf.etrakr.tmf.adb.core.internal.AdbException;
import net.sf.etrakr.tmf.adb.core.messages.Messages;


public class GetInputStreamCommand extends AbstractRemoteCommand<InputStream> {
	private final IPath fRemotePath;

	public GetInputStreamCommand(AdbConnection connection, IPath path) {
		super(connection);
		fRemotePath = path;
	}

	@Override
	public InputStream getResult(IProgressMonitor monitor) throws RemoteConnectionException {
		final SubMonitor subMon = SubMonitor.convert(monitor, 10);
		SftpCallable<InputStream> c = new SftpCallable<InputStream>() {
			@Override
			public InputStream call() throws AdbException, IOException {
				try {
					return getConnection().getSftpChannel().get(fRemotePath.toString(),
							new CommandProgressMonitor(NLS.bind(Messages.GetInputStreamCommand_Receiving, fRemotePath.toString()), getProgressMonitor()));
				} catch (RemoteConnectionException e) {
					throw new IOException(e.getMessage());
				}
			}
		};
		try {
			return c.getResult(subMon.newChild(10));
		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
	}
}
