package org.hotwheel.spring.validator;

import java.lang.annotation.*;

/**
 * 参数验证
 *
 * Created by wangfeng on 2017/6/20.
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidMethod {
    Class<?>[] value() default {};
}
