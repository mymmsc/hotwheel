package org.hotwheel.spring.common;

import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 耗时监控
 *
 * Created by pasta on 15/11/4.
 * @since 1.0.1 性能监控
 */
public class TraceInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);
    private final static String mdcIp = "MDC_IP";
    private final static String mdcStartTime = "MDC_START_TIME";
    private final static String mdcRequest = "MDC_REQUEST";
    private final static String mdcHeaderRequest = "MDC_HEADER_REQUEST";
    private final static String mdcHeaderResponse = "MDC_HEADER_RESPONSE";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        MDC.put(mdcStartTime, String.valueOf(System.currentTimeMillis()));
        String uri = httpServletRequest.getRequestURI();
        String requestHeader = getHeader(httpServletRequest);
        MDC.put(mdcHeaderRequest, requestHeader);
        String requestParams = getParams(httpServletRequest.getParameterMap());
        MDC.put(mdcRequest, requestParams);
        // 验证IP地址
        String ip = null;
        String xip = httpServletRequest.getHeader("X-Forwarded-For");
        if(xip == null) {
            ip = httpServletRequest.getRemoteAddr();
        } else {
            ip = xip;
        }
        MDC.put(mdcIp, ip);
        logger.info("ip={},url={},request-header=[{}],params=[{}]", ip, uri, requestHeader, requestParams);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public static String getHeaders(Map<String, Object> params) {
        String sRet = "{";
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = Api.toString(entry.getValue());
            String str = String.format(",%s=%s", key, value);
            sb.append(str);
        }
        if(sb.length() > 0) {
            sRet += sb.substring(1);
        }
        sRet += "}";
        return sRet;
    }

    public static String getParams(Map<String, String[]> params) {
        String sRet = "{";
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String value = values != null && values.length > 0 ? values[0] : "N/A";
            String str = String.format("&%s=%s", key, value);
            sb.append(str);
        }
        if(sb.length() > 0) {
            sRet += sb.substring(1);
        }
        sRet += "}";
        return sRet;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        String uri = httpServletRequest.getRequestURI();
        String startTime = MDC.get(mdcStartTime);
        String requestHeader = MDC.get(mdcHeaderRequest);
        String requestParams = MDC.get(mdcRequest);
        String responseHeader = getHeader(httpServletResponse);
        String ip = MDC.get(mdcIp);
        long tm = System.currentTimeMillis() - Api.valueOf(long.class, startTime);
        logger.info("ip={},url={},request-header=[{}],params=[{}],response-header=[{}], cost time {} ms.",
                ip,uri, requestHeader, requestParams, responseHeader, tm);
    }

    private String getHeader(HttpServletRequest httpServletRequest) {
        // 通过枚举类型获取请求文件的头部信息集
        Map<String, Object> headers = new HashMap<String, Object>();
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        //遍历头部信息集
        while(headerNames.hasMoreElements()){
            //取出信息名
            String name = (String)headerNames.nextElement();
            //取出信息值
            String value = httpServletRequest.getHeader(name);
            headers.put(name, value);
        }

        return getHeaders(headers);
    }

    private String getHeader(HttpServletResponse httpServletResponse) {
        // 通过枚举类型获取请求文件的头部信息集
        Map<String, Object> headers = new HashMap<String, Object>();
        Collection<String> headerNames = httpServletResponse.getHeaderNames();
        //遍历头部信息集
        for (String name : headerNames) {
            //取出信息值
            String value = httpServletResponse.getHeader(name);
            headers.put(name, value);
        }

        return getHeaders(headers);
    }
}
