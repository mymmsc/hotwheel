package org.hotwheel.spring.databinder.impl;

import org.apache.commons.lang3.StringUtils;
import org.hotwheel.assembly.Api;
import org.hotwheel.assembly.BeanAlias;
import org.hotwheel.spring.databinder.ExtendDataBinder;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestDataBinder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Bryant Hang
 * Date: 15/1/17
 * Time: 下午6:34
 */
public class DefaultExtendDataBinder implements ExtendDataBinder {

    //Rename cache
    private final Map<Class<?>, Map<String, String>> replaceMap = new ConcurrentHashMap<>();

    /**
     * 获取字段别名
     *
     * @param field
     * @return
     */
    private static String getFieldAlias(Field field) {
        String sRet = "";
        Annotation[] anns = null;
        Annotation ann = null;
        anns = field.getDeclaredAnnotations();
        if (anns.length > 0) {
            for (int k = 0; k < anns.length; k++) {
                ann = anns[k];
                if (ann instanceof BeanAlias) {
                    BeanAlias ba = (BeanAlias) ann;
                    if (ba != null) {
                        sRet = ba.value();
                    }
                }
            }
        }

        return sRet;
    }

    /**
     * 判断字段名是否匹配, 包括注解别名
     *
     * @param field
     * @param name
     * @return
     * @since 2.1.3
     */
    public static boolean fieldMatch(final Field field, final String name) {
        boolean bFieldMatch = false;
        boolean bAliasMatch = false;
        if (field != null && !Api.isEmpty(name)) {
            bFieldMatch = field.getName().equalsIgnoreCase(name);
            if (!bFieldMatch) {
                final String fieldAlias = getFieldAlias(field);
                if (!Api.isEmpty(fieldAlias)) {
                    String[] listAlias = fieldAlias.split(",");
                    for (String alias : listAlias) {
                        if (!Api.isEmpty(alias) && alias.equalsIgnoreCase(name)) {
                            bAliasMatch = true;
                            break;
                        }
                    }
                }
            }
        }
        return (bFieldMatch || bAliasMatch);
    }

    /**
     * 获得类成员变量的类
     *
     * @param clazz
     * @param mpvs
     * @return Class
     */
    public static void dataBind(Class<?> clazz, MutablePropertyValues mpvs) {
        // 取得clazz类的成员变量列表
        Field[] fields = clazz.getDeclaredFields();
        Field field = null;
        String aliasName = null;
        String paramName = null;
        List<PropertyValue> tmpList = mpvs.getPropertyValueList();
        for (int i = 0; tmpList != null && i < tmpList.size(); i++) {
            PropertyValue pv = tmpList.get(i);
            if (pv == null) continue;
            paramName = pv.getName();
            Object value = pv.getValue();
            // 遍历所有类成员变量, 为赋值作准备
            for (int j = 0; j < fields.length; j++) {
                field = fields[j];
                // 忽略字段名大小写
                if (fieldMatch(field, paramName)) {
                    // 得到类成员变量数据类型
                    mpvs.add(field.getName(), value);
                }
            }
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.getName().startsWith("java")) {
                dataBind(superClass, mpvs);
            }
        }
    }

    /**
     * 扩展bind，这里实现的是对parameter中的name别名转换以及low_underscore到camel的转化
     *
     * @param mpvs
     * @param dataBinder
     */
    @Override
    public void doExtendBind(MutablePropertyValues mpvs, ServletRequestDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        Class<?> targetClass = target.getClass();
        if (!replaceMap.containsKey(targetClass)) {
            Map<String, String> mapping = analyzeClass(targetClass);
            replaceMap.put(targetClass, mapping);
        }
        Map<String, String> mapping = replaceMap.get(targetClass);

        dataBind(targetClass, mpvs);
    }

    private static Map<String, String> analyzeClass(Class<?> targetClass) {
        Field[] fields = targetClass.getDeclaredFields();
        Map<String, String> renameMap = new HashMap<String, String>();
        for (Field field : fields) {
            BeanAlias ann = field.getAnnotation(BeanAlias.class);
            if (ann != null && StringUtils.isNotBlank(ann.value())) {
                renameMap.put(ann.value(), field.getName());
            }
        }
        //if (renameMap.isEmpty()) return ConcurrentHashMap<String, String>Collections.emptyMap();
        return renameMap;
    }
}
