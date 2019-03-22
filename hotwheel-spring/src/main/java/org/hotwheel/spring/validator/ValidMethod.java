package org.hotwheel.spring.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数验证
 * <p>
 * Created by wangfeng on 2017/6/20.
 *
 * @version 5.0.8
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidMethod {
    Class<?>[] value() default {};
}
