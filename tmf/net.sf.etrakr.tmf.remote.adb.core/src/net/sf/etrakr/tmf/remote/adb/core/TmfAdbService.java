package net.sf.etrakr.tmf.remote.adb.core;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.tracecompass.tmf.remote.core.proxy.RemoteSystemProxy;
import org.eclipse.tracecompass.tmf.remote.core.proxy.TmfRemoteConnectionFactory;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandInput;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandResult;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandShell;

public class TmfAdbService {

	public static final String remoteServicesId = "net.sf.etrakr.remote.adb";
	public static final String sHostUri = "adb://localhost";
	public static final String hostName = "local_adb";

	public static final String CMD_INPUT_SUPPORT_TAGS = "atrace --list_categories";
			
	private RemoteSystemProxy proxy;
	
	public TmfAdbService() throws RemoteConnectionException, ExecutionException, URISyntaxException {
		
		init();
	}

	public String getSystraceSupportTags() throws ExecutionException{
		
		ICommandShell shell = proxy.createCommandShell();

		ICommandInput command = shell.createCommand();
		command.addAll(checkNotNull(Arrays.asList(CMD_INPUT_SUPPORT_TAGS)));
		ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
		
		int r = result.getResult();
		
		if(r != 0) throw new ExecutionException("adb command fail : getSystraceSupportTags");
		
		String str = result.getOutput().toString();
		
		return str;
	}
	
	private void init() throws URISyntaxException, RemoteConnectionException, ExecutionException{
		
		URI hostUri = URIUtil.fromString(sHostUri);
		
		TmfRemoteConnectionFactory.registerConnectionFactory(remoteServicesId, new TmfAdbConnectionFactory());

		IRemoteConnection conn = TmfRemoteConnectionFactory.createConnection(hostUri, hostName);

		proxy = new RemoteSystemProxy(conn);

		IProgressMonitor monitor = new NullProgressMonitor();

		proxy.connect(monitor);
		
	}
	
}
