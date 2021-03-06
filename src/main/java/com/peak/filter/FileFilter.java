package com.peak.filter;

import java.io.File;

/**
 *
 * @ClassName  FileFilter
 * @Description 对文件进行过滤
 * @author 潘高峰
 * @date   2018年12月14日 下午3:19:40
 *
 */
public interface FileFilter {
	
	/**
	 * 判断文件是要被处理
	 * @title access
	 * @param file
	 * @return boolean  true 表示会被处理
	 */
	boolean access(File file);
}
