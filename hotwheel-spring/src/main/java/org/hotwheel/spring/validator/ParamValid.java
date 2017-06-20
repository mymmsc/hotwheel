package org.hotwheel.spring.validator;

import java.lang.annotation.*;

/**
 * 注解需要验证的参数
 *
 * Created by wangfeng on 2017/6/20.
 * <pre>
 *  @NotNull | 引用类型 | 注解元素必须非空
 *  @Null | 引用类型 |元素为空
 *  @Digits | byte,short,int,long及其包装器,BigDecimal,BigInteger,String| 验证数字是否合法.属性：integer(整数部分), fraction(小数部分)
 *  @Future/@Past| java.util.Date, java.util.Calendar | 是否在当前时间之后或之前
 *  @Max/@Min | byte,short,int,long及其包装器,BigDecimal,BigInteger | 验证值是否小于等于最大指定整数值或大于等于最小指定整数值
 *  @Pattern | String |验证字符串是否匹配指定的正则表达式.属性：regexp(正则), flags（选项,Pattern.Flag值）
 *  @Size | String, Collection, Map,  数组 | 验证元素大小是否在指定范围内.属性:max(最大长度), min(最小长度), message(提示, 默认为{constraint.size})
 *  @DecimalMax/@DecimalMin | byte,short,int,long及其包装器,BigDecimal,BigInteger,String | 验证值是否小于等于最大指定小数值或大于等于最小指定小数值
 *  @Valid | |验证值是否需要递归调用
 *
 *  @Null
 *  @NotNull
 *  @AssertFalse
 *  @AssertTrue
 *  @DecimalMax(value) 不大于value的数值
 *  @DecimalMin(value) 不小于value的数值
 *  @Digits(integer,fraction) 整数部分不超过integer,小数部分不超过fraction
 *  @Future 将来的日期
 *  @Past 过去的日期
 *  @Max(value) 不大于value的数值
 *  @Min(value) 不小于value的数值
 *  @Pattern(value) 满足指定正则表达式
 *  @Size(max,min) 长度在min到max之间
 *
 *  [正则表达式]文本框输入内容控制
 *  整数或者小数：^[0-9]+\.{0,1}[0-9]{0,2}$
 *  只能输入数字："^[0-9]*$".
 *  只能输入n位的数字："^\d{n}$".
 *  只能输入至少n位的数字："^\d{n,}$".
 *  只能输入m~n位的数字：."^\d{m,n}$"
 *  只能输入零和非零开头的数字："^(0|[1-9][0-9]*)$".
 *  只能输入有两位小数的正实数："^[0-9]+(.[0-9]{2})?$".
 *  只能输入有1~3位小数的正实数："^[0-9]+(.[0-9]{1,3})?$".
 *  只能输入非零的正整数："^\+?[1-9][0-9]*$".
 *  只能输入非零的负整数："^\-[1-9][]0-9"*$.
 *  只能输入长度为3的字符："^.{3}$".
 *  只能输入由26个英文字母组成的字符串："^[A-Za-z]+$".
 *  只能输入由26个大写英文字母组成的字符串："^[A-Z]+$".
 *  只能输入由26个小写英文字母组成的字符串："^[a-z]+$".
 *  只能输入由数字和26个英文字母组成的字符串："^[A-Za-z0-9]+$".
 *  只能输入由数字、26个英文字母或者下划线组成的字符串："^\w+$".
 *  验证用户密码："^[a-zA-Z]\w{5,17}$"正确格式为：以字母开头, 长度在6~18之间, 只能包含字符、数字和下划线.
 *  验证是否含有^%&',;=?$\"等字符："[^%&',;=?$\x22]+".
 *  只能输入汉字："^[\u4e00-\u9fa5]{0,}$"
 *  验证Email地址："^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$".
 *  验证InternetURL："^http://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$".
 *  验证电话号码："^(\(\d{3,4}-)|\d{3.4}-)?\d{7,8}$"正确格式为："XXX-XXXXXXX"、"XXXX-XXXXXXXX"、"XXX-XXXXXXX"、"XXX-XXXXXXXX"、"XXXXXXX"和"XXXXXXXX".
 *  验证身份证号（15位或18位数字）："^\d{15}|\d{18}$".
 *  验证一年的12个月："^(0?[1-9]|1[0-2])$"正确格式为："01"～"09"和"1"～"12".
 *  验证一个月的31天："^((0?[1-9])|((1|2)[0-9])|30|31)$"正确格式为；"01"～"09"和"1"～"31".
 *  匹配中文字符的正则表达式： [\u4e00-\u9fa5]
 *  匹配双字节字符(包括汉字在内)：[^\x00-\xff]
 *
 *  下表包含了元字符的完整列表以及它们在正则表达式上下文中的行为：
 *  字符         说明
 *  \           将下一字符标记为特殊字符、文本、反向引用或八进制转义符.例如, “n”匹配字符“n”.“\n”匹配换行符.序列“\\”匹配“\”, “\(”匹配“(”.
 *  ^           匹配输入字符串开始的位置.如果设置了 RegExp 对象的 Multiline 属性, ^ 还会与“\n”或“\r”之后的位置匹配.
 *  $           匹配输入字符串结尾的位置.如果设置了 RegExp 对象的 Multiline 属性, $ 还会与“\n”或“\r”之前的位置匹配.
 *  *           零次或多次匹配前面的字符或子表达式.例如, zo* 匹配“z”和“zoo”.* 等效于 {0,}.
 *  +           一次或多次匹配前面的字符或子表达式.例如, “zo+”与“zo”和“zoo”匹配, 但与“z”不匹配.+ 等效于 {1,}.
 *  ?           零次或一次匹配前面的字符或子表达式.例如, “do(es)?”匹配“do”或“does”中的“do”.? 等效于 {0,1}.
 *  {n}         n 是非负整数.正好匹配 n 次.例如, “o{2}”与“Bob”中的“o”不匹配, 但与“food”中的两个“o”匹配.
 *  {n,}        n 是非负整数.至少匹配 n 次.例如, “o{2,}”不匹配“Bob”中的“o”, 而匹配“foooood”中的所有 o.“o{1,}”等效于“o+”.“o{0,}”等效于“o*”.
 *  {n,m}       M 和 n 是非负整数, 其中 n <= m.匹配至少 n 次, 至多 m 次.例如, “o{1,3}”匹配“fooooood”中的头三个 o.'o{0,1}' 等效于 'o?'.注意：您不能将空格插入逗号和数字之间.
 *  ?           当此字符紧随任何其他限定符（*、+、?、{n}、{n,}、{n,m}）之后时, 匹配模式是“非贪心的”.“非贪心的”模式匹配搜索到的、尽可能短的字符串, 而默认的“贪心的”模式匹配搜索到的、尽可能长的字符串.例如, 在字符串“oooo”中, “o+?”只匹配单个“o”, 而“o+”匹配所有“o”.
 *  .           匹配除"\n"之外的任何单个字符.若要匹配包括“\n”在内的任意字符, 请使用诸如“[\s\S]”之类的模式.
 *  (pattern)   匹配 pattern 并捕获该匹配的子表达式.可以使用 $0…$9 属性从结果“匹配”集合中检索捕获的匹配.若要匹配括号字符 ( ), 请使用“\(”或者“\)”.
 *  (?:pattern) 匹配 pattern 但不捕获该匹配的子表达式, 即它是一个非捕获匹配, 不存储供以后使用的匹配.这对于用“or”字符 (|) 组合模式部件的情况很有用.例如, 'industr(?:y|ies) 是比 'industry|industries' 更经济的表达式.
 *  (?=pattern) 执行正向预测先行搜索的子表达式, 该表达式匹配处于匹配 pattern 的字符串的起始点的字符串.它是一个非捕获匹配, 即不能捕获供以后使用的匹配.例如, 'Windows (?=95|98|NT|2000)' 匹配“Windows 2000”中的“Windows”, 但不匹配“Windows 3.1”中的“Windows”.预测先行不占用字符, 即发生匹配后, 下一匹配的搜索紧随上一匹配之后, 而不是在组成预测先行的字符后.
 *  (?!pattern) 执行反向预测先行搜索的子表达式, 该表达式匹配不处于匹配 pattern 的字符串的起始点的搜索字符串.它是一个非捕获匹配, 即不能捕获供以后使用的匹配.例如, 'Windows (?!95|98|NT|2000)' 匹配“Windows 3.1”中的 “Windows”, 但不匹配“Windows 2000”中的“Windows”.预测先行不占用字符, 即发生匹配后, 下一匹配的搜索紧随上一匹配之后, 而不是在组成预测先行的字符后.
 *  x|y         匹配 x 或 y.例如, 'z|food' 匹配“z”或“food”.'(z|f)ood' 匹配“zood”或“food”.
 *  [xyz]       字符集.匹配包含的任一字符.例如, “[abc]”匹配“plain”中的“a”.
 *  [^xyz]      反向字符集.匹配未包含的任何字符.例如, “[^abc]”匹配“plain”中的“p”.
 *  [a-z]       字符范围.匹配指定范围内的任何字符.例如, “[a-z]”匹配“a”到“z”范围内的任何小写字母.
 *  [^a-z]      反向范围字符.匹配不在指定的范围内的任何字符.例如, “[^a-z]”匹配任何不在“a”到“z”范围内的任何字符.
 *  \b          匹配一个字边界, 即字与空格间的位置.例如, “er\b”匹配“never”中的“er”, 但不匹配“verb”中的“er”.
 *  \B          非字边界匹配.“er\B”匹配“verb”中的“er”, 但不匹配“never”中的“er”.
 *  \cx         匹配 x 指示的控制字符.例如, \cM 匹配 Control-M 或回车符.x 的值必须在 A-Z 或 a-z 之间.如果不是这样, 则假定 c 就是“c”字符本身.
 *  \d          数字字符匹配.等效于 [0-9].
 *  \D          非数字字符匹配.等效于 [^0-9].
 *  \f          换页符匹配.等效于 \x0c 和 \cL.
 *  \n          换行符匹配.等效于 \x0a 和 \cJ.
 *  \r          匹配一个回车符.等效于 \x0d 和 \cM.
 *  \s          匹配任何空白字符, 包括空格、制表符、换页符等.与 [ \f\n\r\t\v] 等效.
 *  \S          匹配任何非空白字符.与 [^ \f\n\r\t\v] 等效.
 *  \t          制表符匹配.与 \x09 和 \cI 等效.
 *  \v          垂直制表符匹配.与 \x0b 和 \cK 等效.
 *  \w          匹配任何字类字符, 包括下划线.与“[A-Za-z0-9_]”等效.
 *  \W          与任何非单词字符匹配.与“[^A-Za-z0-9_]”等效.
 *  \xn         匹配 n, 此处的 n 是一个十六进制转义码.十六进制转义码必须正好是两位数长.例如, “\x41”匹配“A”.“\x041”与“\x04”&“1”等效.允许在正则表达式中使用 ASCII 代码.
 *  \num        匹配 num, 此处的 num 是一个正整数.到捕获匹配的反向引用.例如, “(.)\1”匹配两个连续的相同字符.
 *  \n          标识一个八进制转义码或反向引用.如果 \n 前面至少有 n 个捕获子表达式, 那么 n 是反向引用.否则, 如果 n 是八进制数 (0-7), 那么 n 是八进制转义码.
 *  \nm         标识一个八进制转义码或反向引用.如果 \nm 前面至少有 nm 个捕获子表达式, 那么 nm 是反向引用.如果 \nm 前面至少有 n 个捕获, 则 n 是反向引用, 后面跟有字符 m.如果两种前面的情况都不存在, 则 \nm 匹配八进制值 nm, 其中 n 和 m 是八进制数字 (0-7).
 *  \nml        当 n 是八进制数 (0-3), m 和 l 是八进制数 (0-7) 时, 匹配八进制转义码 nml.
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
