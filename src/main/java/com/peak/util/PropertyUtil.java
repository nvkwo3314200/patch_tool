package com.peak.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
	private static Properties prop = null; 
	
	static {
		prop = new Properties();
		try {
			prop.load(PropertyUtil.class.getClassLoader().getResourceAsStream("system.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final String getValue(String key) {
		return prop.getProperty(key);
	}
	
	public static void main(String[] args) {
		System.out.println(PropertyUtil.getValue("file.last_modify_date"));
	}
}