package org.hotwheel.j2ee;

import org.hotwheel.core.BaseContext;

/**
 * 此类虽类名是为Daemon线程，其实为User线程
 *
 * Created by wangfeng on 16/8/11.
 */
public abstract class ServiceRunnable extends BaseContext implements Runnable {

    public final static int TASK_INIT = 0;
    public final static int TASK_RUNNING = 1;
    public final static int TASK_STOPPING = 2;
    public final static int TASK_STOPPED = 3;

    private volatile int status = TASK_INIT;

    protected abstract void service();

    public synchronized void stop() {
        status = TASK_STOPPING;
    }

    public synchronized boolean isRunning() {
        return status < TASK_STOPPING;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void run() {
        status = TASK_RUNNING;
        while (isRunning()) {
            try {
                service();
            } catch (Exception e) {
                logger.error("执行批量异常", e);
            }
        }
        status = TASK_STOPPED;
    }
}
