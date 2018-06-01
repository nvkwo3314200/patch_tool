package com.peak.filter.impl;

import java.io.File;

import com.peak.filter.FileFilter;
import com.peak.filter.NameFileter;
import com.peak.filter.TimeFileter;

public class CombineFilter implements FileFilter{
	FileFilter[] list = null;
	
	public CombineFilter(FileFilter...fileFilters ) {
		this.list = fileFilters;
	}

	@Override
	public boolean access(File file) {
		if(list != null) {
			for(FileFilter filter : list) {
				if(!filter.access(file)) return false;
			}
		}
		return true;
	}

}
