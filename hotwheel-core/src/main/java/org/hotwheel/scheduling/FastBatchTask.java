package org.hotwheel.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.concurrent.RecursiveTask;

/**
 * 快速批量任务
 *
 * Created by wangfeng on 2016/11/15.
 * @since 2.0.15
 */
public abstract class FastBatchTask<T extends FastContext> extends RecursiveTask<T> implements TaskContext<T>{
    protected static final String PROP_THRESHOLD = "threshold";
    protected static final String PROP_THREADNUM = "threadNum";
    protected static final String PROP_BATCHSIZE = "batchSize";

    protected static final String[] ALL_PROPERTIES = {
            PROP_THRESHOLD,
            PROP_THREADNUM,
            PROP_BATCHSIZE
    };
    /** 线程数 */
    protected volatile int numberOfThread = 0;

    protected int threadNum = Runtime.getRuntime().availableProcessors();
    protected int threshold = 1000;
    protected int batchSize = 100;

    protected Logger logger =  LoggerFactory.getLogger(getClass());
    protected String taskName = null;
    protected Class<T> contextClass;

    /** 开始行数 */
    protected int start;
    /** 结束行数 */
    protected int end;
    /** 传入参数 */
    protected Object[] args = null;

    /**
     * 创建批量任务
     * @param threadNum
     * @param threshold
     * @param batchSize
     * @param taskName
     */
    public FastBatchTask(int threadNum, int threshold, int batchSize, String taskName) {
        this.threadNum = threadNum;
        this.threshold = threshold;
        this.batchSize = batchSize;
        this.taskName = taskName;
    }

    /**
     * 批量任务构造函数
     *
     * @param start
     * @param end
     * @param args
     */
    public void init(int start, int end, Object... args) {
        this.start = start;
        this.end = end;
        this.args = args;
    }

    private FastBatchTask newTask(int start, int end, Object... args) {
        FastBatchTask task = null;
        Constructor<?> constructor = null;
        try {
            //constructor = getClass().getConstructor(new Class[] {int.class, int.class, Object[].class});
            constructor = getClass().getConstructor(new Class[] {int.class, int.class, int.class, String.class});
            if(constructor != null) {
                task = (FastBatchTask)constructor.newInstance(this.threadNum, this.threshold, this.batchSize, this.taskName);
                task.init(start, end, args);
            }
        } catch (Exception e) {
            logger.error("create task[{}] error", taskName, e);
        }
        return task;
    }

    @Override
    protected T compute() {
        T ret = getContext();
        //ret.taskName = taskName;
        //ret.file = data.file;
        //ret.fields = data.fields;
        numberOfThread++;
        logger.info(taskName + ": " + start + "->" + end + ": 1");
        //如果任务足够小就计算任务
        int remaining = (end - start);
        //boolean canCompute = (end - start) <= threshold;
        if (remaining <= 0) {
            // 不执行
            //return ret;
        } else if(remaining == 1 || remaining <= threshold) {
            logger.info(taskName + ": " + start + "->" + end + ": 2");
            try {
                execute(ret);
            } catch (Exception e) {
                logger.error("{}#excute failed: ", taskName, e);
            }
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;
            FastBatchTask leftTask = newTask(start, middle, args);
            FastBatchTask rightTask = newTask(middle, end, args);
            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待任务执行结束合并其结果
            T leftResult = (T)leftTask.join();
            T rightResult = (T)rightTask.join();

            //合并子任务
            if(leftResult != null) {
                //ret.lines.addAll(leftResult.lines);
                ret.merge(leftResult);
                //merge(leftResult);
            }
            if(rightResult != null) {
                //ret.lines.addAll(rightResult.lines);
                ret.merge(rightResult);
                //merge(rightResult);
            }
            logger.info(taskName + ": " + start + "->" + end + ": 3");
        }
        return ret;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
