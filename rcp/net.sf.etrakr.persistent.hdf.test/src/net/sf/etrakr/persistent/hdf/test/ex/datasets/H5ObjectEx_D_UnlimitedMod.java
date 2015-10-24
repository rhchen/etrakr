/************************************************************
  This example shows how to create and extend an unlimited
  dataset.  The program first writes integers to a dataset
  with dataspace dimensions of DIM_XxDIM_Y, then closes the
  file.  Next, it reopens the file, reads back the data,
  outputs it to the screen, extends the dataset, and writes
  new data to the entire extended dataset.  Finally it
  reopens the file again, reads back the data, and utputs it
  to the screen.
 ************************************************************/
package net.sf.etrakr.persistent.hdf.test.ex.datasets;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5ScalarDS;

public class H5ObjectEx_D_UnlimitedMod {
	private static String FILENAME = "H5ObjectEx_D_UnlimitedMod.h5";
	private static String DATASETNAME = "DS1";
	private static final int DIM_X = 4;
	private static final int DIM_Y = 7;
	private static final int EDIM_X = 6;
	private static final int EDIM_Y = 10;
	private static final int CHUNK_X = 4;
	private static final int CHUNK_Y = 4;
	private static final int RANK = 2;
	private static final int NDIMS = 2;
    private static final int DATATYPE_SIZE = 4;

