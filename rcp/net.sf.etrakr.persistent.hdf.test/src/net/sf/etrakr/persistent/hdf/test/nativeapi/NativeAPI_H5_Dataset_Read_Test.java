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
import net.sf.etrakr.persistent.hdf.test.ori.nativeapi.HDF5DatasetCreate;

public class NativeAPI_H5_Dataset_Read_Test {

	//private static String fname  = "HDF5DatasetRead.h5";
	private static String fname = "D:\\tmp\\HDF5DatasetRead.h5";
    private static String dsname  = "2D 32-bit integer 20x10";
    private static long[] dims2D = { 20, 10 };
    
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
	 * Description: this example shows how to read/write HDF datasets using the
	 * "HDF Native Package (Java)". The example creates an integer dataset, and read
	 * and write data values:
	 * 
	 * <pre>
	 *     "/" (root)
	 *             2D 32-bit integer 20x10
	 * </pre>
	 * 
	 * </p>
	 * @throws Exception 
	 */
	@Test
	public void test() throws Exception {
		
		int file_id = -1;
        int dataset_id = -1;

        // create the file and add groups and dataset into the file
        createFile();

        // Open file using the default properties.
        try {
            file_id = H5.H5Fopen(fname, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Open dataset using the default properties.
        try {
            if (file_id >= 0)
                dataset_id = H5.H5Dopen(file_id, dsname, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(file_id >= 0);
        
        // Allocate array of pointers to two-dimensional arrays (the
        // elements of the dataset.
        int[][] dataRead = new int[(int) dims2D[0]][(int) (dims2D[1])];

        try {
            if (dataset_id >= 0)
                H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_INT,
                        HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                        HDF5Constants.H5P_DEFAULT, dataRead);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataset_id >= 0);
        
        // print out the data values
        System.out.println("\n\nOriginal Data Values");
        for (int i = 0; i < 20; i++) {
            System.out.print("\n" + dataRead[i][0]);
            for (int j = 1; j < 10; j++) {
                System.out.print(", " + dataRead[i][j]);
            }
        }

        // change data value and write it to file.
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                dataRead[i][j]++;
            }
        }

        // Write the data to the dataset.
        try {
            if (dataset_id >= 0)
                H5.H5Dwrite(dataset_id, HDF5Constants.H5T_NATIVE_INT,
                        HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, 
                        dataRead);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataset_id >= 0);
        
        // reload the data value
        int[][] dataModified = new int[(int) dims2D[0]][(int) (dims2D[1])];

        try {
            if (dataset_id >= 0)
                H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_INT,
                        HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                        HDF5Constants.H5P_DEFAULT, dataModified);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataset_id >= 0);
        
        // print out the modified data values
        System.out.println("\n\nModified Data Values");
        for (int i = 0; i < 20; i++) {
            System.out.print("\n" + dataModified[i][0]);
            for (int j = 1; j < 10; j++) {
                System.out.print(", " + dataModified[i][j]);
            }
        }

        // Close the dataset.
        try {
            if (dataset_id >= 0)
                H5.H5Dclose(dataset_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataset_id >= 0);
        
        // Close the file.
        try {
            if (file_id >= 0)
                H5.H5Fclose(file_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        Assert.assertTrue(file_id >= 0);
        
        System.out.println("\n exit");
        
	}

	/**
     * create the file and add groups ans dataset into the file, which is the
     * same as javaExample.H5DatasetCreate
     * 
     * @see HDF5DatasetCreate.H5DatasetCreate
     * @throws Exception
     */
    private static void createFile() throws Exception {
        int file_id = -1;
        int dataspace_id = -1;
        int dataset_id = -1;
        
        // Create a new file using default properties.
        try {
            file_id = H5.H5Fcreate(fname, HDF5Constants.H5F_ACC_TRUNC,
                    HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the data space for the dataset.
        try {
            dataspace_id = H5.H5Screate_simple(2, dims2D, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the dataset.
        try {
            if ((file_id >= 0) && (dataspace_id >= 0))
                dataset_id = H5.H5Dcreate(file_id, dsname,
                        HDF5Constants.H5T_STD_I32LE, dataspace_id,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Terminate access to the data space.
        try {
            if (dataspace_id >= 0)
                H5.H5Sclose(dataspace_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // set the data values
        int[] dataIn = new int[20 * 10];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                dataIn[i * 10 + j] = i * 100 + j;
            }
        }

        // Write the data to the dataset.
        try {
            if (dataset_id >= 0)
                H5.H5Dwrite(dataset_id, HDF5Constants.H5T_NATIVE_INT,
                        HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                        HDF5Constants.H5P_DEFAULT, dataIn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // End access to the dataset and release resources used by it.
        try {
            if (dataset_id >= 0)
                H5.H5Dclose(dataset_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Close the file.
        try {
            if (file_id >= 0)
                H5.H5Fclose(file_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
