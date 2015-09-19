package net.sf.etrakr.tmf.remote.adb.core.test;

import org.junit.Assert;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandInput;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandResult;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandShell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService;
import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceOptions;
import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceTag;

public class TmfAdbServiceTest2 {

	private static final @NonNull String[] CMD_INPUT_ADB = { "cat","/sys/kernel/debug/tracing/trace_pipe"};
	//private static final @NonNull String[] CMD_INPUT_ADB = { "cat","/sys/kernel/debug/tracing/trace"};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	public void test1() throws ExecutionException, RemoteConnectionException, URISyntaxException, InterruptedException {
		
		/*
		 * Fix me. RH
		 * This test is for inputstream read tracing_pip
		 * Not work still
		 */
		ICommandResult result =TmfAdbService.init().gogo(CMD_INPUT_ADB, new NullProgressMonitor());
		
		int r = result.getResult();
		
		if(r != 0) throw new ExecutionException("adb command fail : getSystraceSupportTags");
		
		List<String> atraceOutput = result.getOutput();
		
		Thread.sleep(10000);
		
		System.out.println("TmfAdbServiceTest2.test1 "+ atraceOutput);
	}
	
}
