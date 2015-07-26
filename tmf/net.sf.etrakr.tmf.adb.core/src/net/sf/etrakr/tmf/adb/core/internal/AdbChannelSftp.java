package net.sf.etrakr.tmf.adb.core.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class AdbChannelSftp extends AdbChannel {

	public static final int OVERWRITE = 0;
	public static final int RESUME = 1;
	public static final int APPEND = 2;

	public static final int SSH_FX_NO_SUCH_FILE=                  2;
	
	@Override
	public void run() {

	}

	public void mkdir(String path) throws AdbException {

		/* TBD */
	}

	public void ls(String path, LsEntrySelector selector) throws AdbException {
		/* TBD */
	}

	public AdbSftpATTRS lstat(String path) throws AdbException {
		/* TBD */
		return null;
	}

	public String readlink(String path) throws AdbException {
		/* TBD */
		return null;
	}

	public java.util.Vector ls(String path) throws AdbException {
		final java.util.Vector v = new Vector();
		LsEntrySelector selector = new LsEntrySelector() {
			public int select(LsEntry entry) {
				v.addElement(entry);
				return CONTINUE;
			}
		};
		ls(path, selector);
		return v;
	}

	public interface LsEntrySelector {
		public final int CONTINUE = 0;
		public final int BREAK = 1;

		/**
		 * <p>
		 * The <code>select</code> method will be invoked in <code>ls</code>
		 * method for each file entry. If this method returns BREAK,
		 * <code>ls</code> will be canceled.
		 * 
		 * @param entry
		 *            one of entry from ls
		 * @return if BREAK is returned, the 'ls' operation will be canceled.
		 */
		public int select(LsEntry entry);
	}

	public class LsEntry implements Comparable {
		private String filename;
		private String longname;
		private AdbSftpATTRS attrs;

		LsEntry(String filename, String longname, AdbSftpATTRS attrs) {
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

		public AdbSftpATTRS getAttrs() {
			return attrs;
		};

		void setAttrs(AdbSftpATTRS attrs) {
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

	public void put(InputStream src, String dst) throws AdbException {
		put(src, dst, null, OVERWRITE);
	}

	public void put(InputStream src, String dst, int mode) throws AdbException {
		put(src, dst, null, mode);
	}

	public void put(InputStream src, String dst, AdbSftpProgressMonitor monitor) throws AdbException {
		put(src, dst, monitor, OVERWRITE);
	}

	public void put(InputStream src, String dst, AdbSftpProgressMonitor monitor, int mode) throws AdbException {

		if (monitor != null) {
			monitor.init(AdbSftpProgressMonitor.PUT, "-", dst, AdbSftpProgressMonitor.UNKNOWN_SIZE);
		}

		_put(src, dst, monitor, mode);
	}

	public void _put(InputStream src, String dst, AdbSftpProgressMonitor monitor, int mode) throws AdbException {

	}

	public OutputStream put(String dst) throws AdbException {
		return put(dst, (AdbSftpProgressMonitor) null, OVERWRITE);
	}

	public OutputStream put(String dst, final int mode) throws AdbException {
		return put(dst, (AdbSftpProgressMonitor) null, mode);
	}

	public OutputStream put(String dst, final AdbSftpProgressMonitor monitor, final int mode) throws AdbException {
		return put(dst, monitor, mode, 0);
	}

	public OutputStream put(String dst, final AdbSftpProgressMonitor monitor, final int mode, long offset)
			throws AdbException {

		/* TBD */
		return null;
	}
	
	public void chmod(int permissions, String path) throws AdbException{
		/* TBD */
	}
	
	public InputStream get(String src) throws AdbException{
	    return get(src, null, 0L);
	  }
	
	public InputStream get(String src, AdbSftpProgressMonitor monitor) throws AdbException{
	    return get(src, monitor, 0L);
	  }
	
	public InputStream get(String src, final AdbSftpProgressMonitor monitor, final long skip) throws AdbException{
		/* TBD */
		return null;
	}
	
	public void setMtime(String path, int mtime) throws AdbException{
		/* TBD */
	}
}
