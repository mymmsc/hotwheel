/**
 *
 */
package org.hotwheel.io;

import org.hotwheel.assembly.Api;

import java.util.Date;

/**
 * HttpClient类post方法返回的对象
 *
 * @author wangfeng
 * @remark 默认错误码为404, 如果接口异常, status为900, error为异常消息内容
 */
public class HttpResult {
    /**
     * 错误码
     */
    private int status = 404;
    /**
     * 类型
     */
    private String type = null;
    /**
     * 日期, 如果是下载动作, 此处保留文件的时间
     */
    private Date date = null;
    /**
     * 错误内容
     */
    private String error = null;
    /**
     * 二进制数据
     */
    private byte[] data = null;
    /**
     * 文本内容
     */
    private String body = null;

    private String timestamp;

    private String cookies = "";

    /**
     * 耗时
     */
    private long acrossTime;

    public HttpResult() {
        acrossTime = -1;
        status = 900;
        timestamp = Api.toString(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public void finished() {
        Date t1 = Api.toDate(timestamp, "yyyy-MM-dd HH:mm:ss.SSS");
        Date t2 = new Date();
        acrossTime = t2.getTime() - t1.getTime();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @SuppressWarnings("deprecation")
    public String date() {
        String timeStr = String.format("%d-%02d-%02d %02d:%02d:%02d",
                1900 + date.getYear(), date.getMonth() + 1, date.getDate(),
                date.getHours(), date.getMinutes(), date.getSeconds());
        return timeStr;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public long getAcrossTime() {
        return acrossTime;
    }

    public void setAcrossTime(long acrossTime) {
        this.acrossTime = acrossTime;
    }
/*
    String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
    SimpleDateFormat dateFormat = new SimpleDateFormat(RFC1123_DATE_PATTERN);

    Date date = new Date(1234);
    String str = dateFormat.format(date);
    Date date2 = dateFormat.parse(str);

    System.out.println("date="+date+"; "+date.getTime());
    System.out.println("str="+str);
    System.out.println("date2="+date2+"; "+date2.getTime());
	*/

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }
}
