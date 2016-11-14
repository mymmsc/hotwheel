package org.hotwheel.scheduling;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.ResourceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool批量任务抽象类
 *
 * Created by wangfeng on 2016/10/26.
 */
public abstract class AbstractPartitionTask extends RecursiveTask<BatchContext> {
    protected Logger logger = null;
    protected String taskName = null;
    protected BatchContext data = new BatchContext();

    /** 开始行数 */
    protected int start;
    /** 结束行数 */
    protected int end;
    /** 传入参数 */
    protected Object[] args = null;
    protected Class<?> clazz = this.getClass();

    /** 最大处理的记录数阀值 */
    protected static int threshold = 20000;
    protected static int batchThreadNum = 4;
    /** 最小批量处理的记录数 */
    protected static int kBatchSize = 200;

    /** 线程数 */
    protected volatile int numberOfThread = 0;

    //protected static String env;
    //protected final static String DDF   = "yyyyMMdd";
    //protected static String reportPath;

    //protected static boolean hasLogOutTime = false;

    protected static ResourceBundle rbRuntime = null;

    static {
        refreshConfig();
    }

    public static void refreshConfig() {
        //log.info("refreshConfig...");
        rbRuntime = ResourceApi.getBundle("runtime");
        //env                 = getRuntime("env", String.class);
        //reportPath          = getRuntime("overdue.report.path", String.class);
        threshold           = getRuntime("batch.threshold", int.class);
        batchThreadNum      = getRuntime("batch.threadnum", int.class);
        kBatchSize          = getRuntime("batch.sql.limit", int.class);
        //hasLogOutTime       = getRuntime("batch.outtime", boolean.class);
    }

    private static <T> T getRuntime(String key, Class<T> clazz) {
        String value = rbRuntime.getString(key);
        return Api.valueOf(clazz, value);
    }
    /**
     * 批量任务构造函数
     *
     * @param start
     * @param end
     * @param args
     */
    public AbstractPartitionTask(int start, int end, Object... args) {
        this.start = start;
        this.end = end;
        this.args = args;
        this.clazz = getClass();
        logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * 执行数据操作
     * @param data
     * @return
     */
    protected abstract boolean execute(List<String> data);

    protected AbstractPartitionTask newTask(int start, int end, Object... args) {
        AbstractPartitionTask task = null;
        Constructor<?> constructor = null;
        try {
            constructor = clazz.getConstructor(new Class[] {int.class, int.class, Object[].class});
            if(constructor != null) {
                task = (AbstractPartitionTask)constructor.newInstance(start, end, args);
            }
        } catch (Exception e) {
            logger.error("create task[{}] error", taskName, e);
        }
        return task;
    }

    @Override
    protected BatchContext compute() {
        BatchContext ret = new BatchContext();
        ret.taskName = taskName;
        ret.file = data.file;
        ret.fields = data.fields;
        numberOfThread++;
        logger.info(taskName + ": " + start + "->" + end + ": 1");
        //如果任务足够小就计算任务
        int remaining = (end - start);
        boolean canCompute = (end - start) <= threshold;
        if (remaining < 0) {
            // 不执行
            //return ret;
        } else if(remaining <= threshold) {
            logger.info(taskName + ": " + start + "->" + end + ": 2");
            execute(ret.lines);
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;
            AbstractPartitionTask leftTask = newTask(start, middle, args);
            AbstractPartitionTask rightTask = newTask(middle, end, args);
            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待任务执行结束合并其结果
            BatchContext leftResult = leftTask.join();
            BatchContext rightResult = rightTask.join();

            //合并子任务
            if(leftResult.lines != null) {
                ret.lines.addAll(leftResult.lines);
            }
            if(rightResult.lines != null) {
                ret.lines.addAll(rightResult.lines);
            }
            logger.info(taskName + ": " + start + "->" + end + ": 3");
        }
        return ret;
    }
}
