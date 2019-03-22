package org.hotwheel.spring;

/**
 * 版本控制
 * <p>
 * Created by wangfeng on 2017/6/12.
 *
 * @version 5.0.2
 */
public class HotWheelSpring {
    /**
     * Return the full version string of the present Spring codebase,
     * or {@code null} if it cannot be determined.
     *
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        Package pkg = HotWheelSpring.class.getPackage();
        return (pkg != null ? pkg.getImplementationVersion() : null);
    }
}
