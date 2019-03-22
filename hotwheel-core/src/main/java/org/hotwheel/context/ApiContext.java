/**
 * @(#)ApiContext.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.hotwheel.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.InvalidAttributesException;

/**
 * @author WangFeng(wangfeng @ yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ApiContext {
    private static Logger logger = LoggerFactory.getLogger(ApiContext.class);
    public static final String namePrefixOfJdbc = "java:comp/env/";
    private static boolean m_bInited = false;

    /**
     * ApiContext构造函数
     */
    private synchronized static void init() {
        if (m_bInited) {
            return;
        }
        try {
            Context ctx = new InitialContext();
            ctx.lookup("java:/api/test");
        } catch (NoInitialContextException e) {
            ApiInitialContextFactoryBuilder.initialize();
            m_bInited = true;
        } catch (NamingException e) {
            // 忽略异常
            m_bInited = true;
        }
    }

    @SuppressWarnings("unchecked")
    public static <X> X lookup(String name) throws NamingException {
        init();
        X obj = null;
        try {
            Context ctx = new InitialContext();
            obj = (X) ctx.lookup(name);
        } catch (NoInitialContextException e) {
            logger.error("", e);
        }

        return obj;
    }

    /**
     * 绑定对象
     *
     * @param name 名称
     * @param obj  对象
     * @throws NamingException 如果绑定失败
     */
    public static void bind(String name, Object obj) throws NamingException {
        init();
        try {
            Context ctx = new InitialContext();
            ctx.bind(name, obj);
        } catch (NameAlreadyBoundException e) {
            logger.error("", e);
        } catch (InvalidAttributesException e) {
            logger.error("", e);
        } catch (NoInitialContextException e) {
            logger.error("", e);
        }
    }
}
