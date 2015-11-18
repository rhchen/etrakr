package net.sf.etrakr.persistent.hdf.serivce.impl;

import org.eclipse.core.runtime.Assert;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;
import net.sf.etrakr.persistent.hdf.serivce.IHdfService;

public class HdfService implements IHdfService {

	private static String fname = "C:\\tmp\\etrakr_trace.h5";
	
	private static long[] dims3D = { 20, 10, 5 };
	
	private H5File _h5file;
	
	private Group _root_group;
	
	private int _h5file_file_identifier;
	
	public static HdfService instance(){
		
		return new HdfService();
	}
	
	public HdfService open() throws Exception{
		
		_Obj_CreateFile();
		
		Assert.isNotNull(this._h5file);
		
		this._h5file_file_identifier = this._h5file.open();
		
		_Obj_CreateStructure();
		
		return this;
	}
	
	public void close() throws HDF5Exception{
		
		Assert.isNotNull(this._h5file);
		
		this._h5file.close();
	}
	
	private HdfService _Obj_CreateFile() throws Exception{
		
		// Retrieve an instance of the implementing class for the HDF5 format
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

        // If the implementing class wasn't found, it's an error.
        Assert.isNotNull(fileFormat);
        
        H5File testFile = (H5File) fileFormat.createFile(fname, FileFormat.FILE_CREATE_DELETE);
        
        Assert.isNotNull(testFile);
        
        this._h5file = testFile;
        
		return this;
	}

	private void _Obj_CreateStructure(){
		
		Assert.isNotNull(this._h5file);
		
		//retrieve the root group
		Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) this._h5file.getRootNode()).getUserObject();
		
		Assert.isNotNull(root);
		
		this._root_group = root;
	}
}
