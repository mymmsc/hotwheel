package org.hotwheel.adapter;

import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 全局的一个对象抽象类
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * <p> 对象增加Log4j日志功能, 需要有common-logging.jar和log4j.jar
 * @since mymmsc-api 6.3.9
 */
public abstract class BaseObject {
    /**
     * log4j适配对象
     */
    protected Logger logger = null;

    /**
     * 自动适配对象
     */
    public BaseObject() {
        init();
    }

    /**
     * 获取资源文件流
     *
     * @param name 资源文件名称
     * @return 返回资源文件路径
     */
    protected InputStream getResource(String name) {
        return getClass().getResourceAsStream(name);
    }

    /**
     * 缓存资源文件
     *
     * @param name 资源文件名称
     * @return 返回资源文件路径
     */
    protected String storeResouce(String name) {
        String sRet = null;
        String tmpPath = Api.getTempDir();
        String pkgName = this.getClass().getPackage().getName().replaceAll("\\.", "/");
        tmpPath += '/' + pkgName;
        Api.mkdirs(tmpPath);
        tmpPath += '/' + name;
        byte[] buff = new byte[4096];
        int len = -1;
        InputStream is = getResource(name);
        File file = new File(tmpPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            while ((len = is.read(buff)) > 0) {
                fos.write(buff, 0, len);
            }
            sRet = tmpPath;
        } catch (IOException e) {
            logger.error("缓存资源文件失败", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return sRet;
    }

    /**
     * 重新初始化配置信息
     */
    private void init() {
        try {
            //Class.forName("org.apache.log4j.PropertyConfigurator");
            //Class.forName("org.apache.log4j.Logger");
            logger = LoggerFactory.getLogger(getClass());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 关闭对象
     */
    public abstract void close();

}
