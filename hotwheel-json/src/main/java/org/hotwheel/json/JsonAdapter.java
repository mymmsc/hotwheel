/**
 * @(#)JsonAdapter.java 6.3.12 2012/05/11
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.hotwheel.json;

import org.hotwheel.assembly.Api;
import org.hotwheel.json.impl.jackson.JacksonParser;
import org.hotwheel.json.impl.jackson.JsonFactory;
import org.hotwheel.json.impl.jackson.JsonParseException;
import org.hotwheel.json.impl.jackson.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JsonAdapter
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @remark 支持较为复杂的嵌套bean的json解析和输出json串
 * @since mymmsc-api 6.3.9
 */
public class JsonAdapter {
    private static Logger logger = LoggerFactory.getLogger(JsonAdapter.class);
    private JacksonParser parser = null;

    private JsonAdapter(JacksonParser parser, boolean verbose) {
        this.parser = parser;
    }

    public static JsonAdapter parse(String string) {
        return parse(string, false);
    }

    public static JsonAdapter parse(String string, boolean verbose) {
        JsonAdapter ret = null;
        if (!Api.isEmpty(string)) {

            JsonFactory factory = new JsonFactory();
            JacksonParser jp = null;
            try {
                jp = factory.createJsonParser(string);
            } catch (JsonParseException e) {
                logger.error("", e);
            } catch (IOException e) {
                logger.error("", e);
            }
            if (jp != null) {
                ret = new JsonAdapter(jp, verbose);
            }
        }

        return ret;
    }

    /**
     * 判断对象是否泛型
     *
     * @param obj
     * @return
     */
    private static Class<?> getGenericClass(Object obj) {
        Class<?> gcRet = null;
        Class<?> clazz = obj.getClass();
        Type tg = clazz.getGenericSuperclass();
        if (tg != null) {
            try {
                ParameterizedType pType = (ParameterizedType) tg;
                Type[] ts = pType.getActualTypeArguments();
                if (ts != null && ts.length > 0) {
                    gcRet = (Class<?>) ts[0];
                }
            } catch (Exception e) {
                //
            }

        }
        return gcRet;
    }

    /**
     * 获得List的JSON串
     *
     * @param list    java.util.List类对象
     * @param toLower 字段名是否转为小写
     * @return String JSON串
     */
    private static <T> String getList(List<T> list, boolean toLower) {
        String sRet = null;
        if (list != null) {
            StringBuffer buffer = new StringBuffer();
            int count = list.size();
            for (int i = 0; i < count; i++) {
                Object obj = list.get(i);
                String s = get(obj, toLower);
                buffer.append(s);
                if (i + 1 < count) {
                    buffer.append(",");
                }
            }
            sRet = buffer.toString();
        }
        return sRet;
    }

    /**
     * 获得数组的JSON串
     *
     * @param array   数组
     * @param toLower 字段名是否转为小写
     * @return String JSON串
     */
    private static <T> String getArray(Object array, boolean toLower) {
        String sRet = null;
        if (array != null) {
            StringBuffer buffer = new StringBuffer();
            int count = Array.getLength(array);
            for (int i = 0; i < count; i++) {
                Object obj = Array.get(array, i);
                String s = null;
                if (obj == null) {
                    s = "";
                } else if (Api.isBaseType(obj)) {
                    s = Api.toString(obj);
                } else {
                    s = get(obj, toLower);
                }
                if (obj instanceof String) {
                    //s = s.replace("\\", "\\\\");
                    s = "\"" + s.replaceAll("\"", "\\\\\"") + "\"";
                }
                buffer.append(s);
                // buffer.append("{" + s + "}");
                if (i + 1 < count) {
                    buffer.append(",");
                }
            }
            sRet = buffer.toString();
        }
        return sRet;
    }

