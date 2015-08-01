package net.sf.etrakr.remote.adb.core.adb;

public interface ICmdProgressMonitor {
	
	public static final int PUT = 0;
	public static final int GET = 1;
	public static final long UNKNOWN_SIZE = -1L;

	void init(int op, String src, String dest, long max);

	boolean count(long count);

	void end();
}
