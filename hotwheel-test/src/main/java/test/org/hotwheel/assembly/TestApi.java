package test.org.hotwheel.assembly;

import org.hotwheel.assembly.Api;

import java.util.Date;

/**
 * 测试基础Api
 *
 * Created by wangfeng on 2017/6/16.
 */
public class TestApi {

    public static void main(String[] args) {
        String dateStr = "2017-06-15T03:12:47.048Z";
        Date date = Api.parseDate(dateStr);
        System.out.println(date);
        dateStr = "2017-06-15 03:12:47.048";
        date = Api.parseDate(dateStr);
        System.out.println(date);
        dateStr = "2017-06-15 03:12:47";
        date = Api.parseDate(dateStr);
        System.out.println(date);
        long tm = date.getTime();
        dateStr = "" + tm;
        System.out.println(dateStr);
        date = Api.parseDate(dateStr);
        System.out.println(date);
    }
}
