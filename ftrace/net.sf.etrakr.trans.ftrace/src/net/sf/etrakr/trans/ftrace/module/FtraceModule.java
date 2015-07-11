
/**
 * 
 */
package net.sf.etrakr.trans.ftrace.module;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author rhchen
 *
 */
public class FtraceModule extends SimpleModule {

	private static final String MODULE_NAME = "Ftrace Module";
	private static final Version v = new Version(1, 0, 0, "SNAPSHOT", "etrakr", "net.sf.etrakr.ftrace");
	
	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.Module#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return MODULE_NAME;
	}

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.Module#setupModule(com.fasterxml.jackson.databind.Module.SetupContext)
	 */
	@Override
	public void setupModule(SetupContext setupContext) {
		super.setupModule(setupContext);
	}

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.Module#version()
	 */
	@Override
	public Version version() {
		return v;
	}

}
