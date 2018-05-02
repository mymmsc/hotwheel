package test.affinity;

import org.hotwheel.jni.affinity.AffinityLock;
import org.hotwheel.jni.affinity.AffinityThreadFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hotwheel.jni.affinity.AffinityStrategies.*;

/**
 * 测试线程池cpu亲和性
 *
 * @author wangfeng
 * @since 2018/4/23
 */
public class AffinityThreadFactoryMain {

    private static final ExecutorService ES = Executors.newFixedThreadPool(4,
            new AffinityThreadFactory("bg", SAME_CORE, DIFFERENT_SOCKET, ANY));

    public static void main(String... args) throws InterruptedException {
        for (int i = 0; i < 8; i++) {
            ES.submit(new Callable<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    Thread.sleep(100);
                    return null;
                }
            });
        }
        Thread.sleep(200);
        System.out.println("\nThe assignment of CPUs is\n" + AffinityLock.dumpLocks());
        ES.shutdown();
        ES.awaitTermination(1, TimeUnit.SECONDS);
    }

}
