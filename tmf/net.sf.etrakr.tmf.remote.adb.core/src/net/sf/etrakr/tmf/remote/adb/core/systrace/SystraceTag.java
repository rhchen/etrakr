package net.sf.etrakr.tmf.remote.adb.core.systrace;

public class SystraceTag {
	
    public final String tag;
    public final String info;

    public SystraceTag(String tagName, String details) {
        tag = tagName;
        info = details;
    }
}
