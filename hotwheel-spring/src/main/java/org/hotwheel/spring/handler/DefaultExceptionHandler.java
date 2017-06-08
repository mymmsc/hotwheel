package org.hotwheel.spring.handler;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * spring全局异常
 *
 * Created by wangfeng on 2017/3/14.
 * @version 3.2.0
 */
public class DefaultExceptionHandler implements HandlerExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception ex) {
        ModelAndView mv = new ModelAndView();
        // 使用FastJson提供的FastJsonJsonView视图返回, 不需要捕获异常
        FastJsonJsonView view = new FastJsonJsonView();
        Map<String, Object> attributes = new HashMap<String, Object>();
        //"version":null,"status":0,"message":"SUCCES","timestamp":"2017-06-07 13:45:29.633",
        // "host":"100.67.25.169","acrossTime":2745,"data"
        attributes.put("version", "3.2.3");
        attributes.put("status", 99000);
        String message = ex.getMessage();
        if (Api.isEmpty(message)) {
            message = "Unknown error.";
        }
        attributes.put("message", message);
        attributes.put("timestamp", Api.toString(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        attributes.put("host", Api.getLocalIp());
        attributes.put("acrossTime", 1);
        view.setAttributesMap(attributes);
        mv.setView(view);
        logger.error("{}异常: " + message, httpServletRequest.getRequestURI(), ex);
        return mv;
    }
}
