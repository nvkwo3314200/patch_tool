package com.peak.filter;

import java.io.File;

public class FolderIgnore implements FileFilter {
	private static String[] folders = {};
	public FolderIgnore(String... folders) {
		FolderIgnore.folders = folders;
	} 
	@Override
	public boolean access(File file) {
		for (String folder : folders) {
			if(file.getAbsolutePath().indexOf(folder) > -1) {
				return false;
			}
		}
		return true;
	}

}
