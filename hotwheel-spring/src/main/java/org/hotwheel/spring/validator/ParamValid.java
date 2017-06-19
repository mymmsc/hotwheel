package org.hotwheel.spring.validator;

import java.lang.annotation.*;

/**
 * 注解需要验证的参数
 *
 * Created by wangfeng on 2017/6/20.
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamValid {
}
