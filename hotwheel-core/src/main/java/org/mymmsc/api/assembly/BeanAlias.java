/**
 *
 */
package org.mymmsc.api.assembly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * bean成员变量映射别名
 *
 * @author WangFeng
 * @since 2.1.3 允许用逗号间隔多个别名
 */
@Target(ElementType.FIELD) //类，接口或enum
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanAlias {
    String value();
}
