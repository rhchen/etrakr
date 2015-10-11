package net.sf.etrakr.persistent.hdf.test.objapi;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ncsa.hdf.hdf5lib.HdfDLLLoader;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;

public class ObjAPI_H5_Attribute_Create_Test {

	//private static String fname  = "H5AttributeCreate.h5";
	private static String fname = "D:\\tmp\\H5AttributeCreate.h5";
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
	 * Title: HDF Object Package (Java) Example
	 * </p>
	 * <p>
	 * Description: this example shows how to create/read/write HDF attribute using
	 * the "HDF Object Package (Java)". The example creates an attribute and, read
	 * and write the attribute value:
	 * 
	 * <pre>
	 *     "/" (root)
	 *             2D 32-bit integer 20x10
	 *             (attribute: name="data range", value=[0, 10000])
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @author Peter X. Cao
	 * @version 2.4
	 */
	@Test
	public void test() throws Exception {
		
		// create the file and add groups and dataset into the file
        createFile();

        // retrieve an instance of H5File
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

        if (fileFormat == null) {
            System.err.println("Cannot find HDF5 FileFormat.");
        }

        Assert.assertNotNull(fileFormat);
        
        // open the file with read and write access
        FileFormat testFile = fileFormat.createInstance(fname, FileFormat.WRITE);

        if (testFile == null) {
            System.err.println("Failed to open file: " + fname);
        }

        Assert.assertNotNull(testFile);
        
        // open the file and retrieve the file structure
        testFile.open();
        Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) testFile.getRootNode()).getUserObject();

        // retrieve athe dataset "2D 32-bit integer 20x10"
        Dataset dataset = (Dataset) root.getMemberList().get(0);

        // create 2D 32-bit (4 bytes) integer dataset of 20 by 10
        Datatype dtype = testFile.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
        long[] attrDims = { 2 }; // 1D of size two
        int[] attrValue = { 0, 10000 }; // attribute value

        // create a attribute of 1D integer of size two
        Attribute attr = new Attribute("data range", dtype, attrDims);
        attr.setValue(attrValue); // set the attribute value

        // attach the attribute to the dataset
        dataset.writeMetadata(attr);

        // read the attribute into memory
        List attrList = dataset.getMetadata();
        attr = (Attribute) attrList.get(0);

        // print out attribute value
        System.out.println(attr.toString());
        System.out.println(attr.toString("  "));

        // close file resource
        testFile.close();
        
        System.out.println("exit");
	}

	/**
     * create the file and add groups ans dataset into the file, which is the
     * same as javaExample.H5DatasetCreate
     * 
     * @see javaExample.HDF5DatasetCreate
     * @throws Exception
     */
    private static void createFile() throws Exception {
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

        // set the data values
        int[] dataIn = new int[20 * 10];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                dataIn[i * 10 + j] = i * 100 + j;
            }
        }

        // create 2D 32-bit (4 bytes) integer dataset of 20 by 10
        Datatype dtype = testFile.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
        Dataset dataset = testFile.createScalarDS(dsname, root, dtype, dims2D, null, null, 0, dataIn);

        // close file resource
        testFile.close();
    }
}
