package net.sf.etrakr.tmf.adb.core.internal;

public class AdbChannelExec extends AdbChannel {

	byte[] command = new byte[0];

	@Override
	public void run() {

	}

	public void setCommand(String command) {
		this.command = AdbUtil.str2byte(command);
	}

	public void setCommand(byte[] command) {
		this.command = command;
	}

	public void setErrStream(java.io.OutputStream out) {
		setExtOutputStream(out);
	}

	public void setErrStream(java.io.OutputStream out, boolean dontclose) {
		setExtOutputStream(out, dontclose);
	}

	public java.io.InputStream getErrStream() throws java.io.IOException {
		return getExtInputStream();
	}
}
