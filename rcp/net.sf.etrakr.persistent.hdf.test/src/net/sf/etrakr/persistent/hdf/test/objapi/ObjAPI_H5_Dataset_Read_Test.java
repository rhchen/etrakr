package net.sf.etrakr.persistent.hdf.test.objapi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ncsa.hdf.hdf5lib.HdfDLLLoader;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;

public class ObjAPI_H5_Dataset_Read_Test {

	//private static String fname  = "H5DatasetRead.h5";
	private static String fname = "C:\\tmp\\H5DatasetRead.h5";
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
	 * Description: this example shows how to read/write HDF datasets using the
	 * "HDF Object Package (Java)". The example creates an integer dataset, and read
	 * and write data values:
	 * 
	 * <pre>
	 *     "/" (root)
	 *             2D 32-bit integer 20x10
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @author Peter X. Cao
	 * @version 2.4
	 * @throws Exception 
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

        // retrieve the dataset "2D 32-bit integer 20x10"
        Dataset dataset = (Dataset) root.getMemberList().get(0);
        int[] dataRead = (int[]) dataset.read();

        // print out the data values
        System.out.println("\n\nOriginal Data Values");
        for (int i = 0; i < 20; i++) {
            System.out.print("\n" + dataRead[i * 10]);
            for (int j = 1; j < 10; j++) {
                System.out.print(", " + dataRead[i * 10 + j]);
            }
        }

        // change data value and write it to file.
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                dataRead[i * 10 + j]++;
            }
        }
        dataset.write(dataRead);

        // clean and reload the data value
        int[] dataModified = (int[]) dataset.read();

        // print out the modified data values
        System.out.println("\n\nModified Data Values");
        for (int i = 0; i < 20; i++) {
            System.out.print("\n" + dataModified[i * 10]);
            for (int j = 1; j < 10; j++) {
                System.out.print(", " + dataModified[i * 10 + j]);
            }
        }

        // close file resource
        testFile.close();
        
        System.out.println("\n\n exit");
	}

	 /**
     * create the file and add groups and dataset into the file, which is the
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
                dataIn[i * 10 + j] = 1000 + i * 100 + j;
            }
        }

        // create 2D 32-bit (4 bytes) integer dataset of 20 by 10
        Datatype dtype = testFile.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
        Dataset dataset = testFile
                .createScalarDS("2D 32-bit integer 20x10", root, dtype, dims2D, null, null, 0, dataIn);

        // close file resource
        testFile.close();
        
        
    }
}
