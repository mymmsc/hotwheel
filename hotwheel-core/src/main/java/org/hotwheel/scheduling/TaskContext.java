package org.hotwheel.scheduling;

/**
 * 任务上下文
 *
 * Created by wangfeng on 2016/11/14.
 */
public interface TaskContext<T> {

    T getContext();

    /**
     * 执行数据操作
     * @param context
     * @return
     */
    boolean execute(T context);

    /**
     * 合并结果集
     *
     * @param context
     * @return
     */
    //boolean merge(T context);
}
