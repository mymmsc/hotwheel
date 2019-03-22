package test.org.hotwheel.assembly;

import org.hotwheel.assembly.Api;
import org.hotwheel.util.TraceId;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试基础Api
 * <p>
 * Created by wangfeng on 2017/6/16.
 */
public class TestApi {
    private final static String TimeFormat = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        String md5 = Api.md5("diancui|7737d392-51d5-4f3d-873b-d557209bdf63|31|807748768458862594|807748768458862594|看看|1513687573|jdb-dsmp-20170522");
        System.out.println("md5: " + md5);
        long tm = System.currentTimeMillis();
        String s = "${host}1${port}2";
        String exp = "\\$\\{([\\s\\S]*?)\\}";
        //exp = "\\$\\{([\\s\\S]*?)\\}[\\s\\S]*?";
        Pattern pat = Pattern.compile(exp);
        Matcher mat = pat.matcher(s);
        int groupCount = 0;
        while (mat.find()) {
            groupCount = mat.groupCount();
            System.out.println(mat.group(1));
        }
        s = TraceId.genTraceId();
        System.out.println(s);
        for (int i = 0; i < 1000000; i++) {
            s = TraceId.genTraceId();
        }
        System.out.println(s);
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
