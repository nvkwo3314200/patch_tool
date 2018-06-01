package com.peak.filter;

import java.io.File;
import java.util.Date;

public class TimeFileter implements FileFilter {
	private Date accessDate = null;
	
	public TimeFileter(Date accessDate) {
		this.accessDate = accessDate;
	}
	
	@Override
	public boolean access(File file) {
		if(file.lastModified() > accessDate.getTime()) return true;
		return false;
	}

}
