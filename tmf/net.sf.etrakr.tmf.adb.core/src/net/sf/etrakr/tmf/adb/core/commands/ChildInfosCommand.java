package net.sf.etrakr.tmf.adb.core.commands;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import net.sf.etrakr.tmf.adb.core.AdbConnection;
import net.sf.etrakr.tmf.adb.core.internal.AdbChannelSftp.LsEntry;
import net.sf.etrakr.tmf.adb.core.internal.AdbException;



public class ChildInfosCommand extends AbstractRemoteCommand<IFileInfo[]> {

	private final IPath fRemotePath;

	public ChildInfosCommand(AdbConnection connection, IPath path) {
		super(connection);
		fRemotePath = path;
	}

	@Override
	public IFileInfo[] getResult(IProgressMonitor monitor) throws RemoteConnectionException {
		final SubMonitor subMon = SubMonitor.convert(monitor, 20);

		Vector<LsEntry> files = getResult(fRemotePath.toString(), subMon.newChild(10));

		List<IFileInfo> result = new ArrayList<IFileInfo>();

		if (files != null && !subMon.isCanceled()) {
			Enumeration<LsEntry> enumeration = files.elements();
			while (enumeration.hasMoreElements() && !subMon.isCanceled()) {
				LsEntry entry = enumeration.nextElement();
				final String fileName = entry.getFilename();
				if (fileName.equals(".") || fileName.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
					// Ignore parent and current dir entry.
					continue;
				}
				result.add(convertToFileInfo(fileName, fRemotePath, entry.getAttrs(), subMon.newChild(10)));
			}
		}

		return result.toArray(new IFileInfo[result.size()]);
	}

	private Vector<LsEntry> getResult(String path, IProgressMonitor monitor) throws RemoteConnectionException {
		final SubMonitor subMon = SubMonitor.convert(monitor, 10);
		SftpCallable<Vector<LsEntry>> c = new SftpCallable<Vector<LsEntry>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Vector<LsEntry> call() throws AdbException {
				return getChannel().ls(quote(fRemotePath.toString(), true));
			}
		};
		try {
			return c.getResult(subMon.newChild(10));
		} catch (AdbException e) {
			throw new RemoteConnectionException(e.getMessage());
		}
	}
}
