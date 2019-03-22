package org.hotwheel.spring.common;

import org.hotwheel.spring.helper.DESedeHelper;
import org.hotwheel.spring.util.PropertiesUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 扩展属性文件加载
 */
public class EncryptablePropertyholder extends PropertyPlaceholderConfigurer {

    private static final String key = "12h4*&^%RTGHJNKLMKHTR^T&YIOJL123k(^&#%$%*&&>NJ$%W#$%^&:?MS%$%";

    private static String decrypt(String str) {
        byte[] dest = null;
        try {
            dest = DESedeHelper.decrypt(DESedeHelper.parseHexStr2Byte(str), key);
        } catch (Exception e) {
            throw new RuntimeException("数据库账号解密失败: ", e);
        }
        return new String(dest);
    }

    @Override
    public void setLocations(Resource... locations) {
        Resource[] newCations = new Resource[locations.length];
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        int i = 0;
        for (Resource resource : locations) {
            String filename = resource.getFilename();
            if (filename.startsWith("${") && filename.endsWith("}")) {
                filename = PropertiesUtils.parseValue(filename);
                resource = resourceLoader.getResource(filename);
            }
            newCations[i++] = resource;
            PropertiesUtils.loadResource(filename);
        }
        super.setLocations(newCations);
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        String key = null;
        String value = null;

        Iterator itr = props.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry e = (Map.Entry) itr.next();
            key = (String) e.getKey();
            value = props.getProperty(key);
            logger.debug(key + ": " + value);
            if (key.endsWith("jdbc.username") || key.endsWith("jdbc.password")) {
                value = decrypt(value);
                props.setProperty(key, value);
                logger.debug("  ==>" + key + ": " + value);
            }
        }

        /*
        //获取配置文件中账号密码信息
        String masterUsername = props.getProperty("master.jdbc.username");
        String masterPassword = props.getProperty("master.jdbc.password");

        //非空校验
        if (StringUtils.isEmpty(masterUsername) || StringUtils.isEmpty(masterPassword)) {
            throw new RuntimeException("数据库配置文件缺少必要参数！");
        }

        //解密
        byte[] masterUsernameByte;
        byte[] masterPasswordByte;
        try {
            masterUsernameByte = DESedeHelper.decrypt(DESedeHelper.parseHexStr2Byte(masterUsername), key);
            masterPasswordByte = DESedeHelper.decrypt(DESedeHelper.parseHexStr2Byte(masterPassword), key);
        } catch (Exception e) {
            throw new RuntimeException("数据库账号密码解密失败：", e);
        }


        props.setProperty("master.jdbc.username",new String(masterUsernameByte));
        props.setProperty("master.jdbc.password",new String(masterPasswordByte));
        */
        super.processProperties(beanFactoryToProcess, props);
    }
}
