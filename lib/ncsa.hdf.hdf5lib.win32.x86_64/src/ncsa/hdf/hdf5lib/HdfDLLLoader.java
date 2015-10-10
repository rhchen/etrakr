package ncsa.hdf.hdf5lib;

public class HdfDLLLoader {

	public static void loadDLL(){
		
		/* Win7 w/o this dll */
		System.loadLibrary("msvcr110");
		System.loadLibrary("jhdf5");
	}
}
