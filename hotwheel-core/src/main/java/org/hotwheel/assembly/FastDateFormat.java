package org.hotwheel.assembly;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimpleDateFormat安全的时间格式化
 * <p>
 * Created by wangfeng on 2017/10/21.
 *
 * @version 5.2.5
 */
public class FastDateFormat {
    /* The Default Timezone to be used */
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("UTC"); //$NON-NLS-1$
    private static final TimeZone ChinaTimeZone = TimeZone.getTimeZone("Asia/Shanghai"); // ShangHai
    private static final TimeZone DefaultTimeZone = TimeZone.getDefault();
    private static final Locale DefaultLocale = Locale.getDefault();

    private static final ThreadLocal<Map<String, SimpleDateFormat>> cacheContext = new ThreadLocal<>();

    private static Map<String, SimpleDateFormat> getCacheContext() {
        Map<String, SimpleDateFormat> cache = cacheContext.get();
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
            cacheContext.set(cache);
        }
        return cache;
    }

    public static SimpleDateFormat getDateFormat(String format) {
        Map<String, SimpleDateFormat> cache = getCacheContext();

        SimpleDateFormat dateFormat = cache.get(format);
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(format, DefaultLocale);
            dateFormat.setTimeZone(DefaultTimeZone);
            cache.put(format, dateFormat);
        }
        return dateFormat;
    }
}
