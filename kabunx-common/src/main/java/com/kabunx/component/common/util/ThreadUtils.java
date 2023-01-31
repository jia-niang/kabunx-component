package com.kabunx.component.common.util;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadUtils {

    public static void sleepMillis(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException ex) {
            log.error("[ThreadUtils] 线程中断标记被移除", ex);
        }
    }

    public static void sleepSeconds(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException ex) {
            log.error("[ThreadUtils] 线程中断标记被移除", ex);
        }
    }

    /**
     * 获取当前线程
     */
    public static Thread getCurrentThread() {
        return Thread.currentThread();
    }

    /**
     * 获取当前线程名称
     */
    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 获取当前线程ID
     */
    public static long getCurrentThreadId() {
        return Thread.currentThread().getId();
    }

    /**
     * CPU核数
     **/
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 空闲保活时限，单位秒
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 有界队列size
     */
    private static final int QUEUE_SIZE = 10000;


    /**
     * 获取执行CPU密集型任务的线程池
     */
    public static ExecutorService getCpuThreadPoolExecutor() {
        return TtlExecutors.getTtlExecutorService(CpuThreadPoolLazyHolder.EXECUTOR);
    }

    private static class CpuThreadPoolLazyHolder {
        static int corePoolSize = (int) (CPU_COUNT * 0.2);
        static int maxPoolSize = CPU_COUNT + 1;
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomThreadFactory("CPU")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            // JVM关闭时的钩子函数
            Runtime.getRuntime().addShutdownHook(
                    new ShutdownHookThread<>("CPU密集型任务线程池", (Callable<Void>) () -> {
                        // 优雅关闭线程池
                        shutdownThreadPoolGracefully(EXECUTOR);
                        return null;
                    })
            );
        }
    }

    // 获取执行IO密集型任务的线程池
    public static ExecutorService getIoThreadPoolExecutor() {
        return TtlExecutors.getTtlExecutorService(IoThreadPoolLazyHolder.EXECUTOR);
    }

    /**
     * 线程池： 用于IO密集型任务
     */
    private static class IoThreadPoolLazyHolder {
        // 最大线程数 = CPU核心数 / （1 - 阻塞占百分比）
        static int maxPoolSize = (int) (CPU_COUNT / (1 - 0.8));

        // 核心线程数 = 最大线程数 * 20%
        static int corePoolSize = (int) (maxPoolSize * 0.2);
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomThreadFactory("IO")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            // JVM关闭时的钩子函数
            Runtime.getRuntime().addShutdownHook(
                    new ShutdownHookThread<>("IO密集型任务线程池", (Callable<Void>) () -> {
                        //优雅关闭线程池
                        shutdownThreadPoolGracefully(EXECUTOR);
                        return null;
                    })
            );
        }
    }

    /**
     * 混合线程池
     */
    public static ThreadPoolExecutor getMixedThreadPoolExecutor() {
        return MixedThreadPoolLazyHolder.EXECUTOR;
    }

    private static class MixedThreadPoolLazyHolder {
        static int MIXED_CORE = 1;  // 混合线程池核心线程数
        static int MIXED_MAX = 128;  // 最大线程数

        static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                MIXED_CORE, MIXED_MAX,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomThreadFactory("MIXED")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            //JVM关闭时的钩子函数
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread<>("混合型任务线程池", (Callable<Void>) () -> {
                //优雅关闭线程池
                shutdownThreadPoolGracefully(EXECUTOR);
                return null;
            }));
        }
    }

    public static class ShutdownHookThread<T> extends Thread {
        private volatile boolean hasShutdown = false;
        private static final AtomicInteger shutdownTimes = new AtomicInteger(0);
        private final Callable<T> callback;

        /**
         * Create the standard hook thread, with a call back, by using {@link Callable} interface.
         *
         * @param name     线程名称
         * @param callback The call back function.
         */
        public ShutdownHookThread(String name, Callable<T> callback) {
            super("JVM退出钩子(" + name + ")");

            this.callback = callback;
        }

        /**
         * Thread run method.
         * Invoke when the jvm shutdown.
         */
        @Override
        public void run() {
            synchronized (this) {
                log.info("[{}] is starting....", getName());
                if (!this.hasShutdown) {
                    this.hasShutdown = true;
                    long beginTime = System.currentTimeMillis();
                    try {
                        this.callback.call();
                    } catch (Exception e) {
                        log.error("[{}] has error.", getName(), e);
                    }
                    long consumingTimeTotal = System.currentTimeMillis() - beginTime;
                    log.info("[{}] 耗时(ms): {}", getName(), consumingTimeTotal);
                }
            }
        }
    }

    /**
     * 优雅关闭线程池
     *
     * @param threadPool 线程池
     */
    public static void shutdownThreadPoolGracefully(ExecutorService threadPool) {
        if (Objects.isNull(threadPool) || threadPool.isTerminated()) {
            return;
        }
        // 拒绝接受新任务
        try {
            threadPool.shutdown();
        } catch (SecurityException | NullPointerException e) {
            return;
        }
        // 等待线程池中的任务完成执行，默认给60秒
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                // 调用 shutdownNow 取消正在执行的任务
                threadPool.shutdownNow();
                // 再次等待 60 s，如果还未结束，可以再次尝试，或则直接放弃
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("线程池任务未正常执行结束");
                }
            }
        } catch (InterruptedException ie) {
            // 捕获异常，重新调用 shutdownNow
            threadPool.shutdownNow();
        }
        // 任然没有关闭，循环关闭1000次，每次等待10毫秒
        if (!threadPool.isTerminated()) {
            try {
                for (int i = 0; i < 1000; i++) {
                    if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                    threadPool.shutdownNow();
                }
            } catch (Throwable e) {
                log.error("循环关闭发生错误", e);
            }
        }
    }

    /**
     * 定制的线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {

        private final ThreadGroup threadGroup;

        //线程数量
        static final AtomicInteger threadCount = new AtomicInteger();

        private final String threadName;

        CustomThreadFactory(String threadName) {
            SecurityManager manager = System.getSecurityManager();
            threadGroup = Objects.nonNull(manager)
                    ? manager.getThreadGroup()
                    : Thread.currentThread().getThreadGroup();
            this.threadName = threadName;
        }

        @Override
        public Thread newThread(@NonNull Runnable target) {
            String name = "CustomThread [" + threadName + "]-" + threadCount.getAndIncrement();
            Thread t = new Thread(threadGroup, target, name, 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
