package net.sf.etrakr.persistent.hdf.test.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Rishi R. Sinha
 * 
 */
public class HObjectTest {
	
	private static String NAME_FILE_H5;
	
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HObjectTest.class);
    private static final H5File H5FILE = new H5File();
    private static final String GNAME = H5TestFile.NAME_GROUP;

    private H5File testFile = null;
    private HObject testObj = null;
    private long testOID;

    @BeforeClass
    public static void createFile() throws Exception {
        try {
            int openID = H5.getOpenIDCount();
            if (openID > 0)
                System.out.println("HObjectTest BeforeClass: Number of IDs still open: " + openID);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
        	H5File f5 = H5TestFile.createTestFile(null);
        	
        	NAME_FILE_H5 = f5.getAbsolutePath();
        }
        catch (final Exception ex) {
            System.out.println("*** Unable to create HDF5 test file. " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @AfterClass
    public static void checkIDs() throws Exception {
        try {
            int openID = H5.getOpenIDCount();
            if (openID > 0)
                System.out.println("HObjectTest AfterClass: Number of IDs still open: " + openID);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Before
    public void openFiles() throws Exception {
        try {
            int openID = H5.getOpenIDCount();
            if (openID > 0)
                log.debug("Before: Number of IDs still open: " + openID);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        //testFile = new H5File(H5TestFile.NAME_FILE_H5, FileFormat.WRITE);
        testFile = new H5File(NAME_FILE_H5, FileFormat.WRITE);
        assertNotNull(testFile);
        testObj = testFile.get(GNAME);
        assertNotNull(testObj);
        testOID = testObj.getOID()[0];
    }

    @After
    public void removeFiles() throws Exception {
        if (testFile != null) {
            try {
                testFile.close();
            }
            catch (final Exception ex) {
            }
            testFile = null;
        }
        try {
            int openID = H5.getOpenIDCount();
            if (openID > 0)
                log.debug("After: Number of IDs still open: " + openID);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getFile()}.
     * <p>
     * What to test:
     * <ul>
     * <li>Make sure file name in object yields same file as filename
     * </ul>
     */
    @Test
    public void testGetFile() {
        log.debug("testGetFile");
        String fullFileName = testObj.getFile();
        //if (!fullFileName.endsWith(H5TestFile.NAME_FILE_H5)) {
        if (!fullFileName.endsWith(NAME_FILE_H5)) {
            fail("Wrong File");
        }
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getName()}.
     * <p>
     * What to test:
     * <ul>
     * <li>For the base group, find the name of the group and test it against the standard.
     * </ul>
     */
    @Test
    public void testGetName() {
        log.debug("testGetName");
        if (!testObj.getName().equals(GNAME.substring(1))) {
            fail("GetName returns wrong name");
        }
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getFullName()}.
     * <p>
     * What to test:
     * <ul>
     * <li>For the base group, find the full name of the group and test it against the standard.
     * </ul>
     */
    @Test
    public void testGetFullName() {
        log.debug("testGetFullName");
        if (!testObj.getFullName().equals(GNAME)) {
            fail("GetFullName returns wrong name");
        }
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getPath()}. *
     * <p>
     * What to test:
     * <ul>
     * <li>For the base group, find the path of the group and test it against the standard.
     * </ul>
     */
    @Test
    public void testGetPath() {
        log.debug("testGetPath");
        if (!testObj.getPath().equals("/")) {
            fail("GetPath returns wrong path");
        }
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#setName(java.lang.String)} .
     * <p>
     * What to test:
     * <ul>
     * <li>Test setting the name to null. It should not be set.
     * <li>Test setting the name to another existing name in the same group.
     * <li>Test setting the name to a new name.
     * </ul>
     */
    @Test
    public void testSetName() {
        log.debug("testSetName");
        final String newName = "tmpName";

        // test set name to null
        try {
            testObj.setName(null);
        }
        catch (final Exception ex) {
            ; // Expected - intentional
        }

        // set to an existing name
        try {
            testObj.setName(H5TestFile.NAME_DATASET_FLOAT);
        }
        catch (final Exception ex) {
            ; // Expected - intentional
        }

        try {
            testObj.setName(newName);
        }
        catch (final Exception ex) {
            fail("setName() failed. " + ex);
        }

        // close the file and reopen it
        try {
            testFile.close();
            testFile.open();
            testObj = testFile.get(newName);
        }
        catch (final Exception ex) {
            fail("setName() failed. " + ex);
        }

        HObject tmpObj = null;
        // test the old name
        try {
            tmpObj = testFile.get(GNAME);
        }
        catch (final Exception ex) {
            fail("testFile.get(GNAME) failed. " + ex);
        }
        assertNull("The dataset should be null because it has been renamed", tmpObj);

        // set back the original name
        try {
            testObj.setName(GNAME);
        }
        catch (final Exception ex) {
            fail("setName() failed. " + ex);
        }

        // make sure the dataset is OK
        try {
            testObj = testFile.get(GNAME);
        }
        catch (final Exception ex) {
            fail("setName() failed. " + ex);
        }
        assertNotNull(testObj);
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#setPath(java.lang.String)} .
     * <p>
     * What to test:
     * <ul>
     * <li>Test setting the path to null. It should not be set.
     * <li>Test setting the path to another existing name in the same group.
     * <li>Test setting the path to a new name.
     * </ul>
     */
    @Test
    public void testSetPath() {
        log.debug("testSetPath");
        String path = testObj.getPath();
        try {
            testObj.setPath(null);
        }
        catch (Exception e) {
            ;
        }

        if (!path.equals(testObj.getPath())) {
            fail("testPath changed the path name even though null was passed to it.");
        }

        try {
            testObj.setPath("testPath");
        }
        catch (Exception e) {
            fail("testPath failed when trying to set it to testPath");
        }
        if (!testObj.getPath().equals("testPath")) {
            fail("testPath failed when trying to set it to testPath");
        }
        try {
            testObj.setPath(path);
        }
        catch (Exception e) {
            fail("testPath failed when trying to reset the path to " + path);
        }
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#open()}.
     * <p>
     * What to test:
     * <ul>
     * <li>Open the Group and check that the gid returned is less than 1.
     * </ul>
     */
    @Test
    public void testOpen() {
        log.debug("testOpen");
        int gid = -1;

        for (int loop = 0; loop < 15; loop++) {
            gid = -1;
            try {
                gid = testObj.open();
            }
            catch (final Exception ex) {
                fail("open() failed. " + ex);
            }
            assertTrue(gid > 0);
            testObj.close(gid);
        }
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#close(int)}.
     * <p>
     * What to test:
     * <ul>
     * <li>Run the tests for opening the group.
     * </ul>
     */
    @Test
    public void testClose() {
        log.debug("testClose");
        testOpen();
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getFID()}.
     * <p>
     * What to test:
     * <ul>
     * <li>get the FID for the group and make sure that it is the same as the FID for the file.
     * </ul>
     */
    @Test
    public void testGetFID() {
        log.debug("testGetFID");
        assertEquals(testObj.getFID(), testFile.getFID());
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#equalsOID(long[])}.
     * <p>
     * What to test:
     * <ul>
     * <li>Check against null. It should fail.
     * <li>Check against the OID that we have already extraced.
     * </ul>
     */
    @Test
    public void testEqualsOID() {
        log.debug("testEqualsOID");
        assertNotNull(testObj);
        assertTrue(testObj.equalsOID(new long[] { testOID }));
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getFileFormat()}.
     * <p>
     * What to test:
     * <ul>
     * <li>For the group, check against null.
     * <li>For the group, check against the testFile.
     * </ul>
     */
    @Test
    public void testGetFileFormat() {
        log.debug("testGetFileFormat");
        assertNotNull(testObj.getFileFormat());
        assertEquals(testObj.getFileFormat(), testFile);
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#getOID()}.
     * <p>
     * What to test:
     * <ul>
     * <li>Check that OIDlist is not null.
     * <li>Check that OID[0] is correct.
     * </ul>
     */
    @Test
    public void testGetOID() {
        log.debug("testGetOID");
        assertNotNull(testObj.getOID());
        assertEquals(testObj.getOID()[0], testOID);
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#hasAttribute()}.
     * <p>
     * What to test:
     * <ul>
     * <li>Check for Image dataset which has an attribute.
     * <li>Check for base group which has no attributes.
     * </ul>
     */
    @Test
    public void testHasAttribute() {
        log.debug("testHasAttribute");
        try {
            assertTrue(testFile.get(H5TestFile.NAME_DATASET_IMAGE).hasAttribute());
        }
        catch (Exception e) {
            fail("get() fails.");
        }
        assertFalse(testObj.hasAttribute());
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

    /**
     * Test method for {@link ncsa.hdf.object.HObject#toString()}.
     * <p>
     * What to test:
     * <ul>
     * <li>Check for the group.
     * </ul>
     */
    @Test
    public void testToString() {
        log.debug("testToString");
        assertEquals(testObj.toString(), GNAME.substring(1));
        int nObjs = 0;
        try {
            nObjs = H5.H5Fget_obj_count(testFile.getFID(), HDF5Constants.H5F_OBJ_ALL);
        }
        catch (final Exception ex) {
            fail("H5.H5Fget_obj_count() failed. " + ex);
        }
        assertEquals(1, nObjs); // file id should be the only one left open
    }

}
