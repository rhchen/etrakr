package net.sf.etrakr.tmf.adb.core.internal;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IProgressMonitor;

import net.sf.etrakr.tmf.adb.core.AdbActivator;

public class JSchProvider implements IJSchService {

	private static JSchProvider instance;

	public static final String PROXY_TYPE_SOCKS5 = "SOCKS5"; //$NON-NLS-1$
	public static final String PROXY_TYPE_HTTP = "HTTP"; //$NON-NLS-1$
	public static final String HTTP_DEFAULT_PORT = "80"; //$NON-NLS-1$

	public static IJSchService getInstance() {
		if (instance == null)
			instance = new JSchProvider();
		return instance;
	}

	@Override
	public AdbSession createSession(String host, int port, String username) throws AdbException {

		Adb adb = AdbActivator.getDefault().getJSch();
		AdbSession session = adb.getSession(username, host, port);
		return session;
	}

	@Override
	public AdbSession createSession(IJSchLocation location) throws AdbException {

		AdbSession session = createSession(location.getHost(), location.getPort(), location.getUsername());
		return session;
	}

	@Override
	public void connect(AdbSession session, int timeout, IProgressMonitor monitor) throws AdbException {

		session.setSocketFactory(new ResponsiveSocketFactory(monitor, timeout));

		try {
			session.connect();
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			// TODO This catch clause has been added to work around
			// Bug 217980 and will be deleted in the future.
			throw new AdbException("invalid server's version string");//$NON-NLS-1$
		} catch (AdbException e) {

			String host = session.getHost();
			String user = session.getUserName();
			int port = session.getPort();

			if (port == -1)
				port = 22;
			session = getJSch().getSession(user, host, port);

			session.setTimeout(timeout);
			connect(session, timeout, monitor);

			if (session.isConnected())
				session.disconnect();
			throw e;
		}

	}

	private static IProxyData getProxyData(String host, String type) {
		IProxyService proxyService = AdbActivator.getDefault().getProxyService();
		if (proxyService == null)
			return null;
		IProxyData data = proxyService.getProxyDataForHost(host, type);
		if (data == null || data.getHost() == null || getJSchProxyType(data) == null)
			return null;
		return data;
	}

	private static String getJSchProxyType(IProxyData data) {
		if (data.getType().equals(IProxyData.HTTPS_PROXY_TYPE))
			return PROXY_TYPE_HTTP;
		if (data.getType().equals(IProxyData.SOCKS_PROXY_TYPE))
			return PROXY_TYPE_SOCKS5;
		return null;
	}

	private static int getPort(IProxyData data) {
		int port = data.getPort();
		if (port == -1) {
			if (data.getType().equals(IProxyData.HTTP_PROXY_TYPE))
				port = 80;
			else if (data.getType().equals(IProxyData.HTTPS_PROXY_TYPE))
				port = 443;
			else if (data.getType().equals(IProxyData.SOCKS_PROXY_TYPE))
				port = 1080;
		}
		return port;
	}

	@Override
	public Proxy getProxyForHost(String host, String proxyType) {
		IProxyService proxyService = AdbActivator.getDefault().getProxyService();
		if (proxyService == null)
			return null;
		boolean useProxy = proxyService.isProxiesEnabled();
		if (!useProxy)
			return null;
		Proxy proxy = null;
		IProxyData data = getProxyData(host, proxyType);
		if (data == null)
			return null;
		String _type = getJSchProxyType(data);
		if (_type == null)
			return null;
		String _host = data.getHost();
		int _port = getPort(data);

		String proxyhost = _host + ":" + _port; //$NON-NLS-1$
		if (_type.equals(PROXY_TYPE_HTTP)) {
			proxy = new ProxyHTTP(proxyhost);

		}
		return proxy;
	}

	@Override
	public void connect(Proxy proxy, String host, int port, int timeout, IProgressMonitor monitor) throws AdbException {
		try {
			proxy.connect(new ResponsiveSocketFactory(monitor, timeout), host, port, timeout);
		} catch (AdbException e) {
			throw e;
		} catch (Exception e) {
			new AdbException(e.getMessage());
		}

	}

	@Override
	public IJSchLocation getLocation(String user, String host, int port) {
		IJSchLocation location = null;
		location = new JSchLocation(user, host, port);
		return location;
	}

	@Override
	public Adb getJSch() {
		return AdbActivator.getDefault().getJSch();
	}

}
