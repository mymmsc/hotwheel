package org.mymmsc.j2ee;

import org.mymmsc.j2ee.annotation.RequestMethod;

import java.lang.reflect.Method;

/**
 * 控制器参数
 *
 * Created by wangfeng on 2016/10/29.
 * @since 6.10.2
 */
public class ActionContext {
    public String uri = null;
    public Class clazz = null;
    public Method method = null;
    public RequestMethod[] requestMethod = null;
    public String[] paramNames = null;
    public boolean responseBody = false;
}
