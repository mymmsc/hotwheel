package org.hotwheel.j2ee;

import org.hotwheel.j2ee.annotation.RequestMethod;

import java.lang.reflect.Method;

/**
 * 控制器参数
 *
 * Created by wangfeng on 2016/10/29.
 * @since 2.0.18
 */
public class ActionContext {
    public String uri = null;
    public Class clazz = null;
    public Method method = null;
    public RequestMethod[] requestMethod = null;
    public String[] paramNames = null;
    public boolean responseBody = false;

    public boolean allowed(String methodName) {
        boolean bRet = false;
        if (requestMethod == null || requestMethod.length < 1) {
            bRet = true;
        } else {
            for (RequestMethod rm : requestMethod) {
                if (rm.toString().equalsIgnoreCase(methodName)) {
                    bRet = true;
                    break;
                }
            }
        }

        return bRet;
    }
}
