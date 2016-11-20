package org.hotwheel.scheduling;

import java.io.Closeable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 *
 * Created by wangfeng on 2016/11/20.
 * @since 2.0.19
 */
public class ThreadPool implements Closeable {
    private ForkJoinPool forkJoinPool = null;
    public final static int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    private int concurrency = 200;

    private int kThreadNum = numberOfProcessors;
    private int kThreshold = 1000;
    private int kBatchSize = 100;

    public ThreadPool(int threadNumber) {
        this.kThreadNum = threadNumber;
        forkJoinPool = new ForkJoinPool(this.kThreadNum * 2);
    }

    @Override
    public void close() {
        if (forkJoinPool != null) {
            try {
                forkJoinPool.shutdown();
                forkJoinPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * Submits a ForkJoinTask for execution.
     */
    public <T> ForkJoinTask<T> submit1(ForkJoinTask<T> task) {
        return forkJoinPool.submit(task);
    }
}
