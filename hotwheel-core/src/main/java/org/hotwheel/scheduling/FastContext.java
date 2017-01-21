package org.hotwheel.scheduling;

/**
 * Created by wangfeng on 2017/1/20.
 */
public interface FastContext<T> {

    /**
     * 合并结果集
     *
     * @param context
     * @return
     */
    public boolean merge(T context);
}
