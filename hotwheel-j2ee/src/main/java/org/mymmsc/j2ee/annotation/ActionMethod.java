package org.mymmsc.j2ee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口方法许可
 *
 * Created by wangfeng on 16/7/11.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMethod {
    //public enum METHOD {ALL, GET, POST};
    //public METHOD value() default METHOD.ALL;
    public String value() default "ALL";
    public boolean safeSupported() default false;
}
