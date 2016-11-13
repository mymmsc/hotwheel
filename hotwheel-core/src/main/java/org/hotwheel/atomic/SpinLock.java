package org.hotwheel.atomic;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 可重入的自旋锁 (重新进入 不会出现死锁)
 *
 * @author wangfeng
 * @date 2016年1月17日 下午6:55:09
 */
public class SpinLock {
    /**
     * 持有自旋锁的线程对象
     */
    private AtomicReference<Thread> owner = new AtomicReference<Thread>();
    /**
     * 用一个计数器 来做 重入锁获取次数的计数
     */
    private int count;

    public void lock() {
        Thread cur = Thread.currentThread();
        if (cur == owner.get()) {
            count++;
            return;
        }

        while (!owner.compareAndSet(null, cur)) {
            // 当线程越来越多 由于while循环会浪费CPU时间片，
            // CompareAndSet需要多次对同一内存进行访问
            // 会造成内存的竞争，然而对于X86，会采取竞争内存总线的方式来访问内存，
            // 所以会造成内存访问速度下降(其他线程老访问缓存)，因而会影响整个系统的性能
        }
    }

    public void unLock() {
        Thread cur = Thread.currentThread();
        if (cur == owner.get()) {
            if (count > 0) {
                count--;
            } else {
                owner.compareAndSet(cur, null);
            }
        }
    }
}
