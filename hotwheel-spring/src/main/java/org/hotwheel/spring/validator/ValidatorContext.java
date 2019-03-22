package org.hotwheel.spring.validator;

import org.hotwheel.reflect.TypeReference;
import org.springframework.validation.Validator;

/**
 * 验证上下文
 * Created by wangfeng on 2017/6/6.
 *
 * @version 5.0.8
 */
public abstract class ValidatorContext<T extends Object> implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        boolean bRet = false;
        Class theClass = new TypeReference<T>() {
        }.getType().getClass();
        if (theClass == clazz) {
            bRet = true;
        }

        return bRet;
    }
}
