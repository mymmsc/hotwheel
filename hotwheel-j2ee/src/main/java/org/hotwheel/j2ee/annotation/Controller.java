package org.hotwheel.j2ee.annotation;

import java.lang.annotation.*;

/**
 * Created by wangfeng on 2016/10/29.
 * @since 6.10.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
