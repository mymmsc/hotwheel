package org.hotwheel.spring.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 初始化spring
 * @since 1.0.0
 */
@Controller
public class SpringWrapper implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(SpringWrapper.class);

    private static WebApplicationContext springContext;
    private static boolean closed = false;

    public SpringWrapper() {
        super();
    }

    public static ApplicationContext getApplicationContext() {
        return springContext;
    }


    public static <T> T getBean(Class<T> type) throws BeansException {
        return springContext.getBean(type);
    }

    public static Object getBean(String name) throws BeansException {
        return springContext.getBean(name);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        logger.info( "**************init done***************** ");
        closed = false;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        closed = true;
        logger.info( "**************app closing***************** ");
    }

    @PostConstruct
    public void init() {
        logger.info("初始化...");
        closed = false;
    }

    @PreDestroy
    public void close() {
        closed = true;
        logger.info("关闭...");
    }
    public static boolean isClosed() {
        return closed;
    }

    public static void setClosed(boolean closed) {
        SpringWrapper.closed = closed;
    }
}
