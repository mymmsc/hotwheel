package org.hotwheel.spring.util;

import org.hotwheel.assembly.Api;
import org.hotwheel.util.CaseInsensitiveMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * http请求相关的工具类
 *
 * Created by wangfeng on 2017/10/21.
 * @version 5.2.4
 */
public class RequestUtil {

    /** number of milliseconds per second */
    public final static long MSEC_PER_SEC = 1000L;
    public final static String DDL         = "yyyyMMddHHmmss";
    private static volatile long sn = 0;
    private static volatile long timestamp = 0;

    private static final String kPrefixTraceId = Api.getLocalIp();
    private static AtomicLong atomicLong = new AtomicLong(0);
    private static final long kTraceMax = 1000000L;

    /**
     * 试图将计数器重置为0
     * @param tm
     */
    private synchronized static void testInitTraceId(long tm) {
        if (timestamp < tm) {
            timestamp = tm;
            // 以原子方式设置当前值为newValue, 并返回旧值
            atomicLong.getAndSet(0);
        }
    }

    /**
     * 接口请求的跟踪标识
     * @return
     */
    public static String genTraceId() {
        StringBuffer sb = new StringBuffer();
        long tm = System.currentTimeMillis() / MSEC_PER_SEC;
        testInitTraceId(tm);
        Date now = new Date(tm);
        sb.append(kPrefixTraceId).append('/');
        sb.append(Api.toString(now, DDL));
        sn = atomicLong.incrementAndGet();
        String tmp = String.valueOf(kTraceMax + sn);
        sb.append(tmp.substring(1));
        return sb.toString();
    }
    public static Map<String, String> getHeaderMap(HttpServletRequest httpServletRequest) {
        // 通过枚举类型获取请求文件的头部信息集
        Map<String, String> headers = new CaseInsensitiveMap<>();
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        //遍历头部信息集
        while(headerNames.hasMoreElements()){
            //取出信息名
            String name = (String)headerNames.nextElement();
            //取出信息值
            String value = httpServletRequest.getHeader(name);
            headers.put(name, value);
        }
        return headers;
    }

    public static Map<String, String> getHeaderMap(HttpServletResponse httpServletResponse) {
        // 通过枚举类型获取请求文件的头部信息集
        Map<String, String> headers = new CaseInsensitiveMap<>();
        Collection<String> headerNames = httpServletResponse.getHeaderNames();
        //遍历头部信息集
        for (String name : headerNames) {
            //取出信息值
            String value = httpServletResponse.getHeader(name);
            headers.put(name, value);
        }

        return headers;
    }

    /**
     * 获取Client IP 此方法能够穿透squid和proxy
     * @param request
     * @return 真实的ip地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if(ip.indexOf(",") >  0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
