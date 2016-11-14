package org.hotwheel.logging;

import org.slf4j.LoggerFactory;

/**
 * 日志记录器
 * 收敛日志输出
 *
 * Created by wangfeng on 2016/11/13.
 */
public final class Logger {

    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static org.slf4j.Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
