/**
 * 
 */
package net.sf.etrakr.persistent.hdf.test.testsuite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ncsa.hdf.hdf5lib.HdfDLLLoader;


@RunWith(Suite.class)
@Suite.SuiteClasses( {
	
// ncsa.hdf.object.h5 package
    H5CompoundDSTest.class, 
    H5BugFixTest.class, 
    H5ScalarDSTest.class, 
    H5GroupTest.class, 
    H5DatatypeTest.class, 
    H5FileTest.class,

// ncsa.hdf.object package
    CompoundDSTest.class, 
    DatasetTest.class, 
    ScalarDSTest.class, 
    AttributeTest.class, 
    DatatypeTest.class, 
    FileFormatTest.class, 
    GroupTest.class, 
    HObjectTest.class
})

public class AllH5ObjectTests {

	@BeforeClass
    public static void init() {
    	System.out.println("Before all");
    	
    	/* Load dll before test starts*/
		HdfDLLLoader.loadDLL();
    }
	
    
}
