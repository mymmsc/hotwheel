package org.hotwheel.spring.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.hotwheel.assembly.Api;
import org.hotwheel.spring.util.RequestUtil;
import org.hotwheel.util.MdcUtils;
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
 * <p>
 * Created by pasta on 15/11/4.
 *
 * @version 5.2.3
 * @since 1.0.1 性能监控
 */
public class TraceInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);

    private final static String mdcIp = "MDC_IP";
    private final static String mdcStartTime = "MDC_START_TIME";
    private final static String mdcRequest = "MDC_REQUEST";
    private final static String mdcHeaderRequest = "MDC_HEADER_REQUEST";
    //private final static String mdcHeaderResponse = "MDC_HEADER_RESPONSE";
    // 日志跟踪tradeId的header域名称
    private String httpTraceIdName = null;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        MDC.put(mdcStartTime, String.valueOf(System.currentTimeMillis()));
        String traceId = httpServletRequest.getHeader(httpTraceIdName);
        traceId = MdcUtils.add(httpTraceIdName, traceId, true);
        // 将跟踪码回传给客户端
        httpServletResponse.setHeader(httpTraceIdName, traceId);
        String uri = httpServletRequest.getRequestURI();
        String requestHeader = getHttpHeader(httpServletRequest);
        MDC.put(mdcHeaderRequest, requestHeader);
        String requestParams = getParams(httpServletRequest.getParameterMap());
        MDC.put(mdcRequest, requestParams);
        // 验证IP地址
        String ip = RequestUtil.getClientIp(httpServletRequest);
        MDC.put(mdcIp, ip);
        logger.info("{}={},ip={},url={},request-header=[{}],params=[{}]", httpTraceIdName, traceId, ip, uri, requestHeader, requestParams);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //
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
        if (sb.length() > 0) {
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
        if (sb.length() > 0) {
            sRet += sb.substring(1);
        }
        sRet += "}";
        return sRet;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        String uri = httpServletRequest.getRequestURI();
        String traceId = MDC.get(httpTraceIdName);
        String startTime = MDC.get(mdcStartTime);
        String requestHeader = MDC.get(mdcHeaderRequest);
        String requestParams = MDC.get(mdcRequest);
        String responseHeader = getHttpHeader(httpServletResponse);
        String ip = MDC.get(mdcIp);
        long tm = System.currentTimeMillis() - Api.valueOf(long.class, startTime);
        logger.info("{}={},ip={},url={},request-header=[{}],params=[{}],response-header=[{}], cost time {} ms.",
                httpTraceIdName, traceId, ip, uri, requestHeader, requestParams, responseHeader, tm);

        if (StringUtils.isNotBlank(MDC.get(mdcStartTime))) {
            MDC.remove(mdcStartTime);
        }
        if (StringUtils.isNotBlank(MDC.get(mdcHeaderRequest))) {
            MDC.remove(mdcHeaderRequest);
        }
        if (StringUtils.isNotBlank(MDC.get(mdcRequest))) {
            MDC.remove(mdcRequest);
        }
        MdcUtils.add(httpTraceIdName, traceId, false);
        if (StringUtils.isNotBlank(MDC.get(httpTraceIdName))) {
            MDC.remove(httpTraceIdName);
        }
    }

    private String getHttpHeader(HttpServletRequest httpServletRequest) {
        // 通过枚举类型获取请求文件的头部信息集
        Map<String, Object> headers = new HashMap<String, Object>();
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        //遍历头部信息集
        while (headerNames.hasMoreElements()) {
            //取出信息名
            String name = (String) headerNames.nextElement();
            //取出信息值
            String value = httpServletRequest.getHeader(name);
            headers.put(name, value);
        }

        return getHeaders(headers);
    }

    private String getHttpHeader(HttpServletResponse httpServletResponse) {
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

    public String getHttpTraceIdName() {
        return httpTraceIdName;
    }

    public void setHttpTraceIdName(String httpTraceIdName) {
        this.httpTraceIdName = httpTraceIdName;
    }
}
