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
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;

public class H5_Group_Create_Test {

	//private static String fname = "H5GroupCreate.h5";
	private static String fname = "D:\\tmp\\H5FileCreate.h5";
	
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
	 * Title: HDF Object Package (Java) Example
	 * </p>
	 * <p>
	 * Description: this example shows how to create HDF5 groups using the
	 * "HDF Object Package (Java)". The example created the group structure:
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
	public void test() throws Exception {
		
		// retrieve an instance of H5File
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

        if (fileFormat == null) {
            System.err.println("Cannot find HDF5 FileFormat.");
        }

        Assert.assertNotNull(fileFormat);
        
        // create a new file with a given file name.
        H5File testFile = (H5File) fileFormat.createFile(fname, FileFormat.FILE_CREATE_DELETE);

        if (testFile == null) {
            System.err.println("Failed to create file:" + fname);
        }

        Assert.assertNotNull(testFile);
        
        // open the file and retrieve the root group
        testFile.open();
        Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) testFile.getRootNode()).getUserObject();

        // create groups
        Group g1 = testFile.createGroup("g1", root);
        Group g11 = testFile.createGroup("g11", g1);
        Group g12 = testFile.createGroup("g12", g1);
        Group g2 = testFile.createGroup("g2", root);
        Group g21 = testFile.createGroup("g21", g2);
        Group g22 = testFile.createGroup("g22", g2);

        // close file resource
        testFile.close();
        
        System.out.println("\n exit");
	}

}
