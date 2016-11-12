package org.hotwheel.j2ee.scheduler;

import org.hotwheel.core.BaseContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TriggerContext extends BaseContext implements ServletContextListener {

    private Thread thread = null;
    private TaskBatchThread batchThread = null;
    private String threadName = null;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        batchThread = new TaskBatchThread();
        threadName = batchThread.getClass().getSimpleName();
        thread = new Thread(batchThread, threadName);
        // 此次将User线程变为Daemon线程
        thread.setDaemon(true);
        thread.start();
        logger.info("{} start", threadName);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(thread != null) {
            try {
                logger.info("{} stopping", threadName);
                batchThread.stop();
                thread.join(5 * 1000);
                logger.info("{} stop", threadName);
            } catch (InterruptedException e) {
                logger.error("wait thread finished failed.", e);
            }
        }
    }
}