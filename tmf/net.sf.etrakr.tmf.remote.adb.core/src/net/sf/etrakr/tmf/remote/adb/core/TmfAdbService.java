package net.sf.etrakr.tmf.remote.adb.core;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;
import static org.eclipse.tracecompass.common.core.NonNullUtils.nullToEmptyString;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteProcess;
import org.eclipse.remote.core.IRemoteProcessService;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.tracecompass.internal.tmf.remote.core.Activator;
import org.eclipse.tracecompass.internal.tmf.remote.core.messages.Messages;
import org.eclipse.tracecompass.internal.tmf.remote.core.preferences.TmfRemotePreferences;
import org.eclipse.tracecompass.internal.tmf.remote.core.shell.CommandResult;
import org.eclipse.tracecompass.internal.tmf.remote.core.shell.CommandShell;
import org.eclipse.tracecompass.tmf.remote.core.proxy.RemoteSystemProxy;
import org.eclipse.tracecompass.tmf.remote.core.proxy.TmfRemoteConnectionFactory;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandInput;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandResult;
import org.eclipse.tracecompass.tmf.remote.core.shell.ICommandShell;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;

import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceOptions;
import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceParser;
import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceTag;

public class TmfAdbService {

	public static final String remoteServicesId = "net.sf.etrakr.remote.adb";
	public static final String sHostUri = "adb://localhost";
	public static final String hostName = "local_adb";

	//public static final String CMD_INPUT_SUPPORT_TAGS = "atrace --list_categories";
	public static final String[] CMD_INPUT_SUPPORT_TAGS = {"atrace","--list_categories"};
			
	private RemoteSystemProxy proxy;
	
	private SystraceOptions mOptions;
	
	public TmfAdbService() {
		
	}

	public TmfAdbService(SystraceOptions systraceOptions) {
		
		this.mOptions = systraceOptions;
	}

	public static TmfAdbService init() throws URISyntaxException, RemoteConnectionException, ExecutionException{
		
		return new TmfAdbService().connect();
		
	}

	public TmfAdbService push(SystraceOptions systraceOptions) throws RemoteConnectionException, ExecutionException, URISyntaxException{
		
		this.mOptions = systraceOptions;
		
		return this;
		
	}

	public TmfAdbService timeout(int timeout) {

		int oriTimeout = TmfRemotePreferences.getCommandTimeout();

		String PLUGIN_ID = "org.eclipse.tracecompass.tmf.remote.core";
		
		IEclipsePreferences defaultPreferences = InstanceScope.INSTANCE.getNode(PLUGIN_ID);

		// Set default User ID if none already stored in preferences
		defaultPreferences.put(TmfRemotePreferences.TRACE_CONTROL_COMMAND_TIMEOUT_PREF,String.valueOf(timeout));
		
		return this;
	}
	
	public String go() throws ExecutionException{
		
		List<SystraceTag> l = getSystraceSupportTags();
		
		/*
		 * Fix me. RH
		 * Default disable data compress
		 * A special implemented IShellOutputReceiver is required to handle the byte data, zip compressed
		 */
		boolean COMPRESS_DATA = true;
		
		//SystraceOptions mOptions = new SystraceOptions();
		
		final String atraceOptions = mOptions.getOptions(l) + (COMPRESS_DATA ? " -z" : "");
		
		final String cmd = "atrace " + atraceOptions;
		
		/* Fix me. RH */
		final String[] cmdArray = cmd.split(" ");
		ICommandShell shell = proxy.createCommandShell();

		ICommandInput command = shell.createCommand();
		command.addAll(checkNotNull(Arrays.asList(cmdArray)));
		ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
		
		int r = result.getResult();
		
		if(r != 0) throw new ExecutionException("adb command fail : getSystraceSupportTags");
		
		List<String> atraceOutput = result.getOutput();
		
		char c = "\n".charAt(0);
		
		String joined = Joiner.on(c).join(atraceOutput);
		
		//String s = getSystraceData(joined.getBytes(Charsets.UTF_8), COMPRESS_DATA);
		
		return joined;
	}
	
