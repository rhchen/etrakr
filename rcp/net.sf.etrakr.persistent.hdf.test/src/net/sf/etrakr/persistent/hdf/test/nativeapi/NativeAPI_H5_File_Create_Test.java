package net.sf.etrakr.persistent.hdf.test.nativeapi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.HdfDLLLoader;

public class NativeAPI_H5_File_Create_Test {

	//private static String fname = "HDF5FileCreate.h5";
	private static String fname = "D:\\tmp\\HDF5FileCreate.h5";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		/* Load dll before test starts*/
		HdfDLLLoader.loadDLL();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * <p>
	 * Title: HDF Native Package (Java) Example
	 * </p>
	 * <p>
	 * Description: This example shows how to create an empty HDF5 file using the
	 * "HDF Native Package (Java)". If the file (H5FileCreate.h5) already exists, it
	 * will be truncated to zero length.
	 * </p>
	 */
	@Test
	public void test() {

		String str = "";
		Assert.assertNotNull(str);

		int file_id = -1;

		// Create a new file using default properties.
		try {
			file_id = H5.H5Fcreate(fname, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT,
					HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to create file:" + fname);
		}
		
		// Close the file.
		try {
			if (file_id >= 0)
				H5.H5Fclose(file_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertTrue(file_id >= 0);
		
		// End of example that creates an empty HDF5 file named H5FileCreate.h5.
		
		System.out.println("exit");
	}

}
