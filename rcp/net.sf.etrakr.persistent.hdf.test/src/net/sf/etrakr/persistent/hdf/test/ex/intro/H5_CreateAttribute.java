//
//   Creating a dataset attribute.

package net.sf.etrakr.persistent.hdf.test.ex.intro;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class H5_CreateAttribute {
	//private static String FILENAME = "H5_CreateAttribute.h5";
	private static String FILENAME = "d:\\tmp\\H5_CreateAttribute.h5";
	private static String DATASETNAME = "dset";
    private static final int DIM_X = 4;
    private static final int DIM_Y = 6;
	private static String DATASETATTRIBUTE = "Units";

	private static void CreateDatasetAttribute() {
		int file_id = -1;
		int dataspace_id = -1;
		int dataset_id = -1;
		int attribute_id = -1;
        long[] dims1 = { DIM_X, DIM_Y };
		long[] dims = { 2 };
		int[] attr_data = { 100, 200 };

        // Create a new file using default properties.
        try {
            file_id = H5.H5Fcreate(FILENAME, HDF5Constants.H5F_ACC_TRUNC,
                    HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the data space for the dataset.
        try {
            dataspace_id = H5.H5Screate_simple(2, dims1, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the dataset.
        try {
            if ((file_id >= 0) && (dataspace_id >= 0))
                dataset_id = H5.H5Dcreate(file_id, "/" + DATASETNAME,
                        HDF5Constants.H5T_STD_I32BE, dataspace_id,
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

		// Create the data space for the attribute.
		try {
			dataspace_id = H5.H5Screate_simple(1, dims, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create a dataset attribute.
		try {
			if ((dataset_id >= 0) && (dataspace_id >= 0))
				attribute_id = H5.H5Acreate(dataset_id, DATASETATTRIBUTE,
						HDF5Constants.H5T_STD_I32BE, dataspace_id,
						HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Write the attribute data.
		try {
			if (attribute_id >= 0)
				H5.H5Awrite(attribute_id, HDF5Constants.H5T_NATIVE_INT, attr_data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

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

		// Close to the dataset.
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

	public static void main(String[] args) {
		H5_CreateAttribute.CreateDatasetAttribute();
	}

	@Test
	public void test() {
		H5_CreateAttribute.CreateDatasetAttribute();
	}
}