	public CommandResult gogo(final String[] cmdArray, final IProgressMonitor aMonitor) throws ExecutionException{
		
		ICommandShell shell = proxy.createCommandShell();
		final ICommandInput command = shell.createCommand();
		command.addAll(checkNotNull(Arrays.asList(cmdArray)));
		
		final ExecutorService fExecutor = checkNotNull(Executors.newFixedThreadPool(1));
		
		FutureTask<CommandResult> future = new FutureTask<>(new Callable<CommandResult>() {
            @Override
            public CommandResult call() throws IOException, InterruptedException {
                IProgressMonitor monitor = aMonitor;
                if (monitor == null) {
                    monitor = new NullProgressMonitor();
                }
                if (!monitor.isCanceled()) {
                    IRemoteProcess process = proxy.getRemoteConnection().getService(IRemoteProcessService.class).getProcessBuilder(command.getInput()).start();
                    InputReader stdout = new InputReader(checkNotNull(process.getInputStream()));
                    InputReader stderr = new InputReader(checkNotNull(process.getErrorStream()));

                    try {
                        stdout.waitFor(monitor);
                        stderr.waitFor(monitor);
                        if (!monitor.isCanceled()) {
                            return createResult(process.waitFor(), stdout.toString(), stderr.toString());
                        }
                    } catch (OperationCanceledException e) {
                    } catch (InterruptedException e) {
                        return new CommandResult(1, new String[0], new String[] {e.getMessage()});
                    } finally {
                        stdout.stop();
                        stderr.stop();
                        process.destroy();
                    }
                }
                return new CommandResult(1, new String[0], new String[] {"cancelled"}); //$NON-NLS-1$
            }
        });

        fExecutor.execute(future);

        try {
            return checkNotNull(future.get(TmfRemotePreferences.getCommandTimeout(), TimeUnit.MINUTES));
        } catch (InterruptedException ex) {
            throw new ExecutionException(Messages.RemoteConnection_ExecutionCancelled, ex);
        } catch (TimeoutException ex) {
            throw new ExecutionException(Messages.RemoteConnection_ExecutionTimeout, ex);
        } catch (Exception ex) {
            throw new ExecutionException(Messages.RemoteConnection_ExecutionFailure, ex);
        }
        finally {
            future.cancel(true);
        }
	}
	
	public List<SystraceTag> getSystraceSupportTags() throws ExecutionException{
		
		ICommandShell shell = proxy.createCommandShell();

		ICommandInput command = shell.createCommand();
		command.addAll(checkNotNull(Arrays.asList(CMD_INPUT_SUPPORT_TAGS)));
		ICommandResult result = shell.executeCommand(command, new NullProgressMonitor());
		
		int r = result.getResult();
		
		if(r != 0) throw new ExecutionException("adb command fail : getSystraceSupportTags");
		
		List<String> listCategoriesOutput = result.getOutput();
		
		List<SystraceTag> l = parseSupportedTags(listCategoriesOutput);
		
		return l;
	}

