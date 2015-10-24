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

public class NativeAPI_H5_Group_Create_Test {

	//private static String fname = "HDF5GroupCreate.h5";
	private static String fname = "C:\\tmp\\HDF5GroupCreate.h5";
	
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
	 * Description: this example shows how to create HDF5 groups using the
	 * "HDF Native Package (Java)". The example created the group structure:
	 * 
	 * <pre>
	 *     "/" (root)
	 *         g1
	 *             g11
	 *             g12
	 *         g2
	 *             g21
	 *             g22
	 * </pre>
	 * 
	 * </p>
	 */
	@Test
	public void test() {
		
		int file_id = -1;
        int subgroup_id = -1;
        int group_id1 = -1;
        int group_id2 = -1;

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

        Assert.assertTrue(file_id >= 0);
        
        // Create a group in the file.
        try {
            if (file_id >= 0) {
                group_id1 = H5.H5Gcreate(file_id, "g1",
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                if (group_id1 >= 0) {
                    subgroup_id = H5.H5Gcreate(group_id1, "g11",
                            HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    try {
                        if (subgroup_id >= 0)
                            H5.H5Gclose(subgroup_id);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    subgroup_id = H5.H5Gcreate(group_id1, "g12",
                            HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    try {
                        if (subgroup_id >= 0)
                            H5.H5Gclose(subgroup_id);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                group_id2 = H5.H5Gcreate(file_id, "g2",
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                if (group_id2 >= 0) {
                    subgroup_id = H5.H5Gcreate(group_id2, "g21",
                            HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    try {
                        if (subgroup_id >= 0)
                            H5.H5Gclose(subgroup_id);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    subgroup_id = H5.H5Gcreate(group_id2, "g22",
                            HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    try {
                        if (subgroup_id >= 0)
                            H5.H5Gclose(subgroup_id);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(group_id2 >= 0);
        
        // Close the groups.
        try {
            if (group_id2 >= 0)
                H5.H5Gclose(group_id2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        Assert.assertTrue(group_id1 >= 0);
        
        try {
            if (group_id1 >= 0)
                H5.H5Gclose(group_id1);
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
        
        System.out.println("exit");
	}

}
