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

public class NativeAPI_H5_Attribute_Create_Test {

	//private static String fname  = "HDF5AttributeCreate.h5";
	private static String fname = "D:\\tmp\\HDF5AttributeCreate.h5";
    private static String dsname  = "2D 32-bit integer 20x10";
    private static String attrname  = "data range";
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
	 * Description: this example shows how to create/read/write HDF attribute using
	 * the "HDF Native Package (Java)". The example creates an attribute and, read
	 * and write the attribute value:
	 * 
	 * <pre>
	 *     "/" (root)
	 *             2D 32-bit integer 20x10
	 *             (attribute: name="data range", value=[0, 10000])
	 * </pre>
	 * 
	 * </p>
	 * @throws Exception 
	 */
	@Test
	public void test() throws Exception {
		
		int file_id = -1;
        int dataspace_id = -1;
        int dataset_id = -1;
        int attribute_id = -1;

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

        long[] attrDims = { 2 }; // 1D of size two
        int[] attrValue = { 0, 10000 }; // attribute value

        // Create the data space for the attribute.
        try {
            dataspace_id = H5.H5Screate_simple(1, attrDims, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create a dataset attribute.
        try {
            if ((dataset_id >= 0) && (dataspace_id >= 0))
                attribute_id = H5.H5Acreate(dataset_id, attrname,
                        HDF5Constants.H5T_STD_I32BE, dataspace_id,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        Assert.assertTrue(dataset_id >= 0);
        Assert.assertTrue(dataspace_id >= 0);
        
        // Write the attribute data.
        try {
            if (attribute_id >= 0)
                H5.H5Awrite(attribute_id, HDF5Constants.H5T_NATIVE_INT, attrValue);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(attribute_id >= 0);
        
        // Close the attribute.
        try {
            if (attribute_id >= 0)
                H5.H5Aclose(attribute_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Close the dataspace.
        try {
            if (dataspace_id >= 0)
                H5.H5Sclose(dataspace_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataspace_id >= 0);
        
        try {
            if (dataset_id >= 0)
                attribute_id = H5.H5Aopen_by_name(dataset_id, ".", attrname, 
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataset_id >= 0);
        
        // Get dataspace and allocate memory for read buffer.
        try {
            if (attribute_id >= 0)
                dataspace_id = H5.H5Aget_space(attribute_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(attribute_id >= 0);
        
        try {
            if (dataspace_id >= 0)
                H5.H5Sget_simple_extent_dims(dataspace_id, attrDims, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataspace_id >= 0);
        
        // Allocate array of pointers to two-dimensional arrays (the
        // elements of the dataset.
        int[] attrData = new int[(int) attrDims[0]];

        // Read data.
        try {
            if (attribute_id >= 0)
                H5.H5Aread(attribute_id, HDF5Constants.H5T_NATIVE_INT, attrData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(attribute_id >= 0);
        
        // print out attribute value
        System.out.println(attrname);
        System.out.println(attrData[0] + "  " + attrData[1]);

        // Close the dataspace.
        try {
            if (dataspace_id >= 0)
                H5.H5Sclose(dataspace_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataspace_id >= 0);
        
        // Close to the dataset.
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
        
        System.out.println("exit");
	}
	
	/**
     * create the file and add groups and dataset into the file, which is the
     * same as javaExample.H5DatasetCreate
     * 
     * @see javaExample.HDF5DatasetCreate
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

        Assert.assertTrue(file_id >= 0);
        Assert.assertTrue(dataspace_id >= 0);
        
        // Terminate access to the data space.
        try {
            if (dataspace_id >= 0)
                H5.H5Sclose(dataspace_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(dataspace_id >= 0);
        
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

        Assert.assertTrue(dataset_id >= 0);
        
        // End access to the dataset and release resources used by it.
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
    }

}
