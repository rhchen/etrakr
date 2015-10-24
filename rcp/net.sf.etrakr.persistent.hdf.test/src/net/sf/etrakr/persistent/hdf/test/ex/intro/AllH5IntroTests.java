/**
 * 
 */
package net.sf.etrakr.persistent.hdf.test.ex.intro;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ncsa.hdf.hdf5lib.HdfDLLLoader;


@RunWith(Suite.class)
@Suite.SuiteClasses( {
	
// ncsa.hdf.object.h5 package
	H5_CreateAttribute.class,

// ncsa.hdf.object package
	H5Object_CreateAttribute.class
})

public class AllH5IntroTests {

	@BeforeClass
    public static void init() {
    	System.out.println("Before all");
    	
    	/* Load dll before test starts*/
		HdfDLLLoader.loadDLL();
    }
	
    
}
