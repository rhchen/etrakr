/*******************************************************************************
 * Copyright (c) 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bernd Hufmann - Initial API and implementation
 *******************************************************************************/

package net.sf.etrakr.tmf.remote.adb.core.test;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.remote.core.IRemoteCommandShellService;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionType;
import org.eclipse.remote.core.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.IRemoteProcess;
import org.eclipse.remote.core.IRemoteProcessBuilder;
import org.eclipse.remote.core.IRemoteProcessService;
import org.eclipse.remote.core.IRemoteServicesManager;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.tracecompass.internal.tmf.remote.core.shell.CommandResult;
import org.eclipse.tracecompass.tmf.remote.core.proxy.IConnectionFactory;
import org.eclipse.tracecompass.tmf.remote.core.proxy.RemoteSystemProxy;
import org.eclipse.tracecompass.tmf.remote.core.proxy.TmfRemoteConnectionFactory;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandInput;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandResult;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandShell;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.etrakr.tmf.remote.adb.core.TmfAdbConnectionFactory;


/**
 * Test suite for the {@link CommandShell} class
 */
public class AdbCommandShellTest {

    private static final @NonNull String[] CMD_INPUT_ADB = { "printenv"};
    
    @Test
	public void testExecuteSuccess2()
			throws ExecutionException, IOException, URISyntaxException, RemoteConnectionException {

		final String remoteServicesId = "net.sf.etrakr.remote.adb";
		final String sHostUri = "adb://localhost";
		final String hostName = "local_adb";
		URI hostUri = URIUtil.fromString(sHostUri);
		TmfRemoteConnectionFactory.registerConnectionFactory(remoteServicesId, new TmfAdbConnectionFactory());

		IRemoteConnection conn = TmfRemoteConnectionFactory.createConnection(hostUri, hostName);

		RemoteSystemProxy proxy = new RemoteSystemProxy(conn);

		IProgressMonitor monitor = new NullProgressMonitor();

		proxy.connect(monitor);

		ICommandShell shell = proxy.createCommandShell();

		ICommandInput command = shell.createCommand();
		command.addAll(checkNotNull(Arrays.asList(CMD_INPUT_ADB)));
		ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
		
		int r = result.getResult();
		
		assertEquals(0, r);

		System.out.println("result : "+ result.getOutput().toString());
		
		String str = "";
		Assert.assertNotNull(str);
	}
    
    
    private static CommandResult createResult(int origResult, String origStdout, String origStderr) {
        final int result;
        final String stdout, stderr;
        result = origResult;
        stdout = origStdout;
        stderr = origStderr;
        String[] output = splitLines(stdout);
        String[] error = splitLines(stderr);
        return new CommandResult(result, output, error);
    }

    private static @NonNull String[] splitLines(String output) {
        return checkNotNull(output.split("\\r?\\n")); //$NON-NLS-1$
    }
    

}