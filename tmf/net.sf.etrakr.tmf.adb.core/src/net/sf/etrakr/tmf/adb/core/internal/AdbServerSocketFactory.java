package net.sf.etrakr.tmf.adb.core.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public interface AdbServerSocketFactory {
	public ServerSocket createServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException;
}
