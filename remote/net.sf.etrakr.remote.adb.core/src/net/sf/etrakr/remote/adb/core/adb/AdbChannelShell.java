package net.sf.etrakr.remote.adb.core.adb;

import java.util.*;

public class AdbChannelShell extends AdbChannelSession {

	AdbChannelShell() {
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
}
