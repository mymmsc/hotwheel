package org.hotwheel.util;

import java.util.UUID;

/**
 * TraceId生成器
 * JDK 提供的UUID 多线程并发, 会造成重复, 故采用主机ip+进程号+线程号+时间戳+秒内序列号的格式输出
 * Created by wangfeng on 2017/10/22.
 *
 * @version 5.2.13
 */
public final class TraceId {
    // traceId版本号
    private final static String kTraceIdVersion = "10";
    // 线程内序列号
    private static final ThreadLocal<TraceBean> kTraceSequence = new ThreadLocal<>();
    private static final String PROCESS_UUID;
    private static final long traceIdMax;

    static {

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        PROCESS_UUID = uuid.substring(uuid.length() - 7);

        int x = String.valueOf(Long.MAX_VALUE).length();
        traceIdMax = (long) Math.pow(10, x);
    }

    private TraceId() {
        //
    }

    private static final long kTraceMax = 1000000L;
    public final static long MSEC_PER_SEC = 1000L;

    /**
     * 接口请求的跟踪标识
     *
     * @return
     */
    public static String genTraceId() {
        StringBuffer sb = new StringBuffer();
        long tm = System.currentTimeMillis() / MSEC_PER_SEC;

        TraceBean tb = kTraceSequence.get();
        if (tb == null) {
            tb = new TraceBean();
            kTraceSequence.set(tb);
        }
        long sn = tb.incr(tm);
        //kTraceSequence.set(tb);
        // 第一段, ip地址的16进制, 第二段, 进程id, 第三段, 线程id
        sb.append(tb.getPrefix()).append('-');
        // 第四段, 时间戳, 秒
        sb.append(tm).append('-');
        // 第五段, 序列号, 最大999999
        String tmp = String.valueOf(kTraceMax + sn);
        sb.append(tmp.substring(1));
        return sb.toString();
    }

    /**
     * 获取指定位数随机整数
     *
     * @param len
     * @return
     */
    public static String getRandomString(int len) {
        return String.valueOf((long) ((Math.random() * 9 + 1) * Math.pow(10, len - 1)));
    }

    /**
     * 获取traceId
     *
     * @return
     */
    public static String createTraceId() {
        return getRandomString(15);
    }

    /**
     * 接口请求的跟踪标识
     *
     * @return
     */
    public static String genTraceIdV2() {
        return createTraceId();
    }
}
