package com.peak.filter;

import java.io.File;
import java.util.Date;

/**
 * 
 * @author Pan
 *
 */
public class TimeFileter implements FileFilter {
	private Date accessDate = null;
	private Date limitDate = null;
	
	public TimeFileter(Date accessDate, Date limitDate) {
		this.accessDate = accessDate;
		this.limitDate = limitDate;
	}
	
	@Override
	public boolean access(File file) {
		if(file.lastModified() > accessDate.getTime()) {
			if(limitDate == null || file.lastModified() <= limitDate.getTime()) {
				return true;
			}
		}
		return false;
	}

}
