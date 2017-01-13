package org.hotwheel.j2ee.scheduler;

import org.apache.ibatis.session.SqlSession;
import org.hotwheel.beans.factory.annotation.Autowired;
import org.hotwheel.context.ContextLoader;
import org.hotwheel.core.BaseContext;
import org.hotwheel.ibatis.builder.SqlApplicationContext;
import org.mymmsc.api.assembly.Api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时器
 *
 * Created by wangfeng on 16/7/30.
 */
public abstract class ScheduledTimerTask extends BaseContext implements ServletContextListener{
    protected static SqlApplicationContext applicationContext = null;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    /**< 任务 是否正在运行 */
    protected volatile boolean isRunning = false;

    /**< 任务名 */
    protected String taskName = null;
    /**< 任务开始时间 */
    protected String taskStartTime = null;
    /**< 任务停止时间 */
    protected String taskEndTime = null;

    private List<SqlSession> sqlSessionList = null;

    //protected abstract String getFixedTime();
    protected abstract AbstractTask getTask();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        {
            ContextLoader contextLoader = new ContextLoader();
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
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


    public static SqlApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(SqlApplicationContext applicationContext) {
        AbstractTask.applicationContext = applicationContext;
    }

    protected void sessionInitialized() {
        if (applicationContext != null) {
            sqlSessionList = new ArrayList<SqlSession>();
            Class clazz = getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null) {
                    Class<?> contextClass = field.getType();
                    SqlSession session = applicationContext.getSesseion(contextClass);
                    Api.setValue(this, field.getName(), session.getMapper(contextClass));
                    sqlSessionList.add(session);
                }
            }
        }
    }

    protected void sessionDestroyed() {
        if (sqlSessionList != null) {
            for (SqlSession session : sqlSessionList) {
                session.close();
            }
        }
    }
}
