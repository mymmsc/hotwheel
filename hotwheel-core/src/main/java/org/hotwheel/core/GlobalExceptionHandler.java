package org.hotwheel.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 全局异常捕获
 *
 * Created by wangfeng on 2017/1/18.
 * @since 3.0.1
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error("{}", t.getName(), e);
    }
}
