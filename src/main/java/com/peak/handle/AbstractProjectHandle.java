package com.peak.handle;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.peak.common.Constant;
import com.peak.file.FileServiceHelper;
import com.peak.file.IFileService;
import com.peak.filter.FileFilter;
import com.peak.filter.FolderIgnore;
import com.peak.filter.NameAccessFileter;
import com.peak.filter.NameFileter;
import com.peak.filter.TimeFileter;
import com.peak.filter.impl.CombineFilter;
import com.peak.util.DateUtils;
import com.peak.util.PropertyUtil;

public abstract class AbstractProjectHandle implements IProjectHanddle, Callable<Boolean> {
	protected final String project;
	private final String rootPath;
	protected final String[] patchSources;
	protected final String[] projects;
	protected final IFileService iFileService = FileServiceHelper.getFileService();
	protected final boolean modifyLastUpdateDate;
	protected final FileFilter filter;
	
	public AbstractProjectHandle(String project, String rootPath) {
		this.project = project;
		this.rootPath = rootPath;
		String projectStrs = PropertyUtil.getValue("project.names");
		projects = StringUtils.split(projectStrs, Constant.SEPARATOR);
		patchSources = StringUtils.split(PropertyUtil.getValue("patch.source"), Constant.SEPARATOR);
		modifyLastUpdateDate = String.valueOf(Boolean.TRUE).equalsIgnoreCase(PropertyUtil.getValue("modify_date"));
		
		// filter init 
		Date accessDate = DateUtils.parse(PropertyUtil.getValue("file.last_modify_date"));
		Date limitDate = DateUtils.parse(PropertyUtil.getValue("file.last_modify_date_max"));
		String ingoreSuffix = PropertyUtil.getValue("file.ingore_file_suffixs");
		String accessSuffix = PropertyUtil.getValue("file.access_file_suffixs");
		String folderIngore = PropertyUtil.getValue("forder.ingore");
		filter  = new CombineFilter(new TimeFileter(accessDate, limitDate), 
				new NameFileter(StringUtils.split(ingoreSuffix, Constant.SEPARATOR)),
				new NameAccessFileter(StringUtils.split(accessSuffix, Constant.SEPARATOR)),
				new FolderIgnore(StringUtils.split(folderIngore, Constant.SEPARATOR)));
	}
	
	@Override
	public Boolean call(){
		dealFiles(rootPath);
		return true;
	}

}
