package com.peak.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Pan
 *
 */
public class DateUtils {
	private static final String NORAML_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static Date parse(String source) {
		SimpleDateFormat sdf = new SimpleDateFormat(NORAML_FORMAT);
		if(StringUtils.isEmpty(source)) {
			return null;
		}
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		String time = "2018-09-11 12:00:00";
		System.out.println(parse(time));
	}
}
