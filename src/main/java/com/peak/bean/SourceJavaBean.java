package com.peak.bean;

import java.util.List;

/**
 * 
 * @author Pan
 *
 */
public class SourceJavaBean {
	private String sourcePath;
	private String destPath;
	private String sourceClassesPath;
	private List<String> sourceSubClasses;
	private String destClassPath;
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	public String getSourceClassesPath() {
		return sourceClassesPath;
	}
	public void setSourceClassesPath(String sourceClassesPath) {
		this.sourceClassesPath = sourceClassesPath;
	}
	public String getDestClassPath() {
		return destClassPath;
	}
	public void setDestClassPath(String destClassPath) {
		this.destClassPath = destClassPath;
	}
	public List<String> getSourceSubClasses() {
		return sourceSubClasses;
	}
	public void setSourceSubClasses(List<String> sourceSubClasses) {
		this.sourceSubClasses = sourceSubClasses;
	}
}
