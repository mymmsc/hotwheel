package org.hotwheel.stock.task;

import org.hotwheel.spring.scheduler.SchedulerContext;
import org.springframework.stereotype.Service;

/**
 * 历史数据
 * Created by wangfeng on 2017/3/10.
 */
@Service("historyDataTask")
public class HistoryDataTask extends SchedulerContext {

    @Override
    protected void service() {

    }
}
