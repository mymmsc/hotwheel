package org.hotwheel.beans.factory.annotation;

/**
 * Created by wangfeng on 2016/11/2.
 * @since 1.0
 */

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    boolean required() default true;
}
