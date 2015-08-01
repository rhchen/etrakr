package net.sf.etrakr.remote.adb.core.adb;

public class AdbException extends Exception {

	private static final long serialVersionUID = 3358750538483438182L;

	public int id;
	
	private Throwable cause = null;

	public AdbException() {
		super();
	}

	public AdbException(String s) {
		super(s);
	}

	public AdbException(String s, Throwable e) {
		super(s);
		this.cause = e;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public AdbException(int id, String message) {
		super(message);
		this.id = id;
	}

	public AdbException(int id, String message, Throwable e) {
		super(message);
		this.id = id;
		this.cause = e;
	}

	public String toString() {
		return id + ": " + getMessage();
	}

}
