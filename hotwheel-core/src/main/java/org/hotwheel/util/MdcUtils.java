package org.hotwheel.util;

import org.hotwheel.assembly.Api;
import org.slf4j.MDC;

/**
 * MDC 计数器
 *
 * @author wangfeng
 * @version 5.4.0
 * @since 2018/4/7
 */
public class MdcUtils {

    /**
     * traceID计数器
     *
     * @param traceName
     * @param isAdd     true 增加, false 减少
     */
    public static String add(String traceName, String traceId, boolean isAdd) {
        String traceCount = traceName + "_count";
        if (Api.isEmpty(traceId)) {
            traceId = MDC.get(traceName);
        }
        String tmpCount = MDC.get(traceCount);
        int num = Api.valueOf(int.class, tmpCount);
        num += (isAdd ? 1 : -1);
        if (num <= 0) {
            if (!Api.isEmpty(MDC.get(traceCount))) {
                MDC.remove(traceCount);
            }
            if (!Api.isEmpty(MDC.get(traceName))) {
                MDC.remove(traceName);
            }
        } else {
            if (Api.isEmpty(traceId)) {
                traceId = TraceId.genTraceId();
            }
            MDC.put(traceName, traceId);
            tmpCount = Api.toString(num);
            MDC.put(traceCount, tmpCount);
        }

        return traceId;
    }
}
