package org.hotwheel.stereotype;

/**
 * Created by wangfeng on 2016/11/2.
 */

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
