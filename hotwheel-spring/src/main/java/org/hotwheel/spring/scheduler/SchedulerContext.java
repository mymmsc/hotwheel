package org.hotwheel.spring.scheduler;

//import org.hotwheel.stock.exchange.context.BaseContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
/*
    protected long writeToFile(Future<DefaultTaskContext> future) {
        long lRet = 0;
        DefaultTaskContext ret = null;
        try {
            ret = future.get();
        } catch (Exception e) {
            logger.error("FJP get failed.", e);
            setTaskException(false);
        }

        if(ret != null && ret.rows != null && ret.file != null) {
            for (String line : ret.rows) {
                //logger.info(line);
                boolean bWrite = BaseContext.writeToFile(ret.file, line);
                if(!bWrite) {
                    setTaskException(false);
                }
            }
            logger.info("{}\t==> {} rows.", ret.taskName, ret.rows.size());
            lRet = ret.rows.size();
        }

        return lRet;
    }
*/
    /**
     * 设置任务开关
     * @param taskSwitch
     */
    public void setTaskSwitch(boolean taskSwitch) {
        //EValue ev = new EValue(taskSwitch);
        //this.taskSwitch = ev.toBoolean();
        this.taskSwitch = taskSwitch;
    }

    public boolean getTaskSwitch() {
        //return String.valueOf(taskSwitch);
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
