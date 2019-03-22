package org.hotwheel.spring.util;

import org.hotwheel.util.CaseInsensitiveMap;
import org.hotwheel.util.TraceId;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

/**
 * http请求相关的工具类
 * <p>
 * Created by wangfeng on 2017/10/21.
 *
 * @version 5.2.4
 */
public class RequestUtil {

    public static Map<String, String> getHeaderMap(HttpServletRequest httpServletRequest) {
        // 通过枚举类型获取请求文件的头部信息集
        Map<String, String> headers = new CaseInsensitiveMap<>();
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        //遍历头部信息集
        while (headerNames.hasMoreElements()) {
            //取出信息名
            String name = (String) headerNames.nextElement();
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
     *
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
        if (ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    public static String genTraceId() {
        return TraceId.genTraceId();
    }
}
