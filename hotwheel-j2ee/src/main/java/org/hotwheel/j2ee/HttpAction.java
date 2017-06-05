/**
 * @(#)HttpAction.java 6.3.12 12/06/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.hotwheel.j2ee;

import javax.servlet.http.HttpServlet;

/**
 * HttpAction 适配器
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.12 12/06/02
 * @see HttpServlet
 * @since mymmsc-j2ee 6.3.9
 */
public abstract class HttpAction extends HttpController implements IAction {

    /* (non-Javadoc)
     * @see org.mymmsc.j2ee.struts.IAction#execute()
     */
    public abstract byte[] execute();
}
