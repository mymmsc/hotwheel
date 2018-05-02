package org.hotwheel.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用父类, 集合日志等其它功能之用
 * Created by wangfeng on 2016/11/10.
 */
public abstract class BaseContext {
    /** 日志记录器 */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** number of milliseconds per second */
    public final static long MSEC_PER_SEC = 1000L;
    /** 一天的秒数 */
    public final static long SecondOfDay = 24L * 60L * 60L;

    /** 一天的毫秒数 */
    public final static long MillisecondsOfDay = SecondOfDay * MSEC_PER_SEC;

    /** 时间格式 */
    public final static String TimeFormat = "yyyy-MM-dd HH:mm:ss";
    /** 日期格式 */
    public final static String DateFormat = "yyyy-MM-dd";


    /**
     * 获得系统环境变量
     * @param key 环境变量名称
     * @return 环境变量值
     */
    public String getSystemEnv(String key) {
        String sRet = null;
        try {
            sRet = System.getenv(key);
        } catch (Exception e) {
            //
        }

        return sRet;
    }
}
