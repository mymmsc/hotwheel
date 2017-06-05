package org.hotwheel.spring.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 耗时监控
 *
 * Created by pasta on 15/11/4.
 * @since 1.0.1 性能监控
 */
public class TraceInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(TraceInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        MDC.put("MDC_START_TIME", String.valueOf(System.currentTimeMillis()));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        String startTime = MDC.get("MDC_START_TIME");
        log.debug("cost time {} ms", System.currentTimeMillis() - Long.parseLong(startTime));
    }
}
