package org.hotwheel.spring.scheduler;

/**
 * Created by wangfeng on 2017/1/7.
 */
public abstract interface TaskContext {

    /**
     * 定时任务 主方法
     */
    public abstract void doTask();
}
