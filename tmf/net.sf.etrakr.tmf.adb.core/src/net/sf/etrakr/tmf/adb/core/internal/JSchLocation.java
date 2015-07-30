package net.sf.etrakr.tmf.adb.core.internal;

import org.eclipse.core.runtime.PlatformObject;

public class JSchLocation extends PlatformObject implements IJSchLocation {

	/**
	 * port value which indicates to a connection method to use the default port
	 */
	private static int DEFAULT_PORT = 22;

	private String user;
	private String password;
	private String host;
	private int port = DEFAULT_PORT;
	private boolean userFixed = true;
	private String comment = null;
	
	/*
	 * Create a JSchLocation from its composite parts.
	 */
	public JSchLocation(String user, String host, int port) {
		this.user = user;
		this.host = host;
		this.port = port;
	}
	
	public JSchLocation(String user, String host) {
		this(user, host, DEFAULT_PORT);
	}
	  
	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setUsername(String username) {
		if(userFixed)
		      throw new UnsupportedOperationException();
		    this.user=user;
	}

	@Override
	public String getUsername() {
		return user==null ? "" : user; //$NON-NLS-1$
	}

	@Override
	public void setPassword(String password) {
		if(password!=null)
		      this.password=password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setComment(String comment) {
		this.comment=comment;
	}

	@Override
	public String getComment() {
		return comment;
	}

}
