package com.peak.filter;

import java.io.File;

/**
 * 
 * @author Pan
 *
 */
public class NameAccessFileter implements FileFilter{
	public static String[] ACCESS_FILES = {};
	
	
	public NameAccessFileter() {
		
	}
	
	public NameAccessFileter(String...strings) {
		ACCESS_FILES = strings;
	}
	
	@Override
	public boolean access(File file) {
		if(ACCESS_FILES == null || ACCESS_FILES.length == 0) {
			return true;
		}
		for(String item : ACCESS_FILES) {
			if(file.getName().endsWith(item)) {
				return true;
			}
		}
		return false;
	}
	
}
