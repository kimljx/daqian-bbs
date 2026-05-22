package com.walker;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTest {


    public static void main(String[] args) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        System.out.println("当前时间：" + format.format(calendar.getTime()));
        System.out.println("一秒钟前：" + format.format(getTime1()));
        System.out.println("一分钟前：" + format.format(getTime2()));
        System.out.println("一小时前：" + format.format(getTime3()));
        System.out.println("一天前：" + format.format(getTime4()));
        System.out.println("一周前：" + format.format(getTime5()));
        System.out.println("一月前：" + format.format(getTime6()));
        System.out.println("一年前：" + format.format(getTime7()));
    }

    /**
     *
     * @description: 一秒钟前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime1() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    /**
     *
     * @description: 一分钟前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime2() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        return calendar.getTime();
    }

    /**
     *
     * @description: 一小时前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime3() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        return calendar.getTime();
    }

    /**
     *
     * @description: 一天前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime4() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     *
     * @description: 一周前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime5() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     *
     * @description: 一月前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime6() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     *
     * @description: 一年前
     * @author: Often Record
     * @date: 2023年02月24日
     * @return
     */
    private static Date getTime7() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

}