    /**
     * 获得bean的JSON串
     *
     * @param obj 对象
     * @return String JSON串, 以父类为先, 子类与父类在同一级数, 并且默认字段名为小写.
     */
    public static String get(Object obj) {
        String sRet = "";
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            if (Api.isInterface(clazz, List.class)) {
                sRet = '[' + getList((List<?>) obj, false) + ']';
            } else if (clazz.isArray()) {
                sRet = '[' + getArray(obj, false) + ']';
            } else {
                sRet = get(obj, false);
            }
        }
        return sRet;
    }

    /**
     * JSON字符串特殊字符处理，比如：“\A1;1300”
     * @param s
     * @return String
     */
    private static String string2Json(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            switch (c){
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获得bean的JSON串
     *
     * @param obj     对象
     * @param toLower 是否转换字段名为小写
     * @return String JSON串, 以父类为先, 子类与父类在同一级数
     */
    public static String get(Object obj, boolean toLower) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        // 取得clazz类的成员变量列表
        Class<?> clazz = null;
        Field[] fields = null;
        Field field = null;
        String name = null;
        Object value = null;
        Class<?> cls = null;
        Package clsPackage = null;
        String clsPrefix = null;
        boolean isAccessible = false;
        while (obj != null) {
            if (clazz == null) {
                clazz = obj.getClass();
            } else {
                clazz = clazz.getSuperclass();
            }
            if (clazz.getName().startsWith("java")) {
                break;
            }
            fields = clazz.getDeclaredFields();
            field = null;
            // 遍历所有类成员变量, 为赋值作准备
            String sub = null;
            for (int j = 0; j < fields.length; j++) {
                field = fields[j];
                cls = field.getType();
                name = field.getName().trim();
                if (name.equalsIgnoreCase("serialVersionUID")) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                buffer.append("\"");
                buffer.append(toLower ? name.toLowerCase() : name);
                buffer.append("\":");
                isAccessible = field.isAccessible();
                // print(cls.getName());
                clsPackage = cls.getPackage();
                if (clsPackage == null) {
                    clsPrefix = null;
                } else {
                    clsPrefix = clsPackage.getName();
                }
                // print("clsPrefix = " + clsPrefix);
                try {
                    field.setAccessible(true);
                    value = field.get(obj);
                    if (value == null
                            && (clsPrefix == null || clsPrefix
                            .startsWith("java"))) {
                        value = Api.valueOf(cls, "");
                    }
                    if (cls == java.sql.Date.class
                            || cls == java.util.Date.class) {
                        value = Api.toString(value);
                    }

                    if (value instanceof Object
                            && Api.isInterface(value.getClass(), List.class)) {
                        buffer.append("[");
                        sub = getList((List<?>) value, toLower);
                        if (sub != null) {
                            buffer.append(sub);
                        }
                        buffer.append("]");
                    } else if (cls.isArray() || cls == List.class) {
                        buffer.append("[");
                        sub = getArray(value, toLower);
                        if (sub != null) {
                            buffer.append(sub);
                        }
                        buffer.append("]");
                    } else if (value != null && value.getClass().isArray()) {
                        buffer.append("[");
                        sub = getArray(value, toLower);
                        if (sub != null) {
                            buffer.append(sub);
                        }
                        buffer.append("]");
                    } else if (value instanceof String) {
                        buffer.append("\"");
                        /*
                        String tmpStr = ((String) value).replaceAll("\"",
                                "\\\\\"");
                        buffer.append(tmpStr);
                        */
                        buffer.append(string2Json((String)value));
                        buffer.append("\"");
                    } else if ((!Api.isBaseType(cls)) || !Api.isBaseType(value)) {
                        if (value != null) {
                            sub = get(value, toLower);
                            buffer.append(sub);
                        } else {
                            buffer.append("{}");
                        }
                    } else {
                        buffer.append(value);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("", e);
                } catch (IllegalAccessException e) {
                    logger.error("", e);
                } finally {
                    field.setAccessible(isAccessible);
                }
                if (j + 1 < fields.length) {
                    buffer.append(",");
                } else if (!clazz.getSuperclass().getName().startsWith("java")) {
                    if (clazz.getSuperclass().getDeclaredFields().length > 0) {
                        buffer.append(",");
                    }
                }
            }
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * 解析数组
     *
     * @param <T>   数组的元素类
     * @param clazz 数组元素类
     */
    private <T> List<T> parseList(Class<T> clazz) {
        List<T> list = null;
        try {
            JsonToken token = null;
            String fieldName = parser.getCurrentName();
            if (fieldName == null) {
                parser.nextToken();
            }
            while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
                fieldName = parser.getCurrentName();
                if (token == JsonToken.START_ARRAY) {
                    // 数组开始
                    // list = new ArrayList<T>();
                } else if (token == JsonToken.END_ARRAY) {
                    // 数组结束
                } else if (token == JsonToken.START_OBJECT) {
                    // 新对象开始, 类的子类
                    T obj = get(clazz);
                    if (obj != null) {
                        if (list == null) {
                            list = new ArrayList<T>();
                        }
                        list.add(obj);
                    }
                } else if (token == JsonToken.VALUE_STRING
                        || token.isScalarValue()) {
                    // 新对象开始, 类的子类
                    T obj = Api.valueOf(clazz, parser.getText());
                    if (obj != null) {
                        if (list == null) {
                            list = new ArrayList<T>();
                        }
                        list.add(obj);
                    }
                } else if (token == JsonToken.END_OBJECT) {
                    // 对象结束
                } else if (token == JsonToken.END_ARRAY) {
                    // 数组结束
                } else if (token == JsonToken.FIELD_NAME) {
                    // 字段名
                } else if (fieldName != null) {
                    // Api.setValue(obj, fieldName, fieldValue);
                }
            }
        } catch (JsonParseException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }
        return list;
    }

    public <T> T get(Class<T> clazz) {
        T obj = null;
        if (clazz.isArray()) {
            Class cls = clazz.getComponentType();
            obj = (T) parseList(cls);
        } else {
            obj = get(clazz, null);
        }

        return obj;
    }

    /**
     * 在当前JSON块解析一个对象
     *
     * @param clazz 对象类
     * @param claee 泛型类
     * @return clazz 对象
     */
    //@SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, Class<?> claee) {
        T tRet = null;
        try {
            JsonToken token = null;
            String fieldName = parser.getCurrentName();
            String fieldValue = null;
            Package clsPackage = null;
            String clsPrefix = null;
            if (fieldName == null) {
                parser.nextToken();
            }
            while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {
                if (tRet == null) {
                    try {
                        tRet = clazz.newInstance();
                    } catch (InstantiationException e) {
                        // error("", e);
                    } catch (IllegalAccessException e) {
                        // error("", e);
                    }
                }
                fieldName = parser.getCurrentName();
                if (token == JsonToken.VALUE_NULL) {
                    fieldValue = null;
                } else {
                    fieldValue = parser.getText();
                }
                Class<?> cls = Api.getClass(clazz, fieldName);
                if (cls == null) {
                    continue;
                } else if (cls == List.class) {
                    cls = claee;
                } else if (claee != null && cls == Object.class) {
                    cls = claee;
                }
                if (cls != null) {
                    clsPackage = cls.getPackage();
                } else {
                    clsPackage = null;
                }

                if (clsPackage == null) {
                    clsPrefix = null;
                } else {
                    clsPrefix = clsPackage.getName();
                }
                if (token == JsonToken.START_ARRAY) {
                    if (cls != null) {
                        boolean isArray = false;
                        if (cls.isArray()) {
                            cls = cls.getComponentType();
                            isArray = true;
                        }
                        List<?> list = parseList(cls);
                        if (list == null) {
                            Object arr = null;
                            if (isArray && cls != null) {
                                arr = Array.newInstance(cls, 0);
                            } else {
                                arr = null;
                            }
                            Api.setValue(tRet, fieldName, arr);
                        } else {
                            if (!isArray) {
                                Api.setValue(tRet, fieldName, list);
                            } else {
                                Object arr = null;
                                if (list.get(0).getClass() == cls) {
                                    //@SuppressWarnings("unchecked")
                                    //arr = list.toArray((T[]) Array.newInstance(cls, list.size()));
                                    arr = Array.newInstance(cls, list.size());
                                    for (int i = 0; i < list.size(); i++) {
                                        Array.set(arr, i, list.get(i));
                                    }
                                } else {
                                    arr = Array.newInstance(cls, list.size());
                                    for (int i = 0; i < list.size(); i++) {
                                        Array.set(arr, i, list.get(i));
                                    }
                                }
                                Api.setValue(tRet, fieldName, arr);
                            }
                        }
                    }
                } else if (token == JsonToken.END_ARRAY) {
                    // 数组结束
                } else if (token == JsonToken.START_OBJECT) {
                    if (cls != null) {
                        Object obj = get(cls, claee);
                        if (obj != null) {
                            Api.setValue(tRet, fieldName, obj);
                        }
                    }
                } else if (token == JsonToken.END_OBJECT) {
                    // 对象结束
                } else if (token == JsonToken.END_ARRAY) {
                    // 数组结束
                } else if (token == JsonToken.FIELD_NAME) {
                    // 字段名结束
                } else if (token == null) {
                    // 令牌为空
                    // 令牌为空时,可以继续解析, 如果终止解析,就会忽略之后的对象 [wangfeng@2012-6-8
                    // 下午11:04:17]
                    // break;
                } else if (fieldName != null
                        && (clsPrefix == null || clsPrefix.startsWith("java"))) {
                    // System.out.println(fieldName + " : " + token);
                    Object tmpObj = fieldValue;
                    if (Api.isBaseDataType(cls)) {
                        tmpObj = Api.valueOf(cls, fieldValue);
                    } else if (token == JsonToken.VALUE_STRING) {
                        tmpObj = fieldValue;
                    } else if (token == JsonToken.VALUE_NUMBER_INT) {
                        tmpObj = Api.valueOf(int.class, fieldValue);
                    } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                        tmpObj = Api.valueOf(Float.class, fieldValue);
                    } else if (token == JsonToken.VALUE_TRUE
                            || token == JsonToken.VALUE_FALSE) {
                        tmpObj = Api.valueOf(Boolean.class, fieldValue);
                    } else if(cls instanceof Object && claee != null && claee.isArray()) {
                        Object arr = null;
                        if (fieldValue == null) {
                            arr = Array.newInstance(claee.getComponentType(), 0);
                        }
                        tmpObj = arr;
                    }

                    Api.setValue(tRet, fieldName, tmpObj);
                }
            }
        } catch (JsonParseException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }
        return tRet;
    }

    public void close() {
        try {
            parser.close();
        } catch (IOException e) {
            //
        }
    }
}
