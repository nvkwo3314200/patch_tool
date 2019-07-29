package com.peak.file;

/**
 * 
 * @author Pan
 *
 */
public interface IFileService {
	
	/**
	 * 用于拷贝文件
	 * @param source 表示源文件
	 * @param dest 表示目标文件
	 * @return true 表示复制成功
	 * 
	 */
	boolean copyFile(String source, String dest);
	
	/**
	 * 用于拷贝文件
	 * @param source 表示源文件
	 * @param dest 表示目标文件
	 * @param modifyDate true 表示修改文件时候为当前时间
	 * @return true 表示复制成功
	 */
	boolean copyFile(String source, String dest, boolean modifyDate);
	
	/** 
	 * java文件要根据.java文件判断是否copy .classes文件
	 * @param source
	 * @param dests
	 * @return
	 */
	boolean copyJavaFile(String source, String... dests);
	
	/**
	 * 用于删除文件
	 * @param sourece 将被删除的文件，可以是文件夹
	 * @return
	 */
	boolean deleteFile(String... sourece);
}
