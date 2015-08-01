package net.sf.etrakr.remote.adb.core.adb;

public interface ILogger {

	public final int DEBUG = 0;
	public final int INFO = 1;
	public final int WARN = 2;
	public final int ERROR = 3;
	public final int FATAL = 4;

	public boolean isEnabled(int level);

	public void log(int level, String message);
}
