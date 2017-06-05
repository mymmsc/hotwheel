package org.hotwheel.spring.scheduler;

import org.hotwheel.scheduling.PartitionContext;

/**
 * 默认任务
 *
 * Created by wangfeng on 2017/1/21.
 */
public class DefaultTaskContext extends PartitionContext<String, DefaultTaskContext> {
    @Override
    public boolean merge(DefaultTaskContext context) {
        rows.addAll(context.rows);
        return true;
    }
}
