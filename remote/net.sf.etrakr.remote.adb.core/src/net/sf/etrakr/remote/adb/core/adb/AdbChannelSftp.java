package net.sf.etrakr.remote.adb.core.adb;

import java.io.InputStream;
import java.util.*;

public class AdbChannelSftp extends AdbChannelSession {

	public static final int OVERWRITE = 0;
	public static final int RESUME = 1;
	public static final int APPEND = 2;

	public static final int SSH_FX_NO_SUCH_FILE = 2;

	AdbChannelSftp() {
		super();
	}

	public void start() throws AdbException {
		AdbSession _session = getSession();

		if (io.in != null) {
			thread = new Thread(this);
			thread.setName("Shell for " + _session.host);
			if (_session.daemon_thread) {
				thread.setDaemon(_session.daemon_thread);
			}
			thread.start();
		}
	}

	void init() throws AdbException {
		io.setInputStream(getSession().in);
		io.setOutputStream(getSession().out);
	}

	public void setMtime(String path, int mtime) throws AdbException {

		/* RH. TBD */
	}

	public void chmod(int permissions, String path) throws AdbException {

		/* RH. TBD */
	}

	public void mkdir(String path) throws AdbException {

		/* RH. TBD */
	}

	public void put(InputStream src, String dst, ICmdProgressMonitor monitor, int mode) throws AdbException {

		/* RH. TBD */
	}

	public InputStream get(String src) throws AdbException {
		return get(src, null, 0L);
	}

	public InputStream get(String src, ICmdProgressMonitor monitor) throws AdbException {
		return get(src, monitor, 0L);
	}

	public InputStream get(String src, final ICmdProgressMonitor monitor, final long skip) throws AdbException {

		/* RH. TBD */
		return null;
	}

	public Attrs lstat(String path) throws AdbException {

		/* RH. TBD */
		return null;
	}

	public String readlink(String path) throws AdbException {

		/* RH. TBD */
		return null;
	}

	public java.util.Vector ls(String path) throws AdbException{
		
		/* RH. TBD */
		return null;
	}
	
	public class LsEntry implements Comparable {
		private String filename;
		private String longname;
		private Attrs attrs;

		LsEntry(String filename, String longname, Attrs attrs) {
			setFilename(filename);
			setLongname(longname);
			setAttrs(attrs);
		}

		public String getFilename() {
			return filename;
		};

		void setFilename(String filename) {
			this.filename = filename;
		};

		public String getLongname() {
			return longname;
		};

		void setLongname(String longname) {
			this.longname = longname;
		};

		public Attrs getAttrs() {
			return attrs;
		};

		void setAttrs(Attrs attrs) {
			this.attrs = attrs;
		};

		public String toString() {
			return longname;
		}

		public int compareTo(Object o) throws ClassCastException {
			if (o instanceof LsEntry) {
				return filename.compareTo(((LsEntry) o).getFilename());
			}
			throw new ClassCastException("a decendent of LsEntry must be given.");
		}
	}
}
