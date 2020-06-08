package com.peak.common;

/**
 * 
 * @author Pan
 * @date 2018/12/14
 *
 */
public class Constant {
	
	public static final String SEPARATOR = ",";
	public static final String MAVEN_SYMBOL_WEB_ROOT= "target/%s";
	public static final String MAVEN_SYMBOL_MAIN_ROOT= "/src/main/";
	
	public interface RootPath {
		String WEB_CONTENT = "WebContent";
		String WEB_ROOT = "WebRoot";
		
		String SOURCE_ROOT="src";
		String MAVEN_WEB_ROOT = "webapp";
		String MAVEN_SOURCE_ROOT = "java";
	}
	
	public interface PatchSource {
		String SRC = "src";
		String CLASS = "class";
	}
}
