package org.hotwheel.exception;

import org.hotwheel.assembly.Api;

/**
 * 业务异常封装
 * Created by wangfeng on 2017/6/7.
 *
 * @version 5.0.3
 */
public class ApiException extends RuntimeException implements ApiError {
    private ApiError apiError = null;

    /**
     * 抛出业务异常
     *
     * @param ae
     */
    public ApiException(ApiError ae) {
        super(String.format("%s(#%d)", ae.getMessage(), ae.getCode()));
        apiError = ae;
    }

    public ApiException(ApiError ae, String message) {
        super(String.format("%s(#%d)", Api.isEmpty(message) ? ae.getMessage() : message, ae.getCode()));
        apiError = ae;

    }

    public ApiException() {
        super();
    }

    public ApiException(String message) {
        super(message);
        apiError = ActionError.SC_EARGUMENT;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    protected ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    @Override
    public int getCode() {
        return apiError.getCode();
    }
}
