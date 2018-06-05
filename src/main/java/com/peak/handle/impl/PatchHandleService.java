package com.peak.handle.impl;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.peak.bean.SourceJavaBean;
import com.peak.common.Constant;
import com.peak.file.IFileService;
import com.peak.file.impl.FileServiceImpl;
import com.peak.filter.FileFilter;
import com.peak.filter.NameAccessFileter;
import com.peak.filter.NameFileter;
import com.peak.filter.TimeFileter;
import com.peak.filter.impl.CombineFilter;
import com.peak.handle.IPatchHandle;
import com.peak.util.DateUtils;
import com.peak.util.JavaPathUtil;
import com.peak.util.PathUtil;
import com.peak.util.PropertyUtil;

public class PatchHandleService implements IPatchHandle{
	//private static final Logger logger = LoggerFactory.getLogger(PatchHandleService.class);
	private static final Logger logger = Logger.getLogger(PatchHandleService.class);
	FileFilter filter;
	IFileService iFileService;
	String[] projects;
	String[] patchSources;
	boolean modifyLastUpdateDate;
	ExecutorService executor = Executors.newFixedThreadPool(20);  
	
	public PatchHandleService() {
		super();
		
		Date accessDate = DateUtils.parse(PropertyUtil.getValue("file.last_modify_date"));
		String ingoreSuffix = PropertyUtil.getValue("file.ingore_file_suffixs");
		String accessSuffix = PropertyUtil.getValue("file.access_file_suffixs");
		filter  = new CombineFilter(new TimeFileter(accessDate), 
				new NameFileter(StringUtils.split(ingoreSuffix, Constant.SEPARATOR)),
				new NameAccessFileter(StringUtils.split(accessSuffix, Constant.SEPARATOR)));
		String projectStrs = PropertyUtil.getValue("project.names");
		projects = StringUtils.split(projectStrs, Constant.SEPARATOR);
		patchSources = StringUtils.split(PropertyUtil.getValue("patch.source"), Constant.SEPARATOR);
		iFileService = new FileServiceImpl();
		modifyLastUpdateDate = String.valueOf(Boolean.TRUE).equalsIgnoreCase(PropertyUtil.getValue("modify_date"));
	}
	
	@Override
	public void handle() {
		Map<String , Future<Boolean>> map = new HashMap<>();
		for(String project : projects) {
			File file = new File(PathUtil.projectPath + "/" + project);
			if(!file.exists()) {
				logger.info(String.format("[fail] 项目  s% 不存在", project));
				continue ;
			}
			// delete old patch
			iFileService.deleteFile(PathUtil.patchDest + "/" + project);
			
			for(File item : file.listFiles()) {
				if(checkHanddle(item.getName())) {
					// 用多线程来处理每个project下的根目录下的每个文件夹
					Future<Boolean> task = executor.submit(new FileHanddle(project, item.getAbsolutePath()));
					map.put(item.getAbsolutePath(), task);
				}
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

	private boolean checkHanddle(String name) {
		if(Constant.RootPath.SOURCE_ROOT.equals(name) ||
		   Constant.RootPath.WEB_CONTENT.equals(name) ||
		   Constant.RootPath.WEB_ROOT.equals(name)) return true;
		return false;
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
	
	class FileHanddle implements Callable<Boolean>{
		String project;
		String rootPath;
		
		public FileHanddle(String project, String rootPath) {
			this.project = project;
			this.rootPath = rootPath;
		}
		
		void handelFiles(String path) {
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
					handelFiles(item.getAbsolutePath());
				}
			}
		}

		@Override
		public Boolean call() throws Exception {
			handelFiles(rootPath);
			return true;
		}
	}
	
}
