package net.sf.etrakr.remote.adb.core.adb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public interface ISocketFactory {

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException;

	public InputStream getInputStream(Socket socket) throws IOException;

	public OutputStream getOutputStream(Socket socket) throws IOException;
}
