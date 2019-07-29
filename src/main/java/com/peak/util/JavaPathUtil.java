package com.peak.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.peak.bean.SourceJavaBean;
import com.peak.common.Constant;

/**
 * 
 * @author Pan
 *
 */
public class JavaPathUtil {
	private static final String JAVA_SUFFIX = ".java";
	private static final String CLASS_SUFFIX = ".class";
	private static final String CENTER_FOLDER = "/WEB-INF/classes/";
	private static final String SYMBOL = "$";
	
	public static boolean isJava(String filePath, String project) {
		return filePath.endsWith(JAVA_SUFFIX) && checkSourceFile(filePath, project);
	}
	
	public static SourceJavaBean generateDestPath(String filePath, String project) {
		//1. 添加源
		SourceJavaBean bean = new SourceJavaBean();
		bean.setSourcePath(filePath);
		//2. 找到源目标地址
		String rootPath = getRootPath(project);
		String relativePath = PathUtil.getRelativePath(rootPath, filePath);
		bean.setDestPath(PathUtil.PATCH_DEST +"/"+ project +"/"+ Constant.PatchSource.SRC + "/" + relativePath);
		//3. 找到对应的源class地址
		bean.setSourceClassesPath(findClassPath(project, relativePath));
		//4. 找到对应目标class地址
		bean.setDestClassPath(PathUtil.generateDestPath(bean.getSourceClassesPath(), project));
		//5. 找到对应的内部类
		bean.setSourceSubClasses(findSourceSubClasses(bean.getSourceClassesPath()));
		return bean;
	}
	
	private static List<String> findSourceSubClasses(String sourceClassesPath) {
		List<String> list = new ArrayList<String>();
		File file = new File(sourceClassesPath);
		String name = file.getName().substring(0, file.getName().lastIndexOf("."));
		try {
		for(File item : file.getParentFile().listFiles()) {
			if(item.getName().startsWith(name + SYMBOL)) {
				list.add(item.getName());
			}
		}
		}catch (Exception e) {
			System.out.println(file.getAbsolutePath());
		}
		return list;
	}

	private static String findClassPath(String project, String relativePath) {
		String webRootPath = PathUtil.getWebRootPath(project);
		String classRelativePath = relativePath.replaceAll(JAVA_SUFFIX, CLASS_SUFFIX);
		return webRootPath + CENTER_FOLDER + classRelativePath;
	}

	private static String getRootPath(String project) {
		String mavenRoot = getMavenRootPath(project);
		if(mavenRoot != null) {
			return PathUtil.PROJECT_PATH + "/" +project +"/"+ mavenRoot + Constant.RootPath.MAVEN_SOURCE_ROOT;
		}
		return PathUtil.PROJECT_PATH + "/" +project +"/"+ Constant.RootPath.SOURCE_ROOT;
	}
	
	private static boolean checkSourceFile(String filePath, String project) {
		File file = new File(getRootPath(project));
		if(filePath.contains(file.getAbsolutePath())) {
			return true;
		}
		return false;
	}
	
	public static String getSameJavaPathFromClass(String classPath) {
		return classPath.replace(CLASS_SUFFIX, JAVA_SUFFIX);
	}

	public static boolean isJavaSourceFolder(String absolutePath, String project) {
		File file = new File(getRootPath(project));
		if(absolutePath.contains(file.getAbsolutePath())) {
			return true;
		}
		return false;
	}
	
	public static String getMavenRootPath(String project) {
		File file = new File(PathUtil.PROJECT_PATH + "/" +project +"/" + Constant.MAVEN_SYMBOL_MAIN_ROOT);
		if(file.exists()) {
			return Constant.MAVEN_SYMBOL_MAIN_ROOT;
		}
		return null;
		
	}
	
}
