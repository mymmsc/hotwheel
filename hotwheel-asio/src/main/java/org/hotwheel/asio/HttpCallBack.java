package org.hotwheel.asio;

import java.util.Map;

/**
 * 生成参数map的回调方法
 */
public interface HttpCallBack<T> {

    /**
     * NIO整体完成
     *
     * @param httpClient
     */
    void finished(ScoreBoard httpClient);

    /**
     * 返回以TreeMap组织的post参数
     *
     * @param obj
     * @return
     */
    Map<String, Object> getParams(T obj);

    /**
     * 单个请求完成
     *
     * @param ctx
     */
    void completed(HttpContext ctx);

    /**
     * 失败的请求
     *
     * @param sequeueId
     * @param e
     */
    void failed(int sequeueId, Exception e);
}
