package net.sf.etrakr.remote.adb.core.internal;

public class AdbLocation implements IAdbLocation {

	private static int DEFAULT_PORT = 22;

	private String user;
	private String password;
	private String host;
	private int port = DEFAULT_PORT;
	private boolean userFixed = true;
	private String comment = null;
	private IPasswordStore passwordStore = null;

	/*
	 * Create a JSchLocation from its composite parts.
	 */
	public AdbLocation(String user, String host, int port) {
		this.user = user;
		this.host = host;
		this.port = port;
	}

	public AdbLocation(String user, String host) {
		this(user, host, DEFAULT_PORT);
	}

	/**
	 * @see IJSchLocation#getHost()
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @see IJSchLocation#getPort()
	 */
	public int getPort() {
		return port;
	}

	/*
	 * @see IJSchLocation#setUsername(String)
	 */
	public void setUsername(String user) {
		if (userFixed)
			throw new UnsupportedOperationException();
		this.user = user;
	}

	/**
	 * @see IJSchLocation#getUsername()
	 */
	public String getUsername() {
		return user == null ? "" : user; //$NON-NLS-1$
	}

	/**
	 * @see IJSchLocation#setPassword(String)
	 */
	public void setPassword(String password) {
		if (password != null)
			this.password = password;
	}

	/**
	 * @see IJSchLocation#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @see IJSchLocation#setComment(String comment)
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @see IJSchLocation#getComment()
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @see IJSchLocation#setPasswordStore(IPasswordStore store)
	 */
	public void setPasswordStore(IPasswordStore store) {
		this.passwordStore = store;
	}

	/**
	 * @see IJSchLocation#getPasswordStore()
	 */
	public IPasswordStore getPasswordStore() {
		return passwordStore;
	}

	/**
	 * Implementation of inherited toString()
	 */
	public String toString() {
		return user + "@" + host + ((port == DEFAULT_PORT) ? "" : ":" + (new Integer(port).toString())); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

}
