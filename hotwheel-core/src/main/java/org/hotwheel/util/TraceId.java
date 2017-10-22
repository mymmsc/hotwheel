package org.hotwheel.util;

import java.util.UUID;

/**
 * TraceId生成器
 * Created by wangfeng on 2017/10/22.
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
        // 第一段, ip地址的16进制, 第二段, 进程id, 第三段, 线程id
        sb.append(tb.getPrefix()).append('-');
        // 第四段, 时间戳, 秒
        sb.append(tm).append('-');
        // 第五段, 序列号, 最大999999
        String tmp = String.valueOf(kTraceMax + sn);
        sb.append(tmp.substring(1));
        return sb.toString();
    }
}
