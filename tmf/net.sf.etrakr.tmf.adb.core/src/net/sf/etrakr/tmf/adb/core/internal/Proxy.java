package net.sf.etrakr.tmf.adb.core.internal;

import java.io.*;
import java.net.Socket;
public interface Proxy{
  void connect(AdbSocketFactory socket_factory, String host, int port, int timeout) throws Exception;
  InputStream getInputStream();
  OutputStream getOutputStream();
  Socket getSocket();
  void close();
}