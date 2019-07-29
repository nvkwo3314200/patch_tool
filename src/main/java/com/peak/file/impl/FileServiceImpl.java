package com.peak.file.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.peak.file.IFileService;

public class FileServiceImpl implements IFileService{
	private static final Logger logger = Logger.getLogger(FileServiceImpl.class);
	@Override
	public boolean copyFile(String source, String dest) {
		return copyFile(source, dest, false);
	}
	
	@Override
	public boolean copyFile(String source, String dest, boolean modifyDate) {
		File sourceFile = new File(source);
		File destFile = new File(dest);
		if(!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		try {
			FileUtils.copyFile(sourceFile, destFile);
			if(modifyDate) {
				destFile.setLastModified(System.currentTimeMillis());
			}
			logger.debug(String.format("[success] copy file to ==> %s", destFile));
		} catch (IOException e) {
			logger.debug(String.format("[fail] copy file to ==> %s", destFile));
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean copyJavaFile(String source, String... dests) {
		if(dests == null) {
			return false;
		}
		for(String dest : dests) {
			if(!copyFile(source, dest)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean deleteFile(String... sourece) {
		for(String path : sourece) {
			try {
				FileUtils.forceDelete(new File(path));
			} catch (IOException e) {
				if(e instanceof java.io.FileNotFoundException) {
					return true;
				}
				logger.error(String.format("删除文件失败, 路径:%s", path) ,e);
				return false;
			}
		}
		return true;
	}


}
