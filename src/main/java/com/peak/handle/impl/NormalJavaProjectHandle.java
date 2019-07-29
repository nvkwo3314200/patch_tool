package com.peak.handle.impl;

import java.io.File;

import com.peak.bean.SourceJavaBean;
import com.peak.common.Constant;
import com.peak.handle.AbstractProjectHandle;
import com.peak.util.JavaPathUtil;
import com.peak.util.PathUtil;

public class NormalJavaProjectHandle extends AbstractProjectHandle {
	
	public NormalJavaProjectHandle(String project, String rootPath) {
		super(project, rootPath);
	}

	@Override
	public void dealFiles(String path) {
		File file = new File(path);
		for(File item : file.listFiles()) {
			if(item.isFile()) {
				if(filter.access(item)) {
					if(JavaPathUtil.isJava(item.getAbsolutePath(), project)) {
						SourceJavaBean bean = JavaPathUtil.generateDestPath(item.getAbsolutePath(), project);
						handleJavaFile(bean);
					}
					else if(JavaPathUtil.isJavaSourceFolder(item.getAbsolutePath(), project)) {
						// 不用从理此类文件，多为配置文件， 会被直接eclipse直接copy到classpath路径下面
					} 
					else {
						iFileService.copyFile(item.getAbsolutePath(), PathUtil.generateDestPath(item.getAbsolutePath(), project), modifyLastUpdateDate);
					}
				}
			} 
			else {
				dealFiles(item.getAbsolutePath());
			}
		}
	}
	
	private void handleJavaFile(SourceJavaBean bean) {
		// copy java files
		for(String sour : patchSources) {
			if(Constant.PatchSource.SRC.equals(sour)) {
				iFileService.copyFile(bean.getSourcePath(), bean.getDestPath(), modifyLastUpdateDate);
			}
			else if(Constant.PatchSource.CLASS.equals(sour)){
				iFileService.copyFile(bean.getSourcePath(), JavaPathUtil.getSameJavaPathFromClass(bean.getDestClassPath()), modifyLastUpdateDate);
			}
		}
		
		// copy class file
		iFileService.copyFile(bean.getSourceClassesPath(), bean.getDestClassPath(), modifyLastUpdateDate);
		
		// copy 内部类
		if(bean.getSourceSubClasses() != null && bean.getSourceSubClasses().size() > 0) {
			File source = new File(bean.getSourceClassesPath());
			File dest = new File(bean.getDestClassPath());
			String sourcePath = source.getParentFile().getAbsolutePath();
			String destPath = dest.getParentFile().getAbsolutePath();
			for(String name : bean.getSourceSubClasses()) {
				iFileService.copyFile(sourcePath + "/" + name, destPath + "/" + name, modifyLastUpdateDate);
			}
		}
	}
}
