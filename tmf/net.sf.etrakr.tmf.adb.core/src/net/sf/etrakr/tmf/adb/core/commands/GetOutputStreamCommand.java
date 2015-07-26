package net.sf.etrakr.tmf.adb.core.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import net.sf.etrakr.tmf.adb.core.AdbConnection;
import net.sf.etrakr.tmf.adb.core.internal.AdbChannelSftp;
import net.sf.etrakr.tmf.adb.core.internal.AdbException;
import net.sf.etrakr.tmf.adb.core.messages.Messages;


public class GetOutputStreamCommand extends AbstractRemoteCommand<OutputStream> {
	private final IPath fRemotePath;
	private final int fOptions;
	private boolean fIsClosed;

	public GetOutputStreamCommand(AdbConnection connection, int options, IPath path) {
		super(connection);
		fRemotePath = path;
		fOptions = options;
		fIsClosed = false;
	}

	@Override
	public OutputStream getResult(IProgressMonitor monitor) throws RemoteConnectionException {
		final SubMonitor subMon = SubMonitor.convert(monitor, 10);
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				if (!fIsClosed) {
					super.close();
					final InputStream input = new ByteArrayInputStream(this.toByteArray());
					try {
						SftpCallable<Integer> c = new SftpCallable<Integer>() {
							@Override
							public Integer call() throws AdbException, IOException {
								try {
									int mode = AdbChannelSftp.OVERWRITE;
									if ((fOptions & EFS.APPEND) != 0) {
										mode = AdbChannelSftp.APPEND;
									}
									getChannel().put(
											input,
											fRemotePath.toString(),
											new CommandProgressMonitor(NLS.bind(Messages.GetOutputStreamCommand_Sending, fRemotePath.toString()),
													getProgressMonitor()), mode);
									input.close();
								} finally {
									fIsClosed = true;
								}
								return 0;
							}
						};
						c.getResult(subMon.newChild(10));
					} catch (AdbException e) {
						throw new IOException(e.getMessage());
					} catch (CoreException e) {
						throw new IOException(e.getMessage());
					}
				}
			}
		};
	}
}
