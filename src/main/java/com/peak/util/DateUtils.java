package com.peak.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	private static final String NORAML_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	public static Date parse(String source) {
		SimpleDateFormat sdf = new SimpleDateFormat(NORAML_FORMAT);
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
