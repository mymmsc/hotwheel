package org.hotwheel.j2ee;

import org.hotwheel.context.ContextLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangfeng on 2016/11/13.
 */
public class WebApplicationContext implements ServletContextListener {
    /**
     * The root WebApplicationContext instance that this loader manages.
     */
    private WebApplicationContext context;
    /**
     * The 'current' WebApplicationContext, if the ContextLoader class is
     * deployed in the web app ClassLoader itself.
     */
    private static volatile WebApplicationContext currentContext;
    /**
     * Map from (thread context) ClassLoader to corresponding 'current' WebApplicationContext.
     */
    private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread = new ConcurrentHashMap<ClassLoader, WebApplicationContext>(1);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl == ContextLoader.class.getClassLoader()) {
            currentContext = this.context;
        } else if (ccl != null) {
            currentContextPerThread.put(ccl, this.context);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
