package net.sf.etrakr.tmf.adb.core.internal;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
uint32   flags
uint64   size           present only if flag SSH_FILEXFER_ATTR_SIZE
uint32   uid            present only if flag SSH_FILEXFER_ATTR_UIDGID
uint32   gid            present only if flag SSH_FILEXFER_ATTR_UIDGID
uint32   permissions    present only if flag SSH_FILEXFER_ATTR_PERMISSIONS
uint32   atime          present only if flag SSH_FILEXFER_ACMODTIME
uint32   mtime          present only if flag SSH_FILEXFER_ACMODTIME
uint32   extended_count present only if flag SSH_FILEXFER_ATTR_EXTENDED
string   extended_type
string   extended_data
  ...      more extended data (extended_type - extended_data pairs),
           so that number of pairs equals extended_count
*/
public class AdbSftpATTRS {
	static final int S_ISUID = 04000; // set user ID on execution
	  static final int S_ISGID = 02000; // set group ID on execution
	  static final int S_ISVTX = 01000; // sticky bit   ****** NOT DOCUMENTED *****

	  static final int S_IRUSR = 00400; // read by owner
	  static final int S_IWUSR = 00200; // write by owner
	  static final int S_IXUSR = 00100; // execute/search by owner
	  static final int S_IREAD = 00400; // read by owner
	  static final int S_IWRITE= 00200; // write by owner
	  static final int S_IEXEC = 00100; // execute/search by owner

	  static final int S_IRGRP = 00040; // read by group
	  static final int S_IWGRP = 00020; // write by group
	  static final int S_IXGRP = 00010; // execute/search by group

	  static final int S_IROTH = 00004; // read by others
	  static final int S_IWOTH = 00002; // write by others
	  static final int S_IXOTH = 00001; // execute/search by others

	  private static final int pmask = 0xFFF;

	  public String getPermissionsString() {
	    StringBuffer buf = new StringBuffer(10);

	    if(isDir()) buf.append('d');
	    else if(isLink()) buf.append('l');
	    else buf.append('-');

	    if((permissions & S_IRUSR)!=0) buf.append('r');
	    else buf.append('-');

	    if((permissions & S_IWUSR)!=0) buf.append('w');
	    else buf.append('-');

	    if((permissions & S_ISUID)!=0) buf.append('s');
	    else if ((permissions & S_IXUSR)!=0) buf.append('x');
	    else buf.append('-');

	    if((permissions & S_IRGRP)!=0) buf.append('r');
	    else buf.append('-');

	    if((permissions & S_IWGRP)!=0) buf.append('w');
	    else buf.append('-');

	    if((permissions & S_ISGID)!=0) buf.append('s');
	    else if((permissions & S_IXGRP)!=0) buf.append('x');
	    else buf.append('-');

	    if((permissions & S_IROTH) != 0) buf.append('r');
	    else buf.append('-');

	    if((permissions & S_IWOTH) != 0) buf.append('w');
	    else buf.append('-');

	    if((permissions & S_IXOTH) != 0) buf.append('x');
	    else buf.append('-');
	    return (buf.toString());
	  }

	  public String  getAtimeString(){
	    Date date= new Date(((long)atime)*1000L);
	    return (date.toString());
	  }

	  public String  getMtimeString(){
	    Date date= new Date(((long)mtime)*1000L);
	    return (date.toString());
	  }

	  public static final int SSH_FILEXFER_ATTR_SIZE=         0x00000001;
	  public static final int SSH_FILEXFER_ATTR_UIDGID=       0x00000002;
	  public static final int SSH_FILEXFER_ATTR_PERMISSIONS=  0x00000004;
	  public static final int SSH_FILEXFER_ATTR_ACMODTIME=    0x00000008;
	  public static final int SSH_FILEXFER_ATTR_EXTENDED=     0x80000000;

	  static final int S_IFMT=0xf000;
	  static final int S_IFIFO=0x1000;
	  static final int S_IFCHR=0x2000;
	  static final int S_IFDIR=0x4000;
	  static final int S_IFBLK=0x6000;
	  static final int S_IFREG=0x8000;
	  static final int S_IFLNK=0xa000;
	  static final int S_IFSOCK=0xc000;

	  int flags=0;
	  long size;
	  int uid;
	  int gid;
	  int permissions;
	  int atime;
	  int mtime;
	  String[] extended=null;

	  private AdbSftpATTRS(){
	  }

