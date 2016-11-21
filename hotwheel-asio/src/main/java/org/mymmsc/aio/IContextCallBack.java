package org.mymmsc.aio;

import java.util.TreeMap;

/**
 * 生成参数map的回调方法
 */
public interface IContextCallBack<T> {

    /**
     * NIO整体完成
     *
     * @param nioHttpClient
     */
    void finished(NioHttpClient nioHttpClient);

    /**
     * 返回以TreeMap组织的post参数
     *
     * @param obj
     * @return
     */
    TreeMap<String ,Object> getParams(T obj);

    /**
     * 单个请求完成
     * @param ctx
     */
    void completed(HttpContext ctx);
}
