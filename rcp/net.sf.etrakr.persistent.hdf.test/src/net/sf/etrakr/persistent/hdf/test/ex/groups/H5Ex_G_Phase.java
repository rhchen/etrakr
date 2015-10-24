/************************************************************
  This example shows how to set the conditions for
  conversion between compact and dense (indexed) groups.

  This file is intended for use with HDF5 Library version 1.8
 ************************************************************/
package net.sf.etrakr.persistent.hdf.test.ex.groups;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.structs.H5G_info_t;


public class H5Ex_G_Phase {
    private static String FILE = "H5Ex_G_Phase.h5";
    private static int MAX_GROUPS = 7;
    private static int MAX_COMPACT = 5;
    private static int MIN_DENSE = 3;

    enum H5G_storage {
        H5G_STORAGE_TYPE_UNKNOWN(-1),
        H5G_STORAGE_TYPE_SYMBOL_TABLE(0),
        H5G_STORAGE_TYPE_COMPACT(1),
        H5G_STORAGE_TYPE_DENSE(2);

        private static final Map<Integer, H5G_storage> lookup = new HashMap<Integer, H5G_storage>();

        static {
            for (H5G_storage s : EnumSet.allOf(H5G_storage.class))
                lookup.put(s.getCode(), s);
        }

        private int code;

        H5G_storage(int layout_type) {
            this.code = layout_type;
        }

        public int getCode() {
            return this.code;
        }

        public static H5G_storage get(int code) {
            return lookup.get(code);
        }
    }

    private static void CreateGroup() {
        int            file_id = -1;
        int            group_id = -1;
        int            subgroup_id = -1;
        int            fapl_id = -1;
        int            gcpl_id = -1;
        H5G_info_t     ginfo;
        String         name = "G0";    // Name of subgroup_id
        int            i;

        //Set file access property list to allow the latest file format.This will allow the library to create new format groups.
        try {
            fapl_id = H5.H5Pcreate(HDF5Constants.H5P_FILE_ACCESS);
            if(fapl_id >= 0)
                H5.H5Pset_libver_bounds (fapl_id, HDF5Constants.H5F_LIBVER_LATEST, HDF5Constants.H5F_LIBVER_LATEST);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Create group access property list and set the phase change conditions.  
        try {
            gcpl_id = H5.H5Pcreate(HDF5Constants.H5P_GROUP_CREATE);
            if(gcpl_id >= 0)
                H5.H5Pset_link_phase_change (gcpl_id, MAX_COMPACT, MIN_DENSE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Create a new file using the default properties.
        try {
            if(fapl_id >= 0)
                file_id = H5.H5Fcreate (FILE, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, fapl_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Create primary group.
        try {
            if((file_id >= 0) && (gcpl_id >= 0))
                group_id = H5.H5Gcreate(file_id, name, HDF5Constants.H5P_DEFAULT, gcpl_id, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Add subgroups to "group" one at a time, print the storage type for "group" after each subgroup is created.
        for (i = 1; i <= MAX_GROUPS; i++) {
            //Define the subgroup name and create the subgroup.
            char append = (char) (((char)i) + '0');
            name = name + append; /* G1, G2, G3 etc. */
            try {
                if(group_id >= 0) {
                    subgroup_id = H5.H5Gcreate(group_id, name, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT,
                            HDF5Constants.H5P_DEFAULT);
                    H5.H5Gclose (subgroup_id);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //Obtain the group info and print the group storage type
            try {
                if(group_id >= 0) {
                    ginfo = H5.H5Gget_info (group_id);
                    System.out.print(ginfo.nlinks + " Group"+(ginfo.nlinks == 1 ? " " : "s") + ": Storage type is ");
                    switch (H5G_storage.get(ginfo.storage_type)) {
                    case H5G_STORAGE_TYPE_COMPACT:
                        System.out.println("H5G_STORAGE_TYPE_COMPACT"); // New compact format
                        break;
                    case H5G_STORAGE_TYPE_DENSE:
                        System.out.println("H5G_STORAGE_TYPE_DENSE"); //New dense (indexed) format
                        break;
                    case H5G_STORAGE_TYPE_SYMBOL_TABLE:
                        System.out.println("H5G_STORAGE_TYPE_SYMBOL_TABLE"); //Original format 
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println();

        //Delete subgroups one at a time, print the storage type for "group" after each subgroup is deleted.
        for (i = MAX_GROUPS; i >= 1; i--) {
            //Define the subgroup name and delete the subgroup.
            try {
                H5.H5Ldelete(group_id, name, HDF5Constants.H5P_DEFAULT);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            name = name.substring(0, i+1);

            //Obtain the group info and print the group storage type
            try {
                if(group_id >= 0){
                    ginfo = H5.H5Gget_info(group_id);
                    System.out.print(ginfo.nlinks + " Group"+(ginfo.nlinks == 1 ? " " : "s") + ": Storage type is ");
                    switch (H5G_storage.get(ginfo.storage_type)) {
                    case H5G_STORAGE_TYPE_COMPACT:
                        System.out.println("H5G_STORAGE_TYPE_COMPACT"); // New compact format
                        break;
                    case H5G_STORAGE_TYPE_DENSE:
                        System.out.println("H5G_STORAGE_TYPE_DENSE"); //New dense (indexed) format
                        break;
                    case H5G_STORAGE_TYPE_SYMBOL_TABLE:
                        System.out.println("H5G_STORAGE_TYPE_SYMBOL_TABLE"); //Original format 
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        //Close and release resources
        try {
            if(fapl_id >= 0)
                H5.H5Pclose (fapl_id);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        try {
            if(gcpl_id >= 0)
                H5.H5Pclose (gcpl_id);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        //Close the group
        try {
            if(group_id >= 0)
                H5.H5Gclose (group_id);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        //Close the file
        try {
            if(file_id >= 0)
                H5.H5Fclose (file_id);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        H5Ex_G_Phase.CreateGroup();
    }

}
