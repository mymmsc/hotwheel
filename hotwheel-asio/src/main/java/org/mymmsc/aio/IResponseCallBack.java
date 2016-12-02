package org.mymmsc.aio;

import java.util.Map;

/**
 * Created by wangfeng on 2016/12/2.
 */
public interface IResponseCallBack<T> {

    /**
     * NIO整体完成
     *
     * @param scoreBoard
     */
    void finished(ScoreBoard scoreBoard);

    /**
     * 返回以TreeMap组织的post参数
     *
     * @param obj
     * @return
     */
    Map<String, Object> getParams(T obj);

    /**
     * 单个请求完成
     * @param body
     */
    void completed(String body);

    void failed(Exception e);

    public void cancelled();
}
