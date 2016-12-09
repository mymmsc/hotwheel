package org.hotwheel.utils;

import org.mymmsc.api.assembly.BeanAlias;

import java.util.Date;

/**
 * 催收员催记记录情况结果集
 *
 * Created by wangfeng on 16/7/31.
 */
public class RrcCollectionTimes {
    public String taskId;
    public String debtorId;
    public String collectorId;
    public String collectorIdOfRec;
    @BeanAlias("tstart")
    public Date   startTime;
    @BeanAlias("tend")
    public Date   endTime;
    public Date   lastTime;
    public int    stepDays;
}
