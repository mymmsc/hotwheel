package org.hotwheel.j2ee.scheduler;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.ResourceApi;

import java.util.Date;
import java.util.ResourceBundle;

/**
 * 抽象任务类
 *
 * Created by wangfeng on 16/7/31.
 */
public abstract class AbstractTask extends ScheduledTimerTask implements TaskRunnable{
    private static ResourceBundle rb = null;

    static {
        rb = ResourceApi.getBundle("timer");
    }

    @Override
    public void run() {
        if(isTimerTask()) {
            logger.info("{} start.", taskName);
            try {
                isRunning = true;
                doTask();
                isRunning = false;
            } catch (Exception e) {
                logger.error("{} 失败:", e);
            } finally {
                //
            }

            logger.info("{} end", taskName);
        } else {
            logger.info("本机[{}] 不执行定时任务{}", Api.getLocalIp(), taskName);
        }
    }

    @Override
    protected AbstractTask getTask() {
        return this;
    }

    protected static String getTimerValue(String key){
        String sRet = null;
        try {
            sRet = rb.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sRet;
    }

    /**
     * 是否定时器运行周期内
     * @return
     */
    protected boolean isTimerCycle() {
        boolean bRet = false;
        Date date = new Date();
        String now = Api.toString(date, "HH:mm:ss");
        logger.info("running={}, start={}, end={}", isRunning, taskStartTime, taskEndTime);
        if(isRunning &&  now.compareTo(taskStartTime) >= 0 && now.compareTo(taskEndTime) < 0) {
            bRet = true;
        } else {
            bRet = false;
        }
        logger.info("running={}", bRet);

        return bRet;
    }
}
