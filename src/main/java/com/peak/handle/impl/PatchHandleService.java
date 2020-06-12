package com.peak.handle.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.peak.common.Constant;
import com.peak.file.FileServiceHelper;
import com.peak.file.IFileService;
import com.peak.handle.IPatchHandle;
import com.peak.thread.MyThreadPool;
import com.peak.util.PathUtil;
import com.peak.util.PropertyUtil;

/**
 * 
 * @author Pan
 *
 */
public class PatchHandleService implements IPatchHandle{
	private static final Logger logger = Logger.getLogger(PatchHandleService.class);
	IFileService iFileService;
	boolean isMaven = false;
	private String[] projects;
	boolean modifyLastUpdateDate;
	Map<String, Boolean> mavenTypeMap = new HashMap<>();
	ExecutorService executor =  MyThreadPool.getInstance().getThreadPool();  
	
	public PatchHandleService() {
		super();
		String projectStrs = PropertyUtil.getValue("project.names");
		projects = StringUtils.split(projectStrs, Constant.SEPARATOR);
		iFileService = FileServiceHelper.getFileService();
		modifyLastUpdateDate = String.valueOf(Boolean.TRUE).equalsIgnoreCase(PropertyUtil.getValue("modify_date"));
	}
	
	@Override
	public void handle() {
		Map<String , Future<Boolean>> map = new HashMap<>(16);
		for(String project : projects) {
			File file = new File(PathUtil.PROJECT_PATH + "/" + project);
			if(!file.exists()) {
				logger.info(String.format("[fail] 项目  %s 不存在", project));
				continue ;
			}
			boolean isMaven = PathUtil.isMavenProject(project);
			// delete old patch
			iFileService.deleteFile(PathUtil.PATCH_DEST + "/" + project);
			
			if(isMaven) {
				if(PathUtil.isMavenWebProject(project)) {
					dealMavenWebProject(file, project, map);
				} else {
					dealMavenProject(file, project, map);
				}
			} else {
				dealNormalProject(file, project, map);
			}
		}
		
		for(String key : map.keySet()) {
			Future<Boolean> task = map.get(key);
			try {
				if(task.get()) {
					logger.info(String.format("文件夹处理完成: %s", key));
				}
			} catch (InterruptedException | ExecutionException e) {
				
				e.printStackTrace();
			}
		}
		
		if(!executor.isShutdown()) {
			// 平缓关闭线程池
			executor.shutdown();
		}
	}

	private void dealMavenProject(File file, String project, Map<String, Future<Boolean>> map) {
		// 用多线程来处理每个project下的根目录下的每个文件夹
		// 处理target文件夹
		String targetFilePath = file.getAbsolutePath() + "/target/classes";
		Callable<Boolean> call = new NormalJavaProjectHandle(project, targetFilePath);
		Future<Boolean> task = executor.submit(call);
		map.put(targetFilePath, task);
		// 处理src/main/java文件夹, 并copy class文件
		String javaSourcePath = file.getAbsolutePath() + "/src/main/java";
		Callable<Boolean> callJava = new NormalJavaProjectHandle(project, javaSourcePath);
		Future<Boolean> taskJava = executor.submit(callJava);
		map.put(javaSourcePath, taskJava);
	}

	private void dealMavenWebProject(File file, String project, Map<String, Future<Boolean>> map) {
		// 用多线程来处理每个project下的根目录下的每个文件夹
		// 处理target文件夹
		String targetFilePath = file.getAbsolutePath() + "/target/" + project;
		Callable<Boolean> call = new NormalJavaProjectHandle(project, targetFilePath);
		Future<Boolean> task = executor.submit(call);
		map.put(targetFilePath, task);
		// 处理src/main/java文件夹, 并copy class文件
		String javaSourcePath = file.getAbsolutePath() + "/src/main/java";
		Callable<Boolean> callJava = new NormalJavaProjectHandle(project, javaSourcePath);
		Future<Boolean> taskJava = executor.submit(callJava);
		map.put(javaSourcePath, taskJava);
	}

	private void dealNormalProject(File file, String project, Map<String, Future<Boolean>> map) {
		for(File item : file.listFiles()) {
			if(checkHanddle(item.getName())) {
				// 用多线程来处理每个project下的根目录下的每个文件夹
				Callable<Boolean> call = new NormalJavaProjectHandle(project, item.getAbsolutePath());
				Future<Boolean> task = executor.submit(call);
				map.put(item.getAbsolutePath(), task);
			}
		}
	}

	private boolean checkHanddle(String name) {
		if(Constant.RootPath.SOURCE_ROOT.equals(name) ||
		   Constant.RootPath.WEB_CONTENT.equals(name) ||
		   Constant.RootPath.MAVEN_SOURCE_ROOT.equals(name) ||
		   Constant.RootPath.WEB_ROOT.equals(name)) { 
			return true;
		}
		return false;
	}
	
}