	private TmfAdbService connect() throws URISyntaxException, RemoteConnectionException, ExecutionException{
		
		URI hostUri = URIUtil.fromString(sHostUri);
		
		TmfRemoteConnectionFactory.registerConnectionFactory(remoteServicesId, new TmfAdbConnectionFactory());

		IRemoteConnection conn = TmfRemoteConnectionFactory.createConnection(hostUri, hostName);

		proxy = new RemoteSystemProxy(conn){

			@Override
			public ICommandShell createCommandShell() {

				return new CommandShell(super.getRemoteConnection()){

					private final ExecutorService fExecutor = checkNotNull(Executors.newFixedThreadPool(1));
					
					@Override
					public void dispose() {
						fExecutor.shutdown();
					}

					@Override
					public ICommandResult executeCommand(final ICommandInput command, final IProgressMonitor aMonitor)
							throws ExecutionException {

						final IRemoteConnection fConnection = proxy.getRemoteConnection();
						
						if (fConnection.isOpen()) {
							
							FutureTask<CommandResult> future = new FutureTask<>(new Callable<CommandResult>() {
								
								@Override
								public CommandResult call() throws IOException, InterruptedException {
									
									/* Fix me. RH
									 * Workarround for atrace command with -z option
									 * Default handle split the command output to list array
									 * This corrupt the zip format and hard to reassemble it
									 */
									if(command.toString().contains("-z")){
										
										IRemoteProcess process = fConnection.getService(IRemoteProcessService.class).getProcessBuilder(command.getInput()).start();
										
										byte[] b1 = ByteStreams.toByteArray(checkNotNull(process.getInputStream()));
										
										String stdout = SystraceParser.create().handleData(b1).getSystraceData(true);
										
										byte[] b2 = ByteStreams.toByteArray(checkNotNull(process.getErrorStream()));
										
										String stderr = new String(b2);
										
										return createResult(process.waitFor(), stdout, stderr);
									}
									
									return Handle();
								}
								
								private CommandResult Handle() throws IOException{
									
									IProgressMonitor monitor = aMonitor;
									if (monitor == null) {
										monitor = new NullProgressMonitor();
									}
									if (!monitor.isCanceled()) {
										IRemoteProcess process = fConnection.getService(IRemoteProcessService.class).getProcessBuilder(command.getInput()).start();

										InputReader stdout = new InputReader(checkNotNull(process.getInputStream()));
										InputReader stderr = new InputReader(checkNotNull(process.getErrorStream()));

										try {
											stdout.waitFor(monitor);
											stderr.waitFor(monitor);
											if (!monitor.isCanceled()) {

												return createResult(process.waitFor(), stdout.toString(),stderr.toString());
											}
										} catch (OperationCanceledException e) {
										} catch (InterruptedException e) {
											return new CommandResult(1, new String[0], new String[] { e.getMessage() });
										} finally {
											stdout.stop();
											stderr.stop();
											process.destroy();
										}
									}
									
									return new CommandResult(1, new String[0], new String[] { "cancelled" }); //$NON-NLS-1$
									
								}
							});

							fExecutor.execute(future);

							try {
								return checkNotNull(
										future.get(TmfRemotePreferences.getCommandTimeout(), TimeUnit.SECONDS));
							} catch (InterruptedException ex) {
								throw new ExecutionException(Messages.RemoteConnection_ExecutionCancelled, ex);
							} catch (TimeoutException ex) {
								throw new ExecutionException(Messages.RemoteConnection_ExecutionTimeout, ex);
							} catch (Exception ex) {
								throw new ExecutionException(Messages.RemoteConnection_ExecutionFailure, ex);
							} finally {
								future.cancel(true);
							}
						}
						
						throw new ExecutionException(Messages.RemoteConnection_ShellNotConnected, null);

					}
					
				};
			}
			
		};

		IProgressMonitor monitor = new NullProgressMonitor();

		proxy.connect(monitor);
		
		return this;
	}
	
	private TmfAdbService connect2() throws URISyntaxException, RemoteConnectionException, ExecutionException{
		
		URI hostUri = URIUtil.fromString(sHostUri);
		
		TmfRemoteConnectionFactory.registerConnectionFactory(remoteServicesId, new TmfAdbConnectionFactory());

		IRemoteConnection conn = TmfRemoteConnectionFactory.createConnection(hostUri, hostName);

		proxy = new RemoteSystemProxy(conn);

		IProgressMonitor monitor = new NullProgressMonitor();

		proxy.connect(monitor);
		
		return this;
	}
	
