package org.mymmsc.api.assembly;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 资源文件自动加载
 *
 * Created by wangfeng on 2016/10/2.
 * @since v6.10.0.1
 */
public final class ResourceApi {
    private final static long kTimeToLive = 10 * 1000;

    /**
     * 默认10秒自动加载一次文件
     *
     * @param baseName
     * @return
     */
    public static ResourceBundle getBundle(String baseName) {
        return getBundle(baseName, kTimeToLive);
    }

    /**
     * 资源文件自动加载
     * @param baseName 资源文件名
     * @param timeToLieve 自动加载的间隔时间, 单位毫秒
     * @return
     */
    public static ResourceBundle getBundle(String baseName, long timeToLieve) {
        return ResourceBundle.getBundle(baseName, Locale.getDefault(), new ResourceBundleControl(timeToLieve));
    }

    /**
     * 重载控制器，每秒钟重载一次
     */
    private static class ResourceBundleControl extends ResourceBundle.Control {

        private long timeToLive = 1000;

        private ResourceBundleControl(long timeToLive) {
            this.timeToLive = timeToLive;
        }

        /**
         * 每一秒钟检查一次
         */
        @Override
        public long getTimeToLive(String baseName, Locale locale) {
            return timeToLive;
        }

        @Override
        public boolean needsReload(String baseName, Locale locale,
                                   String format, ClassLoader loader,
                                   ResourceBundle bundle, long loadTime) {
            return true;
        }
    }
}
