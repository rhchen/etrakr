/************************************************************

  This example shows how to read and write object references
  to a dataset.  The program first creates objects in the
  file and writes references to those objects to a dataset
  with a dataspace of DIM0, then closes the file.  Next, it
  reopens the file, dereferences the references, and outputs
  the names of their targets to the screen.

  This file is intended for use with HDF5 Library verion 1.6

 ************************************************************/
package net.sf.etrakr.persistent.hdf.test.ex.datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;
import ncsa.hdf.object.h5.H5ScalarDS;

public class H5ObjectEx_T_ObjectReference {
	private static String FILENAME = "H5ObjectEx_T_ObjectReference.h5";
	private static String DATASETNAME = "DS1";
	private static String DATASETNAME2 = "DS2";
	private static String GROUPNAME = "G1";
	private static final int DIM0 = 2;
	private static final int RANK = 1;

	// Values for the status of space allocation
	enum H5G_obj {
        H5G_UNKNOWN(HDF5Constants.H5G_UNKNOWN), /* Unknown object type */
        H5G_GROUP(HDF5Constants.H5G_GROUP), /* Object is a group */
        H5G_DATASET(HDF5Constants.H5G_DATASET), /* Object is a dataset */
        H5G_TYPE(HDF5Constants.H5G_TYPE), /* Object is a named data type */
        H5G_LINK(HDF5Constants.H5G_LINK), /* Object is a symbolic link */
        H5G_UDLINK(HDF5Constants.H5G_UDLINK), /* Object is a user-defined link */
        H5G_RESERVED_5(HDF5Constants.H5G_RESERVED_5), /* Reserved for future use */
        H5G_RESERVED_6(HDF5Constants.H5G_RESERVED_6), /* Reserved for future use */
        H5G_RESERVED_7(HDF5Constants.H5G_RESERVED_7); /* Reserved for future use */
		private static final Map<Integer, H5G_obj> lookup = new HashMap<Integer, H5G_obj>();

		static {
			for (H5G_obj s : EnumSet.allOf(H5G_obj.class))
				lookup.put(s.getCode(), s);
		}

		private int code;

		H5G_obj(int layout_type) {
			this.code = layout_type;
		}

		public int getCode() {
			return this.code;
		}

		public static H5G_obj get(int code) {
			return lookup.get(code);
		}
	}

	private static void writeObjRef() {
        H5File file = null;
        H5ScalarDS dset = null;
        H5ScalarDS dset2 = null;
        H5Group grp = null;
		long[] dims = { DIM0 };
        long[] dset_data = new long[DIM0];
        final H5Datatype typeInt = new H5Datatype(Datatype.CLASS_INTEGER, 8, Datatype.ORDER_BE, -1);
        final H5Datatype typeRef = new H5Datatype(Datatype.CLASS_REFERENCE, -1, -1, -1);

		// Create a new file using default properties.
		try {
            file = new H5File(FILENAME, FileFormat.CREATE);
            file.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create dataset with a scalar dataspace.
		try {
            dset2 = (H5ScalarDS) file.createScalarDS(DATASETNAME2, null, typeInt, dims, null, null, 0, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create a group in the file.
		try {
		    grp = (H5Group) file.createGroup("/" + GROUPNAME, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create references to the previously created objects. Passing -1
		// as space_id causes this parameter to be ignored. Other values
		// besides valid dataspaces result in an error.
		try {
            dset_data[0] = grp.getOID()[0];
            dset_data[1] = dset2.getOID()[0];
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Create the dataset.
		try {
            dset = (H5ScalarDS) file.createScalarDS(DATASETNAME, null, typeRef, dims, null, null, 0, dset_data);
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

	private static void readObjRef() {
        H5File file = null;
        H5ScalarDS dset = null;
		int dataset_id = -1;
		int dataspace_id = -1;
		int object_type = -1;
		int object_id = -1;
		long[] dims = { DIM0 };
		byte[][] dset_data;

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

		// Get dataspace and allocate memory for read buffer.
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

		// Allocate array of pointers to two-dimensional arrays (the
		// elements of the dataset.
		dset_data = new byte[(int) dims[0]][8];

		// Read the data using the default properties.
		try {
			if (dataset_id >= 0) {
				H5.H5Dread(dataset_id, HDF5Constants.H5T_STD_REF_OBJ,
						HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, 
						dset_data);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Output the data to the screen.
		for (int indx = 0; indx < dims[0]; indx++) {
			System.out.println(DATASETNAME + "[" + indx + "]:");
			System.out.print("  ->");
			// Open the referenced object, get its name and type.
			try {
				if (dataset_id >= 0) {
                    int[] otype = { 1 };
					object_id = H5.H5Rdereference(dataset_id, HDF5Constants.H5R_OBJECT, dset_data[indx]);
					object_type = H5.H5Rget_obj_type(dataset_id, HDF5Constants.H5R_OBJECT, dset_data[indx], otype);
				}
				String[] obj_name = new String[1];
				long name_size = 1;
				if (object_type >= 0) {
					// Get the length of the name and retrieve the name.
					name_size = 1 + H5.H5Iget_name(object_id, obj_name, name_size);
				}
				if ((object_id >= 0) && (object_type >= -1)) {
					switch (H5G_obj.get(object_type)) {
					case H5G_GROUP:
						System.out.print("H5G_GROUP");
						try {
							if (object_id >= 0)
								H5.H5Gclose(object_id);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case H5G_DATASET:
						System.out.print("H5G_DATASET");
						try {
							if (object_id >= 0)
								H5.H5Dclose(object_id);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case H5G_TYPE:
						System.out.print("H5G_TYPE");
						try {
							if (object_id >= 0)
								H5.H5Tclose(object_id);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					default:
						System.out.print("UNHANDLED");
					}
				}
				// Print the name.
				if (name_size > 1)
					System.out.println(": " + obj_name[0]);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// End access to the dataset and release resources used by it.
		try {
			if (dataspace_id >= 0)
				H5.H5Sclose(dataspace_id);
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

		// Close the file.
		try {
            file.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Check if gzip compression is available and can be used for both
		// compression and decompression. Normally we do not perform error
		// checking in these examples for the sake of clarity, but in this
		// case we will make an exception because this filter is an
		// optional part of the hdf5 library.
		H5ObjectEx_T_ObjectReference.writeObjRef();
		H5ObjectEx_T_ObjectReference.readObjRef();
	}

}
