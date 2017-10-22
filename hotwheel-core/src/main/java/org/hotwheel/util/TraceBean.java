package org.hotwheel.util;

import org.hotwheel.assembly.Api;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 跟踪信息
 *
 * Created by wangfeng on 2017/10/22.
 * @version 5.2.15
 */
public class TraceBean {
    private String prefix = null;
    private long sequenceId = 0;
    private long pid = 0;
    private long threadId = 0;

    private long timestamp = 0;

    public TraceBean() {
        sequenceId = 0;
        pid = getProcessId();
        threadId = getThreadId0();
        byte[] ipas = ipToBytesByReg(Api.getLocalIp());
        prefix = Api.MemToHex(ipas) + '-' + pid + '-' + threadId;
    }

    /**
     * 序列号+1
     * @return
     */
    public long incr(long tm) {
        if (timestamp < tm) {
            timestamp = tm;
            sequenceId = 0;
        }
        sequenceId += 1;
        return sequenceId;
    }

    private static long getThreadId0() {
        long threadId = 0;
        try {
            threadId = Thread.currentThread().getId();
        } catch (Exception e) {
            //
        }
        return threadId;
    }

    private static long getProcessId() {
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

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
