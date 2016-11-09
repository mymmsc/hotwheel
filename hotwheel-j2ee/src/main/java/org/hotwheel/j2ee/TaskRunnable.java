package org.hotwheel.j2ee;

/**
 * Created by wangfeng on 2016/11/10.
 */
public interface TaskRunnable extends Runnable {

    /**
     * 判断本机是否运行定时任务
     * @return
     */
    public abstract boolean isTimerTask();

    public abstract void doTask();
}
