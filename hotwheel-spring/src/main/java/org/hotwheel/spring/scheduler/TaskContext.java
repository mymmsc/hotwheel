package org.hotwheel.spring.scheduler;

/**
 * 定时任务抽象类
 * <p>
 * Created by wangfeng on 2017/1/7.
 */
public abstract interface TaskContext {

    /**
     * 定时任务 主方法
     */
    public abstract void doTask();
}
