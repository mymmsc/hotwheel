/**
 * @(#)Category.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.http;

/**
 * 通用WEB工程的条目
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
public final class Category {
    /**
     * log4j配置文件模板文件名
     */
    public final static String Log4jTpl = "log4j.tpl";
    /**
     * log4j配置文件名
     */
    public final static String Log4jFilename = "log4j.xml";
    /**
     * log4j2配置文件名
     */
    public final static String Log4jV2Filename = "log4j2.xml";
    /**
     * 超时
     */
    public final static int SESSION_TIMEOUT = 30 * 60;

    /**
     * 期望的日志存放路径
     */
    public final static String GLOBAL_LOG_PATH = "/data/runtime/logs";

}
