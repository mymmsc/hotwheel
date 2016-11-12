package org.hotwheel.j2ee.scheduler;

import org.hotwheel.core.BaseContext;
import org.mymmsc.api.assembly.Api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时器
 *
 * Created by wangfeng on 16/7/30.
 */
public abstract class ScheduledTimerTask extends BaseContext implements ServletContextListener{

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    /**< 任务 是否正在运行 */
    protected volatile boolean isRunning = false;

    /**< 任务名 */
    protected String taskName = null;
    /**< 任务开始时间 */
    protected String taskStartTime = null;
    /**< 任务停止时间 */
    protected String taskEndTime = null;


    //protected abstract String getFixedTime();
    protected abstract AbstractTask getTask();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        long msNow = System.currentTimeMillis();
        long msStart = getTimeMillis(taskStartTime);
        long msEnd = getTimeMillis(taskEndTime);
        // 如果开始时间大于结束时间, 则视为定时任务不执行 [wangfeng on 2016/11/10 10:43]
        if(msStart >= msEnd) {
            isRunning = false;
            logger.info("Task[{}] not executed, [{}>{}]", taskName,
                    Api.toString(new Date(msStart), TimeFormat), Api.toString(new Date(msEnd), TimeFormat));
        } else {
            long initDelay = getTimeMillis(taskStartTime) - msNow;
            initDelay = initDelay > 0 ? initDelay : MillisecondsOfDay + initDelay;
            AbstractTask task = getTask();

            executor.scheduleAtFixedRate(
                    task,
                    initDelay,
                    MillisecondsOfDay,
                    TimeUnit.MILLISECONDS);
            isRunning = true;
            logger.info("Task[{}] will be executed at [{}]", taskName, Api.toString(new Date(msNow + initDelay), TimeFormat));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            if(executor != null) {
                executor.shutdown();
            }
        } catch (Exception e) {
            error(e);
        } finally {
            isRunning = false;
        }
    }

    /**
     * 获取指定时间对应的毫秒数
     * @param time "HH:mm:ss"
     * @return
     */
    private long getTimeMillis(String time) {
        long lRet = 0;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            lRet = curDate.getTime();
        } catch (ParseException e) {
            logger.error("", e);
        }
        return lRet;
    }

    protected void error(Exception e) {
        String prefix = String.format("Task[%s]", taskName);
        logger.error(prefix + "关闭定时器失败", e);
    }
}
