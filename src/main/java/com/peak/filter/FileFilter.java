package com.peak.filter;

import java.io.File;

public interface FileFilter {
	boolean access(File file);
}