	private static void writeUnlimited() {
        H5File file = null;
        Dataset dset = null;
		int file_id = -1;
		int dcpl_id = -1;
		int dataspace_id = -1;
		int dataset_id = -1;
        int type_id = -1;
		long[] dims = { DIM_X, DIM_Y };
		long[] chunk_dims = { CHUNK_X, CHUNK_Y };
		long[] maxdims = { HDF5Constants.H5S_UNLIMITED, HDF5Constants.H5S_UNLIMITED };
		int[][] dset_data = new int[DIM_X][DIM_Y];
        final H5Datatype typeInt = new H5Datatype(Datatype.CLASS_INTEGER,
                DATATYPE_SIZE, Datatype.ORDER_LE, -1);

		// Initialize the dataset.
		for (int indx = 0; indx < DIM_X; indx++)
			for (int jndx = 0; jndx < DIM_Y; jndx++)
				dset_data[indx][jndx] = indx * jndx - jndx;

		// Create a new file using default properties.
		try {
            file = new H5File(FILENAME, FileFormat.CREATE);
            file_id = file.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create dataspace with unlimited dimensions.
		try {
			dataspace_id = H5.H5Screate_simple(RANK, dims, maxdims);
            type_id = typeInt.toNative();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create the dataset creation property list.
		try {
			dcpl_id = H5.H5Pcreate(HDF5Constants.H5P_DATASET_CREATE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Set the chunk size.
		try {
			if (dcpl_id >= 0)
				H5.H5Pset_chunk(dcpl_id, NDIMS, chunk_dims);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create the unlimited dataset.
		try {
			if ((file_id >= 0) && (dataspace_id >= 0) && (dcpl_id >= 0))
				dataset_id = H5.H5Dcreate(file_id, DATASETNAME,
				        type_id, dataspace_id, 
						HDF5Constants.H5P_DEFAULT, dcpl_id, HDF5Constants.H5P_DEFAULT);
            dset = new H5ScalarDS(file, DATASETNAME, "/");
            Group pgroup = (Group) file.get("/");
            pgroup.addToMemberList(dset);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Write the data to the dataset.
		try {
            dset.write(dset_data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// End access to the dataset and release resources used by it.
		try {
			if (dcpl_id >= 0)
				H5.H5Pclose(dcpl_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        
        try {
            if (type_id >= 0)
                H5.H5Tclose(type_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
			if (dataset_id >= 0)
                dset.close(dataset_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (dataspace_id >= 0)
				H5.H5Sclose(dataspace_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Close the file.
		try {
            file.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void extendUnlimited() {
        H5File file = null;
        H5ScalarDS dset = null;
		int dataspace_id = -1;
		int dataset_id = -1;
		long[] dims = { DIM_X, DIM_Y };
		long[] extdims = { EDIM_X, EDIM_Y };
		int[] dset_data;
		int[][] extend_dset_data = new int[EDIM_X][EDIM_Y];

		// Open an existing file.
		try {
            file = new H5File(FILENAME, FileFormat.WRITE);
            file.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Open an existing dataset.
		try {
            dset = (H5ScalarDS) file.get(DATASETNAME);
            dataset_id = dset.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Get dataspace and allocate memory for read buffer. This is a
		// two dimensional dataset so the dynamic allocation must be done
		// in steps.
		try {
			if (dataset_id >= 0)
				dataspace_id = H5.H5Dget_space(dataset_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (dataspace_id >= 0)
				H5.H5Sget_simple_extent_dims(dataspace_id, dims, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Allocate array of pointers to rows.
		dset_data = new int[(int) dims[0]*(int) dims[1]];

		// Read the data using the default properties.
		try {
            dset.init();
            dset_data = (int[]) dset.getData();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Output the data to the screen.
		System.out.println("Dataset before extension:");
		for (int indx = 0; indx < DIM_X; indx++) {
			System.out.print(" [ ");
			for (int jndx = 0; jndx < DIM_Y; jndx++)
				System.out.print(dset_data[indx*DIM_Y+jndx] + " ");
			System.out.println("]");
		}
		System.out.println();

		try {
			if (dataspace_id >= 0)
				H5.H5Sclose(dataspace_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Extend the dataset.
		try {
            dset.extend(extdims);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Initialize data for writing to the extended dataset.
		for (int indx = 0; indx < EDIM_X; indx++)
			for (int jndx = 0; jndx < EDIM_Y; jndx++)
				extend_dset_data[indx][jndx] = jndx;

		// Write the data to the extended dataset.
		try {
            dset.init();
            dset.write(extend_dset_data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// End access to the dataset and release resources used by it.
		try {
			if (dataset_id >= 0)
                dset.close(dataset_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Close the file.
		try {
            file.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readUnlimited() {
        H5File file = null;
        H5ScalarDS dset = null;
		int dataspace_id = -1;
		int dataset_id = -1;
		long[] dims = { DIM_X, DIM_Y };
		int[] dset_data;

		// Open an existing file.
		try {
            file = new H5File(FILENAME, FileFormat.READ);
            file.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Open an existing dataset.
		try {
            dset = (H5ScalarDS) file.get(DATASETNAME);
            dataset_id = dset.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Get dataspace and allocate memory for the read buffer as before.
		try {
			if (dataset_id >= 0)
				dataspace_id = H5.H5Dget_space(dataset_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (dataspace_id >= 0)
				H5.H5Sget_simple_extent_dims(dataspace_id, dims, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// Allocate array of pointers to rows.
		dset_data = new int[(int) dims[0]*(int) dims[1]];

		// Read the data using the default properties.
		try {
            dset.init();
            dset_data = (int[]) dset.getData();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Output the data to the screen.
		System.out.println("Dataset after extension:");
		for (int indx = 0; indx < dims[0]; indx++) {
			System.out.print(" [ ");
			for (int jndx = 0; jndx < dims[1]; jndx++)
				System.out.print(dset_data[(int) (indx*dims[1]+jndx)] + " ");
			System.out.println("]");
		}
		System.out.println();

		// End access to the dataset and release resources used by it.
		try {
			if (dataset_id >= 0)
                dset.close(dataset_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (dataspace_id >= 0)
				H5.H5Sclose(dataspace_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Close the file.
		try {
            file.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		H5ObjectEx_D_UnlimitedMod.writeUnlimited();
		H5ObjectEx_D_UnlimitedMod.extendUnlimited();
		H5ObjectEx_D_UnlimitedMod.readUnlimited();
	}

}
