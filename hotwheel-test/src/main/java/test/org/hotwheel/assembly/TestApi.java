package test.org.hotwheel.assembly;

import org.hotwheel.assembly.Api;
import org.hotwheel.util.TraceId;

import java.util.Date;

/**
 * 测试基础Api
 *
 * Created by wangfeng on 2017/6/16.
 */
public class TestApi {
    private final static String TimeFormat = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        long tm = System.currentTimeMillis();
        for (int i = 0; i < 1000; i ++) {
            System.out.println(TraceId.genTraceId());
        }

        System.out.println("crossTime: " + (System.currentTimeMillis() - tm));
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
        long tm1 = date.getTime();
        dateStr = "" + tm1;
        System.out.println(dateStr);
        date = Api.parseDate(dateStr);
        System.out.println(date);
        System.out.println(Api.toString(date, TimeFormat));
    }
}
