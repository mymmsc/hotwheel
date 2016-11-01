package org.hotwheel.stereotype;

import java.lang.annotation.*;

/**
 * Created by wangfeng on 2016/11/2.
 *
 * @since 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
    String value() default "";
}
