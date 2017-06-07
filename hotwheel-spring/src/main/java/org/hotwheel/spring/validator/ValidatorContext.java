package org.hotwheel.spring.validator;

/**
 * 验证上下文
 * Created by wangfeng on 2017/6/6.
 * @version 3.2.3
 */
public interface ValidatorContext {

    /**
     * 验证
     * @return 验证通过返回true
     * @throws IllegalArgumentException
     */
    public boolean validate() throws IllegalArgumentException;
}
