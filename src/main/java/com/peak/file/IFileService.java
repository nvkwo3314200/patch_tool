package com.peak.file;

public interface IFileService {
	
	boolean copyFile(String source, String dest);
	
	boolean copyFile(String source, String dest, boolean modifyDate);
	
	//java文件要根据.java文件判断是否copy .classes文件
	boolean copyJavaFile(String source, String... dests);
	
	boolean deleteFile(String... sourece);
}
