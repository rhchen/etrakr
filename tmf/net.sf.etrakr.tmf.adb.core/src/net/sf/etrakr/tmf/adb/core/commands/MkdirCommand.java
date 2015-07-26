package net.sf.etrakr.tmf.adb.core.commands;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import net.sf.etrakr.tmf.adb.core.AdbConnection;
import net.sf.etrakr.tmf.adb.core.internal.AdbException;


public class MkdirCommand extends AbstractRemoteCommand<Void> {

	private final IPath fRemotePath;

	public MkdirCommand(AdbConnection connection, IPath path) {
		super(connection);
		fRemotePath = path;
	}

	@Override
	public Void getResult(IProgressMonitor monitor) throws RemoteConnectionException {
		final SubMonitor subMon = SubMonitor.convert(monitor, 20);

		/*
		 * Recursively create parent directories
		 */
		FetchInfoCommand command = new FetchInfoCommand(getConnection(), fRemotePath.removeLastSegments(1));
		IFileInfo info = command.getResult(subMon.newChild(10));
		if (!info.exists()) {
			MkdirCommand mkdirCommand = new MkdirCommand(getConnection(), fRemotePath.removeLastSegments(1));
			mkdirCommand.getResult(subMon.newChild(10));
		}

		/*
		 * Now create directory
		 */
		SftpCallable<Void> c = new SftpCallable<Void>() {
			@Override
			public Void call() throws AdbException {
				getChannel().mkdir(fRemotePath.toString());
				return null;
			}
		};
		try {
			c.getResult(subMon.newChild(10));
		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
		return null;
	}
}
