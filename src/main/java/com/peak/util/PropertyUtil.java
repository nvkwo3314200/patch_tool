package com.peak.util;

import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author Pan
 *
 */
public class PropertyUtil {
	private static final Properties prop;
	
	static {
		prop = new Properties();
		try {
			prop.load(PropertyUtil.class.getClassLoader().getResourceAsStream("system.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getValue(String key) {
		return prop.getProperty(key);
	}
	
	public static void main(String[] args) {
		System.out.println(PropertyUtil.getValue("file.last_modify_date"));
	}
}
