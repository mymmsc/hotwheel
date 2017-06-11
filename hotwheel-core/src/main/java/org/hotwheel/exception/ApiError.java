package org.hotwheel.exception;

/**
 * 接口类错误接口
 *
 * Created by wangfeng on 2017/6/9.
 * @version 3.2.6
 */
public interface ApiError {

    /**
     * 获得状态码
     * @return
     */
    public int getCode();

    /**
     * 返回错误信息
     * @return
     */
    public String getMessage();
}
