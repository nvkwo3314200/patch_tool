package com.peak.action;

import com.peak.handle.IPatchHandle;
import com.peak.handle.impl.PatchHandleService;

/**
 * @author Pan
 *
 */
public class MainAction {
	
	public static void main(String[] args) {
		IPatchHandle handle = new PatchHandleService();
		handle.handle();
	}
}
