package test.org.hotwheel.assembly;

import org.hotwheel.assembly.Api;

import java.util.Date;

/**
 * 测试基础Api
 *
 * Created by wangfeng on 2017/6/16.
 */
public class TestApi {
    private final static String TimeFormat = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        String dateStr = "2017-06-15T03:12:47.048Z";
        Date date = Api.parseDate(dateStr);
        System.out.println(date);
        System.out.println(Api.toString(date, TimeFormat));
        dateStr = "2017-06-15 03:12:47.048";
        date = Api.parseDate(dateStr);
        System.out.println(date);
        System.out.println(Api.toString(date, TimeFormat));
        dateStr = "2017-06-15 03:12:47";
        date = Api.parseDate(dateStr);
        System.out.println(date);
        System.out.println(Api.toString(date, TimeFormat));
        long tm = date.getTime();
        dateStr = "" + tm;
        System.out.println(dateStr);
        date = Api.parseDate(dateStr);
        System.out.println(date);
        System.out.println(Api.toString(date, TimeFormat));
    }
}
