package com.lyh.api.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shoushen.luan
 * @since 2022-10-23
 */
public class TestDateUtils {
    public static void main(String[] args) {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("now:" + format.format(date));
        date = DateUtils.addYears(new Date(), 1);
        System.out.println("添加1年:" + format.format(date));
        date = DateUtils.addMonths(date, 1);
        System.out.println("添加1月:" + format.format(date));
        date = DateUtils.addWeeks(date, 1);
        System.out.println("添加1周:" + format.format(date));
        date = DateUtils.addDays(date, 1);
        System.out.println("添加1日:" + format.format(date));
        date = DateUtils.addHours(date, 1);
        System.out.println("添加小时:" + format.format(date));
        date = DateUtils.addMinutes(date, 1);
        System.out.println("添加分钟:" + format.format(date));
        date = DateUtils.addSeconds(date, 5);
        System.out.println("添加5秒:" + format.format(date));
        date = DateUtils.addMilliseconds(date, 5000);
        System.out.println("添加5000毫秒:" + format.format(date));
    }
}
