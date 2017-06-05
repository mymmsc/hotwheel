package org.hotwheel.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量上下文
 * Created by wangfeng on 2016/10/26.
 * @since 3.1.1
 */
public abstract class PartitionContext<T, V> implements FastContext<V>{
    /** 任务名称 */
    public String taskName = null;
    /** CSV文件名 */
    public File file = null;
    /** 字段列表 */
    public String[] fields = null;
    /** 输出文件行 */
    public List<T> rows = null;

    public PartitionContext() {
        this.rows = new ArrayList<>();
    }
}
