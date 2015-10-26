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

public class NativeAPI_H5_Dataset_Create_Test {

	//private static String fname  = "HDF5DatasetCreate.h5";
	private static String fname = "C:\\tmp\\HDF5DatasetCreate.h5";
    private static long[] dims2D = { 20, 10 };
    private static long[] dims3D = { 20, 10, 5 };
    
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
	 * Description: this example shows how to create HDF5 datasets using the
	 * "HDF Native Package (Java)". The example created the group structure and
	 * datasets:
	 * 
	 * <pre>
	 *     "/" (root)
	 *         integer arrays
	 *             2D 32-bit integer 20x10
	 *             3D 16-bit integer 20x10x5
	 *         float arrays
	 *             2D 64-bit double 20x10
	 *             3D 32-bit float  20x10x5
	 * </pre>
	 * 
	 * </p>
	 */
	@Test
	public void test() {
		
		int file_id = -1;
        int group_id1 = -1;
        int group_id2 = -1;
        int dataspace_id1 = -1;
        int dataspace_id2 = -1;
        int dataset_id = -1;

        // Create a new file using default properties.
        try {
            file_id = H5.H5Fcreate(fname, HDF5Constants.H5F_ACC_TRUNC,
                    HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create file:" + fname);
            return;
        }

        // Create a group in the file.
        try {
            if (file_id >= 0) {
                group_id1 = H5.H5Gcreate(file_id, "g1",
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                group_id2 = H5.H5Gcreate(file_id, "g2",
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(file_id >= 0);
        
        // Create the data space for the  2D dataset.
        try {
            dataspace_id1 = H5.H5Screate_simple(2, dims2D, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the data space for the  3D dataset.
        try {
            dataspace_id2 = H5.H5Screate_simple(3, dims3D, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // create 2D 32-bit (4 bytes) integer dataset of 20 by 10
        try {
            if ((group_id1 >= 0) && (dataspace_id1 >= 0)) {
                dataset_id = H5.H5Dcreate(group_id1, "2D 32-bit integer 20x10",
                        HDF5Constants.H5T_NATIVE_INT32, dataspace_id1,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                if (dataset_id >= 0)
                    H5.H5Dclose(dataset_id);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(group_id1 >= 0);
        Assert.assertTrue(dataspace_id1 >= 0);
        
        // create 3D 8-bit (1 byte) unsigned integer dataset of 20 by 10 by 5
        try {
            if ((group_id1 >= 0) && (dataspace_id2 >= 0)) {
                dataset_id = H5.H5Dcreate(group_id1, "3D 8-bit unsigned integer 20x10x5",
                        HDF5Constants.H5T_NATIVE_INT8, dataspace_id2,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                if (dataset_id >= 0)
                    H5.H5Dclose(dataset_id);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(group_id1 >= 0);
        Assert.assertTrue(dataspace_id2 >= 0);
        
        // create 2D 64-bit (8 bytes) double dataset of 20 by 10
        try {
            if ((group_id2 >= 0) && (dataspace_id1 >= 0)) {
                dataset_id = H5.H5Dcreate(group_id2, "2D 64-bit double 20x10",
                        HDF5Constants.H5T_NATIVE_DOUBLE, dataspace_id1,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                if (dataset_id >= 0)
                    H5.H5Dclose(dataset_id);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(group_id2 >= 0);
        Assert.assertTrue(dataspace_id1 >= 0);
        
        // create 3D 32-bit (4 bytes) float dataset of 20 by 10 by 5
        try {
            if ((group_id2 >= 0) && (dataspace_id2 >= 0)) {
                dataset_id = H5.H5Dcreate(group_id2, "3D 32-bit float  20x10x5",
                        HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id2,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                if (dataset_id >= 0)
                    H5.H5Dclose(dataset_id);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(group_id2 >= 0);
        Assert.assertTrue(dataspace_id2 >= 0);
        
        // Terminate access to the data space.
        try {
            if (dataspace_id2 >= 0)
                H5.H5Sclose(dataspace_id2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        Assert.assertTrue(dataspace_id2 >= 0);
        
        try {
            if (dataspace_id1 >= 0)
                H5.H5Sclose(dataspace_id1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataspace_id1 >= 0);
        
        // Close the groups.
        try {
            if (group_id2 >= 0)
                H5.H5Gclose(group_id2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        Assert.assertTrue(group_id2 >= 0);
        
        try {
            if (group_id1 >= 0)
                H5.H5Gclose(group_id1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(group_id1 >= 0);
        
        // Close the file.
        try {
            if (file_id >= 0)
                H5.H5Fclose(file_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        Assert.assertTrue(file_id >= 0);
        
        System.out.println("exit");
	}

}