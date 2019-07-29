package com.peak.file;

import com.peak.file.impl.FileServiceImpl;

public class FileServiceHelper {
	private static IFileService fileService;
	
	public static IFileService getFileService() {
		if(fileService == null) {
			synchronized (FileServiceHelper.class) {
				if(fileService == null) {
					fileService = new FileServiceImpl();
				}
			}
		}
		return fileService;
	}
}
