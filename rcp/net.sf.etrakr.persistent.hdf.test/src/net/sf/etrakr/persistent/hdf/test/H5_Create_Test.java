package net.sf.etrakr.persistent.hdf.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ncsa.hdf.hdf5lib.HdfDLLLoader;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import net.sf.etrakr.persistent.hdf.HdfActivator;

public class H5_Create_Test {

	private static String fname = "D:\\tmp\\H5FileCreate.h5";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		
		/* Load dll before test starts*/
		HdfDLLLoader.loadDLL();
		// Retrieve an instance of the implementing class for the HDF5 format
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

        // If the implementing class wasn't found, it's an error.
        if (fileFormat == null) {
            System.err.println("Cannot find HDF5 FileFormat.");
            return;
        }

        Assert.assertNotNull(fileFormat);
        
        // If the implementing class was found, use it to create a new HDF5 file
        // with a specific file name.
        //
        // If the specified file already exists, it is truncated.
        // The default HDF5 file creation and access properties are used.
        //
        H5File testFile = (H5File) fileFormat.createFile(fname, FileFormat.FILE_CREATE_DELETE);

        if (testFile == null) {
            System.err.println("Failed to create file: " + fname);
            return;
        }
        
        Assert.assertNotNull(testFile);
        
        System.out.println("exit");
        
        // End of example that creates an empty HDF5 file named H5FileCreate.h5.
	}

}
