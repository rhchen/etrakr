package net.sf.etrakr.remote.adb.core.commands;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.remote.core.exception.RemoteConnectionException;

import net.sf.etrakr.remote.adb.core.AdbConnection;

public class DeleteCommand extends AbstractRemoteCommand<Void> {

	private final IPath fRemotePath;

	public DeleteCommand(AdbConnection connection, IPath path) {
		super(connection);
		fRemotePath = path;
	}

	@Override
	public Void getResult(IProgressMonitor monitor) throws RemoteConnectionException {
		final SubMonitor subMon = SubMonitor.convert(monitor, 10);

		ExecCommand command = new ExecCommand(getConnection());
		command.setCommand("/bin/rm -rf " + quote(fRemotePath.toString(), true)); //$NON-NLS-1$
		String result = command.getResult(subMon.newChild(10));
		if (!result.equals("")) { //$NON-NLS-1$
			throw new RemoteConnectionException(result);
		}
		return null;
	}
}
