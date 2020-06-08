package com.peak.action;

import com.peak.handle.IPatchHandle;
import com.peak.handle.impl.PatchHandleService;
import com.peak.util.PropertyUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Pan
 *
 */
public class MainAction {

	public static void main(String[] args) throws IOException {
		IPatchHandle handle = new PatchHandleService();
		handle.handle();
		File file=new File(PropertyUtil.getValue("patch.dest"));
		java.awt.Desktop.getDesktop().open(file);
	}
}
