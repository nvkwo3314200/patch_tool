package com.peak.handle.impl;

import com.peak.handle.AbstractProjectHandle;

/**
 * 
 * @author Pan
 *
 */
public class MavenProjectHandle extends AbstractProjectHandle {

	public MavenProjectHandle(String project, String rootPath) {
		super(project, rootPath);
	}

	@Override
	public void dealFiles(String root) {
		// TODO no handle
	}

}
