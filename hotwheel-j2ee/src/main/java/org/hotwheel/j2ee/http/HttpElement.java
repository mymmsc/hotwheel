/**
 * @(#)HttpElement.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.hotwheel.j2ee.http;

import org.hotwheel.j2ee.http.adapter.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP协议元素实体抽象类
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
public abstract class HttpElement implements Element {
    protected HttpServletRequest m_request = null;
    protected HttpServletResponse m_response = null;

    /**
     * AbstractHttpObject构造函数
     */
    protected HttpElement() {
        //
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.mymmsc.j2ee.http.InterfaceHttpObject#init(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    //@SuppressWarnings("unchecked")
    //@Override
    public boolean init(HttpServletRequest request, HttpServletResponse response) {
        m_request = request;
        m_response = response;
        return (m_request != null && m_response != null);
    }

}
