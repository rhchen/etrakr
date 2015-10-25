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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.remote.core.IRemoteCommandShellService;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteProcess;
import org.eclipse.remote.core.IRemoteProcessBuilder;
import org.eclipse.remote.core.IRemoteProcessService;
import org.eclipse.tracecompass.internal.tmf.remote.core.shell.CommandResult;
import org.eclipse.tracecompass.tmf.remote.core.proxy.RemoteSystemProxy;
import org.eclipse.tracecompass.tmf.remote.core.proxy.TmfRemoteConnectionFactory;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandInput;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandResult;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandShell;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for the {@link CommandShell} class
 */
public class CommandShellTest {

    private static final boolean IS_NOT_UNIX = Platform.getOS().equals(Platform.OS_WIN32);

    private static final String[] CMD_INPUT_UNIX = { "cmd" , "/C", "dir", "/W" };
    private static final String[] CMD_ERROR_INPUT_UNIX = { "dir", "/Z" };
    private static final String[] CMD_UNKNOWN_COMMAND_UNIX = { "blablablabla" };

    private static final IRemoteConnection LOCAL_CONNECTION = TmfRemoteConnectionFactory.getLocalConnection();
    private static final RemoteSystemProxy LOCAL_PROXY = new RemoteSystemProxy(checkNotNull(LOCAL_CONNECTION));

    /**
     * Test suite for the {@link CommandShell#executeCommand} method
     * @throws ExecutionException
     *            in case of an error
     */
    @Ignore
    //@Test
    public void testExecuteSuccess1() throws ExecutionException {
        assumeTrue(IS_NOT_UNIX);
        LOCAL_PROXY.connect(new NullProgressMonitor());
        ICommandShell shell = LOCAL_PROXY.createCommandShell();

        ICommandInput command = shell.createCommand();
        command.addAll(checkNotNull(Arrays.asList(CMD_INPUT_UNIX)));
        ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
        assertEquals(0, result.getResult());
    }

    @Test
    public void testExecuteSuccess() throws ExecutionException, IOException {
        assumeTrue(IS_NOT_UNIX);

        LOCAL_PROXY.connect(new NullProgressMonitor());
        
        IRemoteCommandShellService cmdSrv = LOCAL_CONNECTION.getService(IRemoteCommandShellService.class);
        
        //IRemoteProcess prcess = cmdSrv.getCommandShell(IRemoteProcessBuilder.NONE);
        
        IRemoteProcessService procService = LOCAL_CONNECTION.getService(IRemoteProcessService.class);
        
		String[] cmd;
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			cmd = new String[] { "cmd" , "/C", "dir", "/W"};
		} else {
			List<String> list = new ArrayList<>(Arrays.asList(System.getenv("SHELL").split("\\s+")));
			list.add("-l");
			cmd = list.toArray(new String[list.size()]);
		}
		
		IRemoteProcessBuilder builder = procService.getProcessBuilder(cmd);
		IRemoteProcess process = builder.start(IRemoteProcessBuilder.NONE);
		
//		stderr = process.getErrorStream();
//		process.getInputStream();
//		stdout = process.getOutputStream();
//		
//		InputReader stdout = new InputReader(checkNotNull(process.getInputStream()));
//        InputReader stderr = new InputReader(checkNotNull(process.getErrorStream()));
//        
//		createResult(process.waitFor(), stdout.toString(), stderr.toString());		
		
        ICommandShell shell = LOCAL_PROXY.createCommandShell();

        ICommandInput command = shell.createCommand();
        command.addAll(checkNotNull(Arrays.asList(CMD_INPUT_UNIX)));
        
        ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
        System.out.println("result : "+ result.getOutput().toString());
        assertTrue(result.getResult() == 0);
    }

    /**
     * Test suite for the {@link CommandShell#executeCommand} method (with exception)
     * @throws ExecutionException
     *            in case of an error
     */
    @Ignore
    //@Test (expected=ExecutionException.class)
    public void testExecuteException() throws ExecutionException {
        if (!IS_NOT_UNIX) {
            throw new ExecutionException("");
        }
        LOCAL_PROXY.connect(new NullProgressMonitor());
        ICommandShell shell = LOCAL_PROXY.createCommandShell();

        ICommandInput command = shell.createCommand();
        command.addAll(checkNotNull(Arrays.asList(CMD_UNKNOWN_COMMAND_UNIX)));
        ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
        assertTrue(result.getResult() > 0);
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

    private static String[] splitLines(String output) {
        return checkNotNull(output.split("\\r?\\n")); //$NON-NLS-1$
    }
}