package org.hotwheel.spring.context;

import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 初始化spring
 * @since 1.0.0
 */
public class SpringWrapper implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(SpringWrapper.class);

    private static WebApplicationContext springContext;
    private static boolean closed = false;

    public SpringWrapper() {
        super();
    }

    public static ApplicationContext getApplicationContext() {
        return springContext;
    }


    public static <T> T getBean(Class<T> type) throws BeansException {
        return springContext.getBean(type);
    }

    public static Object getBean(String name) throws BeansException {
        return springContext.getBean(name);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        logger.info( "**************init done***************** ");
        closed = false;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        closed = true;
        logger.info( "**************app closing***************** ");
    }

    public static boolean isClosed() {
        return closed;
    }

    public static void setClosed(boolean closed) {
        SpringWrapper.closed = closed;
    }

    private final static String exp = "\\$\\{([\\s\\S]*?)\\}";
    private final static Pattern pattern;
    private final static Map<String, String> mapProperties = new HashMap<>();
    // 配置分离的配置文件路径
    private static String confPath;

    static {
        pattern = Pattern.compile(exp);
        //loadAllResources();
    }

    public static void loadAllResources() {
        PropertiesFactoryBean pfb =  SpringWrapper.getBean(PropertiesFactoryBean.class);
        pfb.setSingleton(false);
        try {
            Properties properties = pfb.getObject();
            if (properties != null && properties.size() > 0) {
                CollectionUtils.mergePropertiesIntoMap(properties, mapProperties);
            }
            // 配置分离文件路径
            confPath = getString("env.conf.path");
            if (Api.isEmpty(confPath)) {
                logger.info("env.conf.path 是空, 不需要额外加载配置信息");
            } else {
                DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
                Resource resource = resourceLoader.getResource(confPath);
                loadResource(resource);
            }
        } catch (IOException e) {
            logger.error("获取属性文件失败", e);
        }
    }

    public static void loadResource(Resource resource) {
        try {
            PropertiesFactoryBean other = new PropertiesFactoryBean();
            other.setLocation(resource);
            other.setSingleton(false);
            Properties otherProperties = other.getObject();
            CollectionUtils.mergePropertiesIntoMap(otherProperties, mapProperties);
        } catch (Exception e) {
            logger.error("获取属性文件失败", e);
        }
    }

    public static String getString(final String key) {
        String value = mapProperties.get(key);
        if (value == null) {
            logger.error("key[{}] 不存在.", key);
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
                        logger.error("sub-key[{}] 不存在.", subKey);
                    } else {
                        subValue = subValue.trim();
                        sRet = Api.contains(sRet, subKey, subValue);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("推到 配置项[{}]", value, e);
        }
        return sRet;
    }
}
