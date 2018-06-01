package com.peak.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.peak.common.Constant;

public class PathUtil {
	public static final String projectPath;
	public static final String patchDest;
//	private static final String[] projects;
	
	static {
		projectPath = PropertyUtil.getValue("project.path");
		patchDest = PropertyUtil.getValue("patch.dest");
//		projects = StringUtils.split(PropertyUtil.getValue("project.name"), Constant.SEPARATOR);
	}
	
	public static String generateDestPath(String filePath, String project) {
		String rootPath = getWebRootPath(project);
		String relativePath = getRelativePath(rootPath, filePath);
		return patchDest + "/" + project + relativePath;
	}
	
	public static String getWebRootPath(String project) {
		StringBuilder sb = new StringBuilder();
		String rootFolderName = getRootFolderName(project);
		sb.append(projectPath).append("/").append(project).append("/").append(rootFolderName);
		return sb.toString();
	}

	private static String getRootFolderName(String project) {
		File file = new File(projectPath + "/" + project);
		for(File child : file.listFiles()) {
			if(StringUtils.equals(child.getName(), Constant.RootPath.WEB_CONTENT)) {
				return Constant.RootPath.WEB_CONTENT;
			} else if(StringUtils.equals(child.getName(), Constant.RootPath.WEB_ROOT)){
				return Constant.RootPath.WEB_ROOT;
			}
		}
		return null;
	}

	public static String getRelativePath(String rootPath, String filePath) {
		File file = new File(filePath);
		File root = new File(rootPath);
		StringBuffer sb = new StringBuffer();
		generatePath(file, root, sb);
		return sb.toString();
	}
	
	private static void generatePath(File file, File root, StringBuffer sb) {
		if(StringUtils.equals(file.getPath(), root.getPath())) {
			return ;
		} else {
			sb.insert(0, file.getName());
			sb.insert(0, "/");
			generatePath(file.getParentFile(), root, sb);
		}
	}
	
}
