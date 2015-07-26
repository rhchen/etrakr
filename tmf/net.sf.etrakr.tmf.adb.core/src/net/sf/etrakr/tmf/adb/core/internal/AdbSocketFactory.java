package net.sf.etrakr.tmf.adb.core.internal;

import java.net.*;
import java.io.*;

public interface AdbSocketFactory {
	public Socket createSocket(String host, int port)throws IOException,UnknownHostException;
	public InputStream getInputStream(Socket socket)throws IOException;
	public OutputStream getOutputStream(Socket socket)throws IOException;
}
