package org.hotwheel.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 跟踪信息
 *
 * Created by wangfeng on 2017/10/22.
 * @version 5.2.15
 */
public class TraceBean {
    private long sequenceId = 0;
    private long pid = 0;
    private long threadId = 0;

    public TraceBean() {
        sequenceId = 0;
        pid = getProcessId();
        threadId = getThreadId0();
    }

    /**
     * 序列号+1
     * @return
     */
    public long increment() {
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
}
