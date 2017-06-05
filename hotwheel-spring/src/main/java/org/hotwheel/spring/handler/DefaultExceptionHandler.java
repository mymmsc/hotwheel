package org.hotwheel.spring.handler;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        attributes.put("code", "1000001");
        attributes.put("msg", ex.getMessage());
        view.setAttributesMap(attributes);
        mv.setView(view);
        logger.error("异常: " + ex.getMessage(), ex);
        return mv;
    }
}
