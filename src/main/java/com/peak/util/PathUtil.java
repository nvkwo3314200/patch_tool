package com.peak.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.peak.common.Constant;

/**
 * 
 * @author Pan
 *
 */
public class PathUtil {
	public static final String PROJECT_PATH;
	public static final String PATCH_DEST;
//	private static final String[] projects;
	
	static {
		PROJECT_PATH = PropertyUtil.getValue("project.path");
		PATCH_DEST = PropertyUtil.getValue("patch.dest");
//		projects = StringUtils.split(PropertyUtil.getValue("project.name"), Constant.SEPARATOR);
	}
	
	public static String generateDestPath(String filePath, String project) {
		String rootPath = getWebRootPath(project);
		String relativePath = getRelativePath(rootPath, filePath);
		return PATCH_DEST + "/" + project + relativePath;
	}
	
	public static String getWebRootPath(String project) {
		StringBuilder sb = new StringBuilder();
		String rootFolderName = getRootFolderName(project);
		sb.append(PROJECT_PATH).append("/").append(project).append("/").append(rootFolderName == null? "" : rootFolderName);
		return sb.toString();
	}

	private static String getRootFolderName(String project) {
		File file = new File(PROJECT_PATH + "/" + project);
		for(File child : file.listFiles()) {
			if(StringUtils.equals(child.getName(), Constant.RootPath.WEB_CONTENT)) {
				return Constant.RootPath.WEB_CONTENT;
			} else if(StringUtils.equals(child.getName(), Constant.RootPath.WEB_ROOT)){
				return Constant.RootPath.WEB_ROOT;
			} else {
				if(isMavenProject(project)) {
					return String.format(Constant.MAVEN_SYMBOL_WEB_ROOT, project);
				}
			}
		}
		return null;
	}

	private static boolean isMavenProject(String project) {
		File file = new File(PROJECT_PATH + "/"  + project + "/" + String.format(Constant.MAVEN_SYMBOL_WEB_ROOT, project));
		if(file.exists()) {
			return true;
		}
		return false;
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
