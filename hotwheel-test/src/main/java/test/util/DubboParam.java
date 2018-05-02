package test.util;

import java.lang.annotation.*;

/**
 * @author wangfeng
 * @date 2018/4/25
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboParam {
    String name();
    boolean isUse() default true;
    boolean isMust() default true;
    boolean isEmpty() default true;
}
