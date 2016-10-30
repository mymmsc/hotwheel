package org.mymmsc.j2ee;

import java.lang.reflect.Method;

/**
 * Created by wangfeng on 2016/10/29.
 * @since 6.10.2
 */
public class ActionContext {
    public String uri = null;
    public Class clazz = null;
    public Method method = null;
    public String[] paramNames = null;
    public boolean responseBody = false;
}
