package org.hotwheel.util;

import org.hotwheel.assembly.Api;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
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
    private static final ThreadLocal<Long> kTraceSequence = new ThreadLocal<>();

    private static final String PROCESS_UUID;
    private static final long traceIdMax;
    private static final String kPrefixTraceId;

    static {
        byte[] ipas = ipToBytesByReg(Api.getLocalIp());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        PROCESS_UUID = uuid.substring(uuid.length() - 7);

        kPrefixTraceId = Api.MemToHex(ipas) + '-' + getPid() + '-' + getThreadId();
        int x = String.valueOf(Long.MAX_VALUE).length();
        traceIdMax = (long) Math.pow(10, x);
    }

    private TraceId() {
        //
    }

    private static volatile long sn = 0;
    private static volatile long timestamp = 0;


    private static final long kTraceMax = 1000000L;
    public final static long MSEC_PER_SEC = 1000L;

    /**
     * 得到下一个trace序列
     * @param tm
     */
    private static long nextTraceId(long tm) {
        if (timestamp < tm) {
            timestamp = tm;
            kTraceSequence.set(0L);
        }
        Long seq = kTraceSequence.get();
        seq++;
        kTraceSequence.set(seq);
        return seq;
    }

    /**
     * 接口请求的跟踪标识
     * @return
     */
    public static String genTraceId() {
        StringBuffer sb = new StringBuffer();
        long tm = System.currentTimeMillis() / MSEC_PER_SEC;
        sn = nextTraceId(tm);

        sb.append(kPrefixTraceId).append('-');
        sb.append(tm).append('-');
        String tmp = String.valueOf(kTraceMax + sn);
        sb.append(tmp.substring(1));
        return sb.toString();
    }

    private static long getThreadId() {
        long threadId = 0;
        try {
            threadId = Thread.currentThread().getId();
        } catch (Exception e) {
            //
        }
        return threadId;
    }

    private static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return 0;
        }
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
