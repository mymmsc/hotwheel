package org.hotwheel.spring.scheduler;

import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 调度上下文, 负责任务控制
 * Created by wangfeng on 2017/1/6.
 * @since 1.0.1
 */
public abstract class SchedulerContext implements TaskContext {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerContext.class);
    /**< 任务 名称 */
    protected String taskName;
    /**< 任务 开始时间 */
    protected String taskStartTime;
    /**< 任务 结束时间 */
    protected String taskEndTime;
    /**< 任务 开关 */
    protected boolean taskSwitch = true;

    protected static volatile boolean isTaskException = false;

    protected void setTaskException(boolean haveException) {
        isTaskException = haveException;
    }

    /**
     * 是否定时器运行周期内
     * @return
     */
    protected boolean isTimerCycle() {
        boolean bRet = false;
        Date date = new Date();
        String now = Api.toString(date, "HH:mm:ss");
        if(now.compareTo(taskStartTime) >= 0 && now.compareTo(taskEndTime) < 0) {
            bRet = true;
        }

        return bRet;
    }

    /**
     * 是否过期
     * @return
     */
    protected boolean isTimeExpire() {
        boolean bRet = false;
        Date date = new Date();
        String now = Api.toString(date, "HH:mm:ss");
        if(now.compareTo(taskEndTime) > 0) {
            bRet = true;
        }

        return bRet;
    }

    /**
     * 业务实现
     */
    protected abstract void service();

    @Override
    public void doTask() {
        // 检查是否运行任务开关
        if (!getTaskSwitch()) {
            return;
        }

        try{
            service();
        } catch (Exception e) {
            logger.error("{} execute failed:", taskEndTime, e);
        }
    }

    /**
     * 设置任务开关
     * @param taskSwitch
     */
    public void setTaskSwitch(boolean taskSwitch) {
        this.taskSwitch = taskSwitch;
    }

    public boolean getTaskSwitch() {
        return taskSwitch;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }
}
