package org.hotwheel.util;

import org.hotwheel.assembly.Api;

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
    private static final String kPrefixTraceId;

    static {
        byte[] ipas = ipToBytesByReg(Api.getLocalIp());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        PROCESS_UUID = uuid.substring(uuid.length() - 7);

        kPrefixTraceId = Api.MemToHex(ipas);
        int x = String.valueOf(Long.MAX_VALUE).length();
        traceIdMax = (long) Math.pow(10, x);
    }

    private TraceId() {
        //
    }

    private static volatile long timestamp = 0;
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
        if (timestamp < tm) {
            timestamp = tm;
            tb.setSequenceId(0);
        }
        tb.increment();
        //kTraceSequence.set(tb);
        long sn = tb.getSequenceId();

        // 第一段, ip地址的16进制
        sb.append(kPrefixTraceId).append('-');
        // 第二段, 进程id
        sb.append(tb.getPid()).append('-');
        // 第三段, 线程id
        sb.append(tb.getThreadId()).append('-');
        // 第四段, 时间戳, 秒
        sb.append(tm).append('-');
        // 第五段, 序列号, 最大999999
        String tmp = String.valueOf(kTraceMax + sn);
        sb.append(tmp.substring(1));
        return sb.toString();
    }

    /**
     * 把IP地址转化为int
     * @param ipAddr
     * @return int
     */
    public static byte[] ipToBytesByReg(String ipAddr) {
        byte[] ret = new byte[4];
        try {
            String[] ipArr = ipAddr.split("\\.");
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
        } catch (Exception e) {
            //throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
        return ret;
    }
}
