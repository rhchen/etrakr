package net.sf.etrakr.tmf.remote.adb.core;

import java.net.URI;

import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.tracecompass.tmf.remote.core.proxy.IConnectionFactory;
import org.eclipse.tracecompass.tmf.remote.core.proxy.TmfRemoteConnectionFactory;

import net.sf.etrakr.remote.adb.core.AdbPlugin;

public class TmfAdbConnectionFactory extends TmfRemoteConnectionFactory.DefaultConnectionFactory implements IConnectionFactory {

	@Override
	public IRemoteConnection createConnection(URI hostUri, String hostName) throws RemoteConnectionException {
		
		try {
			
			return createConnection(hostUri, hostName, 5);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RemoteConnectionException(e);
		}
	}

	public IRemoteConnection createConnection(URI hostUri, String hostName, int timeout) throws RemoteConnectionException, InterruptedException {
		
		boolean f = false;
		
		for(int i=0; i<=timeout; i++){
			
			boolean d = AdbPlugin.getDefault().hasAvailableDevice();
			
			if(d){
				
				f = true;
				break;
				
			}else{
				
				Thread.sleep(1000);
			} 
		}

		if(!f) throw new RemoteConnectionException("No Available ADB Devices");
		
		return super.createConnection(hostUri, hostName);
	}
}
