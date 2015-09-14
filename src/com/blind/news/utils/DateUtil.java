package com.blind.news.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	

	public static synchronized String parseDateFormat1(String strDate) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        SimpleDateFormat sdf1=new SimpleDateFormat("MM/dd HH:mm");
        Date d= sdf.parse(strDate);
		return sdf1.format(d);
	}
	
	public static synchronized String parseDateFormat2(String strDate) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1=new SimpleDateFormat("MM/dd HH:mm");
        Date d= sdf.parse(strDate);
		return sdf1.format(d);
	}
}
