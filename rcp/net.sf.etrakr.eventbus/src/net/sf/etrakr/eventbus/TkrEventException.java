package net.sf.etrakr.eventbus;

public class TkrEventException extends Exception {

	private static final long serialVersionUID = -2041226089535607009L;

	public int id;
	
	private Throwable cause = null;

	public TkrEventException() {
		super();
	}

	public TkrEventException(String s) {
		super(s);
	}

	public TkrEventException(String s, Throwable e) {
		super(s);
		this.cause = e;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public TkrEventException(int id, String message) {
		super(message);
		this.id = id;
	}

	public TkrEventException(int id, String message, Throwable e) {
		super(message);
		this.id = id;
		this.cause = e;
	}

	public String toString() {
		return id + ": " + getMessage();
	}

}
