package net.sf.etrakr.tmf.remote.adb.core.systrace;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystraceOptions{
	
    protected int mTraceBufferSize = 1024;
    protected int mTraceDuration = 5;
    protected String mTraceApp;

    public static SystraceOptions newSystraceOptions(){
    	
		return new SystraceOptions();
	}
	
    public SystraceOptions BufferSize(int BufferSize){
    	
    	mTraceBufferSize = BufferSize;
    	
    	return this;
    }
    
    public SystraceOptions Duration( int Duration){
    	
    	mTraceDuration = Duration;
    	
    	return this;
    }
    
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
