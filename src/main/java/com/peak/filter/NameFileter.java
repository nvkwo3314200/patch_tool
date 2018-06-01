package com.peak.filter;

import java.io.File;

public class NameFileter implements FileFilter{
	public static String[] DENY_FILES = {};
	
	public NameFileter() {
		
	}
	
	public NameFileter(String...strings) {
		DENY_FILES = strings;
	}
	
	@Override
	public boolean access(File file) {
		if(DENY_FILES == null) return true;
		for(String item : DENY_FILES) {
			if(file.getName().endsWith(item)) return false;
		}
		return true;
	}
	
}
