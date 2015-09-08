package net.sf.etrakr.tmf.remote.adb.core;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

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

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

public class TmfAdbService {

	public static final String remoteServicesId = "net.sf.etrakr.remote.adb";
	public static final String sHostUri = "adb://localhost";
	public static final String hostName = "local_adb";

	//public static final String CMD_INPUT_SUPPORT_TAGS = "atrace --list_categories";
	public static final String[] CMD_INPUT_SUPPORT_TAGS = {"atrace","--list_categories"};
			
	private RemoteSystemProxy proxy;
	
	public TmfAdbService() throws RemoteConnectionException, ExecutionException, URISyntaxException {
		
		init();
	}

	public String getSystraceOutput() throws ExecutionException{
		
		List<SystraceTag> l = getSystraceSupportTags();
		
		/*
		 * Fix me. RH
		 * Default disable data compress
		 * A special implemented IShellOutputReceiver is required to handle the byte data, zip compressed
		 */
		boolean COMPRESS_DATA = false;
		
		SystraceOptions mOptions = new SystraceOptions();
		
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
		
		String s = getSystraceData(joined.getBytes(Charsets.UTF_8), COMPRESS_DATA);
		
		return s;
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
	
	private void init() throws URISyntaxException, RemoteConnectionException, ExecutionException{
		
		URI hostUri = URIUtil.fromString(sHostUri);
		
		TmfRemoteConnectionFactory.registerConnectionFactory(remoteServicesId, new TmfAdbConnectionFactory());

		IRemoteConnection conn = TmfRemoteConnectionFactory.createConnection(hostUri, hostName);

		proxy = new RemoteSystemProxy(conn);

		IProgressMonitor monitor = new NullProgressMonitor();

		proxy.connect(monitor);
		
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
	
	public class SystraceTag {
		
	    public final String tag;
	    public final String info;

	    public SystraceTag(String tagName, String details) {
	        tag = tagName;
	        info = details;
	    }
	}
	
	class SystraceOptions{
		
        private int mTraceBufferSize = 1024;
        private int mTraceDuration = 5;
        private String mTraceApp;

        public String getOptions(List<SystraceTag> mSupportedTags) {
        	
            StringBuilder sb = new StringBuilder(5 * mSupportedTags.size());

            if (mTraceApp != null) {
                sb.append("-a ");   //$NON-NLS-1$
                sb.append(mTraceApp);
                sb.append(' ');
            }

            if (mTraceDuration > 0) {
                sb.append("-t");    //$NON-NLS-1$
                sb.append(mTraceDuration);
                sb.append(' ');
            }

            if (mTraceBufferSize > 0) {
                sb.append("-b ");   //$NON-NLS-1$
                sb.append(mTraceBufferSize);
                sb.append(' ');
            }

            Set<String> sEnabledTags = new HashSet<String>();
			
	        for (int i = 0; i < mSupportedTags.size(); i++) {
	        	
	        	sEnabledTags.add(mSupportedTags.get(i).tag);
	        
	        }
	        
            for (String s : sEnabledTags) {
                sb.append(s);
                sb.append(' ');
            }

            return sb.toString().trim();
        }
    }
	
}
