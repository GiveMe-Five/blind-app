package com.livhong.connect;

import java.io.File;

public class Item {

	public String sName;
	public int sId;
	public String rName;
	public int rId;
	public String time;
	public String Identifer;
	public String msg;
	public long length;
	public File recFile;
	
	public String toString(){
		return sName+Constants.ENTRY_SEPARATOR+sId+Constants.ENTRY_SEPARATOR+
				rName+Constants.ENTRY_SEPARATOR+rId+Constants.ENTRY_SEPARATOR+Constants.getTime()+
				Constants.ENTRY_SEPARATOR+Identifer+Constants.ENTRY_SEPARATOR+
				msg+Constants.ENTRY_SEPARATOR+length;
	}
	
}
