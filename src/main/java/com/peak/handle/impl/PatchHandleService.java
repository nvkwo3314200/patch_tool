package com.peak.handle.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

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
			boolean isMaven = checkMaven(project);
			// delete old patch
			iFileService.deleteFile(PathUtil.PATCH_DEST + "/" + project);
			
			if(isMaven) {
				dealMavenProject(file, project, map);
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
		File rootFile = new File(file.getAbsolutePath()+"/" + Constant.MAVEN_SYMBOL_MAIN_ROOT);
		for(File item : rootFile.listFiles()) {
			if(checkHanddle(item.getName())) {
				// 用多线程来处理每个project下的根目录下的每个文件夹
				Callable<Boolean> call = new MavenProjectHandle(project, item.getAbsolutePath());
				//Callable<Boolean> call = new NormalJavaProjectHandle(project, item.getAbsolutePath());
				Future<Boolean> task = executor.submit(call);
				map.put(item.getAbsolutePath(), task);
			}
		}
		
		File webRootFile = new File(file.getAbsolutePath()+"/" + String.format(Constant.MAVEN_SYMBOL_WEB_ROOT, project));
		if(webRootFile.exists()) {
			Callable<Boolean> call = new MavenProjectHandle(project, webRootFile.getAbsolutePath());
			Future<Boolean> task = executor.submit(call);
			map.put(webRootFile.getAbsolutePath(), task);
		}
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

	private boolean checkMaven(String project) {
		File file = new File(PathUtil.PROJECT_PATH + "/" +project + "/" + String.format(Constant.MAVEN_SYMBOL_WEB_ROOT, project));
		if(file.exists()) {
			return true;
		}
		return false;
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
