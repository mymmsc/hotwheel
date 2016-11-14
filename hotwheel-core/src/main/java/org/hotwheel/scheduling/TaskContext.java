package org.hotwheel.scheduling;

import java.util.List;

/**
 * 任务上下文
 *
 * Created by wangfeng on 2016/11/14.
 */
public interface TaskContext<T> {

    T getContext();

    /**
     * 执行数据操作
     * @param data
     * @return
     */
    boolean execute(List<String> data);
}
