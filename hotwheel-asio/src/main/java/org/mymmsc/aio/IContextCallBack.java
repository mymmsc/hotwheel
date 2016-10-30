package org.mymmsc.aio;

import java.util.TreeMap;

/**
 * 生成参数map的回调方法
 */
public interface IContextCallBack<T> {
    /**
     * post参数
     *
     * @param obj
     * @return
     */
    public TreeMap<String ,Object> getParams(T obj);

    void finishend(HttpContext ctx);
}
