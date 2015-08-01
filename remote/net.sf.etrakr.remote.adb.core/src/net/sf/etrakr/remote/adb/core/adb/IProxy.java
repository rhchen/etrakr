package net.sf.etrakr.remote.adb.core.adb;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface IProxy {

	void connect(ISocketFactory socket_factory, String host, int port, int timeout) throws Exception;

	InputStream getInputStream();

	OutputStream getOutputStream();

	Socket getSocket();

	void close();
}