	  static AdbSftpATTRS getATTR(AdbBuffer buf){
		  AdbSftpATTRS attr=new AdbSftpATTRS();	
	    attr.flags=buf.getInt();
	    if((attr.flags&SSH_FILEXFER_ATTR_SIZE)!=0){ attr.size=buf.getLong(); }
	    if((attr.flags&SSH_FILEXFER_ATTR_UIDGID)!=0){
	      attr.uid=buf.getInt(); attr.gid=buf.getInt();
	    }
	    if((attr.flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0){ 
	      attr.permissions=buf.getInt();
	    }
	    if((attr.flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ 
	      attr.atime=buf.getInt();
	    }
	    if((attr.flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ 
	      attr.mtime=buf.getInt(); 
	    }
	    if((attr.flags&SSH_FILEXFER_ATTR_EXTENDED)!=0){
	      int count=buf.getInt();
	      if(count>0){
		attr.extended=new String[count*2];
		for(int i=0; i<count; i++){
		  attr.extended[i*2]=AdbUtil.byte2str(buf.getString());
		  attr.extended[i*2+1]=AdbUtil.byte2str(buf.getString());
		}
	      }
	    }
	    return attr;
	  } 

	  int length(){
	    int len=4;

	    if((flags&SSH_FILEXFER_ATTR_SIZE)!=0){ len+=8; }
	    if((flags&SSH_FILEXFER_ATTR_UIDGID)!=0){ len+=8; }
	    if((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0){ len+=4; }
	    if((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ len+=8; }
	    if((flags&SSH_FILEXFER_ATTR_EXTENDED)!=0){
	      len+=4;
	      int count=extended.length/2;
	      if(count>0){
		for(int i=0; i<count; i++){
		  len+=4; len+=extended[i*2].length();
		  len+=4; len+=extended[i*2+1].length();
		}
	      }
	    }
	    return len;
	  }

	  void dump(AdbBuffer buf){
	    buf.putInt(flags);
	    if((flags&SSH_FILEXFER_ATTR_SIZE)!=0){ buf.putLong(size); }
	    if((flags&SSH_FILEXFER_ATTR_UIDGID)!=0){
	      buf.putInt(uid); buf.putInt(gid);
	    }
	    if((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0){ 
	      buf.putInt(permissions);
	    }
	    if((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ buf.putInt(atime); }
	    if((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ buf.putInt(mtime); }
	    if((flags&SSH_FILEXFER_ATTR_EXTENDED)!=0){
	      int count=extended.length/2;
	      if(count>0){
		for(int i=0; i<count; i++){
		  buf.putString(AdbUtil.str2byte(extended[i*2]));
		  buf.putString(AdbUtil.str2byte(extended[i*2+1]));
		}
	      }
	    }
	  }
	  void setFLAGS(int flags){
	    this.flags=flags;
	  }
	  public void setSIZE(long size){
	    flags|=SSH_FILEXFER_ATTR_SIZE;
	    this.size=size;
	  }
	  public void setUIDGID(int uid, int gid){
	    flags|=SSH_FILEXFER_ATTR_UIDGID;
	    this.uid=uid;
	    this.gid=gid;
	  }
	  public void setACMODTIME(int atime, int mtime){
	    flags|=SSH_FILEXFER_ATTR_ACMODTIME;
	    this.atime=atime;
	    this.mtime=mtime;
	  }
	  public void setPERMISSIONS(int permissions){
	    flags|=SSH_FILEXFER_ATTR_PERMISSIONS;
	    permissions=(this.permissions&~pmask)|(permissions&pmask);
	    this.permissions=permissions;
	  }

	  private boolean isType(int mask) {
	    return (flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0 &&
	           (permissions&S_IFMT)==mask;
	  }

	  public boolean isReg(){
	    return isType(S_IFREG);
	  }

	  public boolean isDir(){
	    return isType(S_IFDIR);
	  }      

	  public boolean isChr(){
	    return isType(S_IFCHR);
	  }      

	  public boolean isBlk(){
	    return isType(S_IFBLK);
	  }      

	  public boolean isFifo(){
	    return isType(S_IFIFO);
	  }      

	  public boolean isLink(){
	    return isType(S_IFLNK);
	  }

	  public boolean isSock(){
	    return isType(S_IFSOCK);
	  }

	  public int getFlags() { return flags; }
	  public long getSize() { return size; }
	  public int getUId() { return uid; }
	  public int getGId() { return gid; }
	  public int getPermissions() { return permissions; }
	  public int getATime() { return atime; }
	  public int getMTime() { return mtime; }
	  public String[] getExtended() { return extended; }

	  public String toString() {
	    return (getPermissionsString()+" "+getUId()+" "+getGId()+" "+getSize()+" "+getMtimeString());
	  }
}
