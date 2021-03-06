/**
 * 
 */
package net.sf.etrakr.persistent.hdf.test.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.DataFormat;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rsinha
 * 
 */
public class DataFormatTest {
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFormatTest.class);
    private static final H5File H5FILE = new H5File();

    private H5File testFile = null;
    private DataFormat testGroup = null;

    private static String NAME_FILE_H5;
    
    @BeforeClass
    public static void createFile() throws Exception {
        try {
            int openID = H5.getOpenIDCount();
            if (openID > 0)
                System.out.println("DataFormatTest BeforeClass: Number of IDs still open: " + openID);
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
                System.out.println("DataFormatTest AfterClass: Number of IDs still open: " + openID);
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
        //testFile = (H5File) H5FILE.open(H5TestFile.NAME_FILE_H5, FileFormat.WRITE);
        testFile = (H5File) H5FILE.open(NAME_FILE_H5, FileFormat.WRITE);
        assertNotNull(testFile);
        testGroup = testFile.get(H5TestFile.NAME_GROUP_ATTR);
        assertNotNull(testGroup);
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
     * Test method for {@link ncsa.hdf.object.DataFormat#getFile()}.
     * <ul>
     * <li>Test if the file name is correct
     * </ul>
     */
    @Test
    public void testGetFile() {
        log.debug("testGetFile");
        //if (!testGroup.getFile().equals(H5TestFile.NAME_FILE_H5)) {
        if (!testGroup.getFile().equals(NAME_FILE_H5)) {
            fail("getFile() fails.");
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
     * Test method for {@link ncsa.hdf.object.DataFormat#getMetadata()}.
     * <ul>
     * <li>Reading the attributes
     * <li>Checking the values of attributes
     * </ul>
     */
    /* Ignore the test due to tycho surefire test fail */
    @Ignore
    public void testGetMetadata() {
        log.debug("testGetMetadata");
        Attribute strAttr = null;
        Attribute arrayIntAttr = null;
        List mdataList = null;
        try {
            mdataList = testGroup.getMetadata();
        }
        catch (final Exception ex) {
            fail("getMetadata() failed. " + ex);
        }
        strAttr = (Attribute) mdataList.get(0);
        arrayIntAttr = (Attribute) mdataList.get(1);
        String[] value = (String[]) strAttr.getValue();
        if (!value[0].equals("String attribute.")) {
            fail("getMdata() failed.");
        }

        int[] intValue = (int[]) arrayIntAttr.getValue();
        long[] dims = arrayIntAttr.getDataDims();

        for (int i = 0; i < dims[0]; i++) {
            if (intValue[i] != i + 1) {
                fail("getValue() failed");
            }
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
     * Test method for {@link ncsa.hdf.object.DataFormat#writeMetadata(java.lang.Object)}.
     * <ul>
     * <li>Writing new attributes
     * <li>Checking that the new attributes are written in file
     * </ul>
     */
    /* Ignore the test due to tycho surefire test fail */
    @Ignore
    public void testWriteMetadata() {
        log.debug("testWriteMetadata");
        long[] attrDims = { 1 };
        String attrName = "CLASS";
        String[] classValue = { "IMAGE" };
        Datatype attrType = new H5Datatype(Datatype.CLASS_STRING, classValue[0].length() + 1, -1, -1);
        Attribute attr = new Attribute(attrName, attrType, attrDims);
        assertNotNull(testGroup);
        assertNotNull(attr);
        attr.setValue(classValue);
        try {
            testGroup.writeMetadata(attr);
        }
        catch (Exception ex) {
            fail("writeMetadata() failed " + ex.getMessage());
        }

        List mdataList = null;
        try {
            mdataList = testGroup.getMetadata();
        }
        catch (final Exception ex) {
            fail("getMetadata() failed. " + ex);
        }

        assertEquals(3, mdataList.size());

        Attribute strAttr = null;
        Attribute arrayIntAttr = null;

        strAttr = (Attribute) mdataList.get(0);
        arrayIntAttr = (Attribute) mdataList.get(1);
        String[] value = (String[]) strAttr.getValue();

        if (!value[0].equals("String attribute.")) {
            fail("writeMdata() failed.");
        }

        int[] intValue = (int[]) arrayIntAttr.getValue();
        long[] dims = arrayIntAttr.getDataDims();

        for (int i = 0; i < dims[0]; i++) {
            if (intValue[i] != i + 1) {
                fail("writeValue() failed");
            }
        }
        strAttr = (Attribute) mdataList.get(2);
        value = (String[]) strAttr.getValue();
        if (!value[0].equals("IMAGE")) {
            fail("writeMetadata() failed.");
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
     * Test method for {@link ncsa.hdf.object.DataFormat#removeMetadata(java.lang.Object)}.
     * <ul>
     * <li>Remove an attribute
     * </ul>
     */
    /* Ignore the test due to tycho surefire test fail */
    @Ignore
    public void testRemoveMetadata() {
        log.debug("testRemoveMetadata");
        List mdataList = null;
        try {
            mdataList = testGroup.getMetadata();
        }
        catch (final Exception ex) {
            fail("getMetadata() failed. " + ex.getMessage());
        }

        Attribute strAttr = (Attribute) mdataList.get(2);
        try {
            testGroup.removeMetadata(strAttr);
        }
        catch (Exception e) {
            fail("removeMetadata() failed " + e.getMessage());
        }
        assertEquals(2, mdataList.size());
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
