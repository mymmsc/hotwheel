package org.hotwheel.spring.common;

import org.hotwheel.spring.helper.DESedeHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by YG on 15/7/30.
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
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        String key = null;
        String value = null;

        Iterator itr = props.entrySet().iterator();
        while (itr.hasNext()){
            Map.Entry e = (Map.Entry)itr.next();
            key = (String)e.getKey();
            value = props.getProperty(key);
            logger.debug(key + ": " + value);
            if(key.endsWith("jdbc.username") || key.endsWith("jdbc.password")) {
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

    public static void main(String... strings) {
        String username = "cuishou_rw";
        String password = "cuishouzdxxndt0";
        String hexString = "";

        byte[] out = DESedeHelper.encrypt(username, key);
        hexString = DESedeHelper.parseByte2HexStr(out);
        System.out.println("username.encrypt=[" + hexString + "]");

        out = DESedeHelper.encrypt(password, key);
        hexString = DESedeHelper.parseByte2HexStr(out);
        System.out.println("password.encrypt=[" + hexString + "]");


        String strUsername = "8EB917F28193D3DDCB58A862DA0F74E2";
        byte[] dec = DESedeHelper.decrypt(DESedeHelper.parseHexStr2Byte(strUsername), key);

        System.out.println("uid="+new String(dec));

        String strPassword = "BADA9749B4A2C51807D1C2042C52B46F2FC371E1FC53CA151C9DBDD98835ED43";
        dec = DESedeHelper.decrypt(DESedeHelper.parseHexStr2Byte(strPassword), key);
        System.out.println("pswd="+new String(dec));
    }
}
