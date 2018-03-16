package org.hotwheel.spring.util;

import org.hotwheel.assembly.Api;
import org.hotwheel.beans.EValue;
import org.hotwheel.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 加载资源文件
 *
 * Created by wangfeng on 2018/1/9.
 * @version 1.0.20
 */
public class PropertiesUtils {
    private final static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private final static String exp = "\\$\\{([\\s\\S]*?)\\}";
    private final static Pattern pattern;
    private final static Map<String, String> mapProperties = new HashMap<>();
    private static PropertiesFactoryBean propertiesFactoryBean;

    static {
        pattern = Pattern.compile(exp);
    }

    public static void loadResource(String confPath) {
        PropertiesFactoryBean pfb = new PropertiesFactoryBean();
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(confPath);
        pfb.setLocation(resource);
        pfb.setSingleton(false);
        Properties otherProperties = null;
        try {
            otherProperties = pfb.getObject();
            CollectionUtils.mergePropertiesIntoMap(otherProperties, mapProperties);
        } catch (IOException e) {
            //logger.error("获取属性文件失败", e);
            throw new ApiException("属性文件[" + confPath + "] 不存在.");
        }
    }

    public static boolean getBoolean(final String key) {
        EValue value = new EValue(getString(key));
        return value.toBoolean();
    }

    public static String getString(final String key) {
        String value = mapProperties.get(key);
        if (value == null) {
            //logger.error("key[{}] 不存在.", key);
            throw new ApiException("读取属性 key[" + key + "] 不存在.");
        } else {
            value = parseValue(value);
            logger.info("key[{}]=[{}]", key, value);
        }
        return value;
    }

    public static <T> T getValue(final String key, Class<T> clazz) {
        String value = getString(key);
        return Api.valueOf(clazz, value);
    }

    public static String parseValue(final String value) {
        String sRet = value;
        try {
            Matcher m = pattern.matcher(value);
            while (m != null && m.find()) {
                if (m.groupCount() > 0) {
                    String subKey = m.group(1);
                    String subValue = mapProperties.get(subKey);
                    if (subValue == null) {
                        //logger.error("sub-key[{}] 不存在.", subKey);
                        throw new ApiException("读取嵌套属性 sub-key[" + subKey + "] 不存在.");
                    } else {
                        subValue = subValue.trim();
                        sRet = Api.contains(sRet, subKey, subValue);
                    }
                }
            }
        } catch (Exception e) {
            //logger.error("推导 配置项[{}]", value, e);
            throw new ApiException("推导 配置项[" + value + "] 错误.");
        }
        return sRet;
    }
}
