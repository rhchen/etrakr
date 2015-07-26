package net.sf.etrakr.tmf.adb.core.commands;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import net.sf.etrakr.tmf.adb.core.JSchConnection;
import net.sf.etrakr.tmf.adb.core.internal.AdbChannelSftp;
import net.sf.etrakr.tmf.adb.core.internal.AdbException;
import net.sf.etrakr.tmf.adb.core.internal.AdbSftpATTRS;


public class FetchInfoCommand extends AbstractRemoteCommand<IFileInfo> {

	private final IPath fRemotePath;

	public static final int SSH_FX_NO_SUCH_FILE=                  2;
	
	public FetchInfoCommand(JSchConnection connection, IPath path) {
		super(connection);
		fRemotePath = path;
	}

	@Override
	public IFileInfo getResult(IProgressMonitor monitor) throws RemoteConnectionException {
		SubMonitor subMon = SubMonitor.convert(monitor, 20);
		SftpCallable<AdbSftpATTRS> c = new SftpCallable<AdbSftpATTRS>() {
			@Override
			public AdbSftpATTRS call() throws AdbException, AdbException {
				return getChannel().lstat(fRemotePath.toString());
			}
		};
		AdbSftpATTRS attrs;
		try {
			attrs = c.getResult(subMon.newChild(10));
			return convertToFileInfo(fRemotePath, attrs, subMon.newChild(10));
		} catch (AdbException e) {
			if (e.id == AdbChannelSftp.SSH_FX_NO_SUCH_FILE) {
				FileInfo info = new FileInfo(fRemotePath.lastSegment());
				info.setExists(false);
				return info;
			}
			throw new RemoteConnectionException(e.getMessage());
		}
	}
}
