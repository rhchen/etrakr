package net.sf.etrakr.tmf.adb.core.internal;

import java.io.*;

public class AdbIO {

	InputStream in;
	OutputStream out;
	OutputStream out_ext;

	private boolean in_dontclose = false;
	private boolean out_dontclose = false;
	private boolean out_ext_dontclose = false;

	void setOutputStream(OutputStream out) {
		this.out = out;
	}

	void setOutputStream(OutputStream out, boolean dontclose) {
		this.out_dontclose = dontclose;
		setOutputStream(out);
	}

	void setExtOutputStream(OutputStream out) {
		this.out_ext = out;
	}

	void setExtOutputStream(OutputStream out, boolean dontclose) {
		this.out_ext_dontclose = dontclose;
		setExtOutputStream(out);
	}

	void setInputStream(InputStream in) {
		this.in = in;
	}

	void setInputStream(InputStream in, boolean dontclose) {
		this.in_dontclose = dontclose;
		setInputStream(in);
	}

	public void put(AdbPacket p) throws IOException, java.net.SocketException {
		out.write(p.buffer.buffer, 0, p.buffer.index);
		out.flush();
	}

	void put(byte[] array, int begin, int length) throws IOException {
		out.write(array, begin, length);
		out.flush();
	}

	void put_ext(byte[] array, int begin, int length) throws IOException {
		out_ext.write(array, begin, length);
		out_ext.flush();
	}

	int getByte() throws IOException {
		return in.read();
	}

	void getByte(byte[] array) throws IOException {
		getByte(array, 0, array.length);
	}

	void getByte(byte[] array, int begin, int length) throws IOException {
		do {
			int completed = in.read(array, begin, length);
			if (completed < 0) {
				throw new IOException("End of IO Stream Read");
			}
			begin += completed;
			length -= completed;
		} while (length > 0);
	}

	void out_close() {
		try {
			if (out != null && !out_dontclose)
				out.close();
			out = null;
		} catch (Exception ee) {
		}
	}

	public void close() {
		try {
			if (in != null && !in_dontclose)
				in.close();
			in = null;
		} catch (Exception ee) {
		}

		out_close();

		try {
			if (out_ext != null && !out_ext_dontclose)
				out_ext.close();
			out_ext = null;
		} catch (Exception ee) {
		}
	}
}
