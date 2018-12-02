package com.example.apple.voicetest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by apple on 2018/10/22.
 */

public class DateUtil {

    final static protected String pattern = "yyyy年MM月dd日 HH:mm:ss";
    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @return
     */
    public static String getDateToString(long milSecond) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    /**
     * 将字符串转为时间戳
     * @param dateString
     * @return
     */
    public static long getStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}
