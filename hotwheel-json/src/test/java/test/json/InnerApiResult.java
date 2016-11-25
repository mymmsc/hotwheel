package test.json;

import java.math.BigDecimal;

/**
 * Created by wangfeng on 2016/11/21.
 */
public class InnerApiResult<T> {
    /**< 接口状态 */
    public InnerApiError error;
    /**< 数据区 */
    public T data;
    public BigDecimal a1;
    public T[] b1;
}