	private List<SystraceTag> parseSupportedTags(List<String> listCategoriesOutput) {
		
		if (listCategoriesOutput == null) {
			return null;
		}

		if (listCategoriesOutput.contains("unknown option")) {
			return null;
		}

		String[] categories = listCategoriesOutput.toArray(new String[listCategoriesOutput.size()]);
		List<SystraceTag> tags = new ArrayList<SystraceTag>(listCategoriesOutput.size());

		Pattern p = Pattern.compile("([^-]+) - (.*)"); //$NON-NLS-1$
		for (String category : categories) {
			Matcher m = p.matcher(category);
			if (m.find()) {
				tags.add(new SystraceTag(m.group(1).trim(), m.group(2).trim()));
			}
		}

		return tags;
	}
	
	private String getSystraceData(byte[] atraceOutput, boolean mUncompress){
		
		byte[] mAtraceOutput = atraceOutput;
        int mAtraceLength = atraceOutput.length;
        int mSystraceIndex = -1;
        
        
		String header = new String(mAtraceOutput, 0, Math.min(100, mAtraceLength));
	    mSystraceIndex = locateSystraceData(header);
	    
	    if (mSystraceIndex < 0) {
            throw new RuntimeException("Unable to find trace start marker 'TRACE:':\n" + header);
        }
	    
	    String trace = "";
        if (mUncompress) {
            Inflater decompressor = new Inflater();
            decompressor.setInput(mAtraceOutput, mSystraceIndex, mAtraceLength - mSystraceIndex);

            byte[] buf = new byte[4096];
            int n;
            StringBuilder sb = new StringBuilder(1000);
            try {
                while ((n = decompressor.inflate(buf)) > 0) {
                    sb.append(new String(buf, 0, n));
                }
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }
            decompressor.end();

            trace = sb.toString();
        } else {
            trace = new String(mAtraceOutput, mSystraceIndex, mAtraceLength - mSystraceIndex);
        }
        
        return trace;
	}
	
	private int locateSystraceData(String header) {
		
		final String TRACE_START = "TRACE:\n";
		
        int index = header.indexOf(TRACE_START);
        if (index < 0) {
            return -1;
        } else {
            return index + TRACE_START.length();
        }
    }
	
	class InputReader {
	    private static final int JOIN_TIMEOUT = 300;
	    private static final int BYTES_PER_KB = 1024;

	    private final InputStreamReader fReader;
	    private final Thread fThread;
	    private final StringBuilder fResult;
	    private volatile boolean fDone;

	    public InputReader(InputStream inputStream) {
	        fResult = new StringBuilder();
	        fReader = new InputStreamReader(inputStream);
	        fThread = new Thread() {
	            @Override
	            public void run() {
	                final char[] buffer = new char[BYTES_PER_KB];
	                int read;
	                try {
	                    while (!fDone && (read = fReader.read(buffer)) > 0) {
	                    	
	                    	System.out.println("buffer : "+ String.valueOf(buffer));
	                        fResult.append(buffer, 0, read);
	                    }
	                } catch (IOException e) {
	                	e.printStackTrace();
	                }
	            }
	        };
	        fThread.start();
	    }

	    public void waitFor(IProgressMonitor monitor) throws InterruptedException {
	        while (fThread.isAlive() && (!monitor.isCanceled())) {
	            fThread.join(JOIN_TIMEOUT);
	        }
	    }

	    public void stop() {
	        fDone = true;
	        fThread.interrupt();
	    }

	    @Override
	    public String toString() {
	        return nullToEmptyString(fResult.toString());
	    }

	}
	
	private static CommandResult createResult(int origResult, String origStdout, String origStderr) {
        final int result;
        final String stdout, stderr;
        result = origResult;
        stdout = origStdout;
        stderr = origStderr;
        String[] output = splitLines(stdout);
        String[] error = splitLines(stderr);
        
//        String[] output = new String[]{origStdout};
//        String[] error  = new String[]{origStderr};
        
        return new CommandResult(result, output, error);
    }

    private static @NonNull String[] splitLines(String output) {
        return checkNotNull(output.split("\\r?\\n")); //$NON-NLS-1$
    }
	
}
