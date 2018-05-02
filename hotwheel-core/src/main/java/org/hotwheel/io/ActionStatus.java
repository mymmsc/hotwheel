package org.hotwheel.io;

import org.hotwheel.assembly.Api;

import java.util.Date;

/**
 * Action响应状态bean
 *
 * @author wangfeng
 * @version 3.0.1 2012/05/27
 * <p> 所有接口的响应都会继承这个类
 */
public class ActionStatus {
    /**< 版本号 */
    private String version;
    /**< 状态码 */
    private int status;
    /**< 状态描述 */
    private String message;
    /**< 时间戳 */
    private String timestamp;
    /**< 主机信息 */
    private String host;
    /**< 耗时毫秒 */
    private long acrossTime;

    public ActionStatus() {
        acrossTime = -1;
        status = 900;
        message = "Unknown error.";
        timestamp = Api.toString(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        host = Api.getLocalIp();
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        //this.message = Api.getLocalIp() + ": " + message;
        this.message = message;
    }

    /**
     * 设定操作状态信息
     *
     * @param status  状态码
     * @param message 状态描述
     */
    public void set(int status, String message) {
        setStatus(status);
        setMessage(message);
        Date t1 = Api.toDate(timestamp, "yyyy-MM-dd HH:mm:ss.SSS");
        Date t2 = new Date();
        acrossTime = t2.getTime() - t1.getTime();
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the acrossTime
     */
    public long getAcrossTime() {
        return acrossTime;
    }

    /**
     * @param acrossTime the acrossTime to set
     */
    public void setAcrossTime(long acrossTime) {
        this.acrossTime = acrossTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
