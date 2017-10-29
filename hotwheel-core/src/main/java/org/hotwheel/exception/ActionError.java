package org.hotwheel.exception;

/**
 * 错误码标准化
 *
 * Created by wangfeng on 2017/5/10.
 * @version 1.0.0
 */
public enum ActionError implements ApiError {
    SC_SUCCESS     (0,     "SUCCESS"),
    SC_ENOTFOUND   (10000, "http协议请求路径错误"),
    SC_EMETHOD     (10001, "http协议请求方法错误"),
    SC_EARGUMENT   (10002, "请求参数错误"),
    SC_EVALIDATE   (10003, "参数验证异常"),
    SC_EIP         (10010, "非法IP地址"),
    SC_EAPPID1     (10011, "验签appId为空"),
    SC_EAPPID2     (10012, "验签appId非法"),
    SC_ETIMESTAMP1 (10021, "验签ts为空"),
    SC_ETIMESTAMP2 (10022, "验签ts未通过"),
    SC_ESIGN       (10021, "验签sign未通过"),
    SC_ENETWORK    (70000, "一般性网络错误"),
    SC_EREFUSE     (70001, "服务器拒绝连接"),
    SC_ECLOSED     (70002, "服务器主动关闭连接"),
    SC_EUNKNOWN    (99999, "未知错误");

    private final int code;
    private final String message;

    ActionError(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
