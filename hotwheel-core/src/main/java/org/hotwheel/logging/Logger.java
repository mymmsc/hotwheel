package org.hotwheel.logging;

import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志记录器
 * 收敛日志输出
 *
 * Created by wangfeng on 2016/11/13.
 * @deprecated 暂不推荐使用
 */
public final class Logger {

    private static ConcurrentHashMap<String, org.slf4j.Logger> mapLoggers = new ConcurrentHashMap<String, org.slf4j.Logger>();

    private static org.slf4j.Logger slf4j(String name) {
        return LoggerFactory.getLogger(name);
    }

    private static org.slf4j.Logger slf4j(Class<?> type) {
        return slf4j(type.getName());
    }

    public static org.slf4j.Logger getLogger() {
        // link http://www.tuicool.com/articles/FnmYNn [wangfeng on 2016/11/16 08:22]
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int current = stackTrace.length -1;
        current = 2;
        String className = stackTrace[current].getClassName();
        return slf4j(className);
    }

    public static org.slf4j.Logger slf4j() {
        // link http://www.tuicool.com/articles/FnmYNn [wangfeng on 2016/11/16 08:22]
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int current = stackTrace.length -1;
        current = 2;
        String className = stackTrace[current].getClassName();
        org.slf4j.Logger logger = mapLoggers.get(className);
        if(logger == null) {
            logger = slf4j(className);
            mapLoggers.put(className, logger);
        }
        return logger;
    }
}
