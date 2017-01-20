package org.hotwheel.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量上下文
 * Created by wangfeng on 2016/10/26.
 */
public class PartitionContext<T> {
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

    /**
     * 合并结果集
     *
     * @param context
     * @return
     */
    public boolean merge(PartitionContext context) {
        rows.addAll(context.rows);
        return true;
    }
}
