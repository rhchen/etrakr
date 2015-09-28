package net.sf.etrakr.tmf.remote.adb.core.systrace;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class SystraceParser {

	private static final String TRACE_START = "TRACE:\n"; //$NON-NLS-1$

    private byte[] mAtraceOutput;
    private int mAtraceLength;
    private int mSystraceIndex = -1;
    
    public static SystraceParser create(){
    	
    	return new SystraceParser();
    }
    
    public SystraceParser handleData(byte[] atraceOutput){
		
		mAtraceOutput = atraceOutput;
        mAtraceLength = atraceOutput.length;
        
        removeCrLf();
        
        // locate the trace start marker within the first hundred bytes
        String header = new String(mAtraceOutput, 0, Math.min(100, mAtraceLength));
        mSystraceIndex = locateSystraceData(header);

        if (mSystraceIndex < 0) {
            throw new RuntimeException("Unable to find trace start marker 'TRACE:':\n" + header);
        }
        
        return this;
	}

	/** Replaces \r\n with \n in {@link #mAtraceOutput}. */
    private void removeCrLf() {
        int dst = 0;
        for (int src = 0; src < mAtraceLength - 1; src++, dst++) {
            byte copy;
            if (mAtraceOutput[src] == '\r' && mAtraceOutput[src + 1] == '\n') {
                copy = '\n';
                src++;
            } else {
                copy = mAtraceOutput[src];
            }
            mAtraceOutput[dst] = copy;
        }

        mAtraceLength = dst;
    }
    
    private int locateSystraceData(String header) {
        int index = header.indexOf(TRACE_START);
        if (index < 0) {
            return -1;
        } else {
            return index + TRACE_START.length();
        }
    }
    
	public String getSystraceData(boolean mUncompress) {
        if (mSystraceIndex < 0) {
            return "";
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

        // each line should end with the characters \n\ followed by a newline
        String html_out = trace.replaceAll("\n", "\\\\n\\\\\n");
        
        return html_out;
    }
}
