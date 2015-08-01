package net.sf.etrakr.remote.adb.core.adb;

public interface IUserInfo {

	String getPassphrase();

	String getPassword();

	boolean promptPassword(String message);

	boolean promptPassphrase(String message);

	boolean promptYesNo(String message);

	void showMessage(String message);
}
