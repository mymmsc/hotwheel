package org.hotwheel.spring.validator;

import java.lang.annotation.*;

/**
 * 注解需要验证的参数
 *
 * Created by wangfeng on 2017/6/20.
 * <pre>
 *     @NotNull | 引用类型 | 注解元素必须非空
 *     @Null | 引用类型 |元素为空
 *     @Digits | byte,short,int,long及其包装器,BigDecimal,BigInteger,String| 验证数字是否合法。属性：integer(整数部分), fraction(小数部分)
 *     @Future/@Past| java.util.Date, java.util.Calendar | 是否在当前时间之后或之前
 *     @Max/@Min | byte,short,int,long及其包装器,BigDecimal,BigInteger | 验证值是否小于等于最大指定整数值或大于等于最小指定整数值
 *     @Pattern | String |验证字符串是否匹配指定的正则表达式。属性：regexp(正则), flags（选项,Pattern.Flag值）
 *     @Size | String, Collection, Map， 数组 | 验证元素大小是否在指定范围内。属性:max(最大长度), min(最小长度), message(提示，默认为{constraint.size})
 *     @DecimalMax/@DecimalMin | byte,short,int,long及其包装器,BigDecimal,BigInteger,String | 验证值是否小于等于最大指定小数值或大于等于最小指定小数值
 *     @Valid | |验证值是否需要递归调用
 *
 *     @Null
 *     @NotNull
 *     @AssertFalse
 *     @AssertTrue
 *     @DecimalMax(value) 不大于value的数值
 *     @DecimalMin(value) 不小于value的数值
 *     @Digits(integer,fraction) 整数部分不超过integer,小数部分不超过fraction
 *     @Future 将来的日期
 *     @Past 过去的日期
 *     @Max(value) 不大于value的数值
 *     @Min(value) 不小于value的数值
 *     @Pattern(value) 满足指定正则表达式
 *     @Size(max,min) 长度在min到max之间
 * </pre>
 * @version 1.0.0
 * @see <url>http://www.cnblogs.com/pixy/p/5306567.html</url>
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamValid {
    //
}
