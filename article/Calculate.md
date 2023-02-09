Java 提供了非常丰富的 API，来支持多线程开发。对我们 Java 程序员来说，多线程是工作中必备的技能。

## 并行获取数据

实际场景：一个聚合接口，要求在 100ms 内返回数据。它的调用逻辑非常复杂，打交道的接口也非常多，需要从 20 多个接口汇总数据。
这些接口，最小的耗时也要 20ms，哪怕全部都是最优状态，算下来也需要 20*20 = 400ms。

这个时候，我们就可以使用 CountDownLatch 完成操作。CountDownLatch 本质上是一个计数器，我们把它初始化为与执行任务相同的数量。当一个任务执行完时，就将计数器的值减
1，直到计数器值达到 0 时，表示完成了所有的任务，在 await 上等待的线程就可以继续执行下去。

我专门为这个场景封装的一个工具类。它传入了两个参数：一个是要计算的 job 数量，另外一个是整个大任务超时的毫秒数。

```java
public class ParallelFetcher {
    final long timeout;
    final CountDownLatch latch;
    final ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 1,
            TimeUnit.HOURS, new ArrayBlockingQueue<>(100));

    public ParallelFetcher(int jobSize, long timeoutMill) {
        latch = new CountDownLatch(jobSize);
        timeout = timeoutMill;
    }

    public void submitJob(Runnable runnable) {
        executor.execute(() -> {
            runnable.run();
            latch.countDown();
        });
    }

    public void await() {
        try {
            this.latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    public void dispose() {
        this.executor.shutdown();
    }
}

```

当我们的 job 运行时间，超过了任务的时间上限，就会被直接终止，这就是 await 函数的功能。

使用案例

```java
public class Parallel {
    public static void main(String[] args) {
        ParallelFetcher fetcher = new ParallelFetcher(20, 50);
        // ConcurrentHashMap
        final Map<String, String> result = new HashMap<>();
        fetcher.submitJob(() -> {
            // 你要执行调用
            result.put("f1", "123");
        });
        fetcher.await();
        fetcher.dispose();
    }
}
```

使用这种方式，我们的接口就可以在固定的时间内返回了。concurrent 包里面提供了非常多的类似 CountDownLatch
的工具，在享受便捷性的同时，我们来看一下这段代码需要注意的事情。

首先，latch 的数量加上 map 的 size，总数应该是 20，但运行之后，大概率不是，我们丢失了部分数据。原因就是，main 方法里使用了
HashMap 类，它并不是线程安全的，在并发执行时发生了错乱，造成了错误的结果，将 HashMap 换成 ConcurrentHashMap 即可解决问题。

从这个小问题我们就可以看出：并发编程并不是那么友好，一不小心就会踏进陷阱。如果你对集合的使用场景并不是特别在行，直接使用线程安全的类，出错的概率会更少一点。

我们再来看一下线程池的设置，里面有非常多的参数，最大池数量达到了 200 个。那线程数到底设置多少合适呢？按照我们的需求，每次请求需要执行
20 个线程，200 个线程就可以支持 10 个并发量，按照最悲观的 50ms 来算的话，这个接口支持的最小 QPS 就是：1000/50*
10=200。这就是说，如果访问量增加，这个线程数还可以调大。

在我们的平常的业务中，有计算密集型任务和 I/O 密集型任务之分。

### I/O 密集型任务

对于我们常见的互联网服务来说，大多数是属于 I/O 密集型的，比如等待数据库的 I/O，等待网络 I/O 等。在这种情况下，当线程数量等于
I/O 任务的数量时，效果是最好的。虽然线程上下文切换会有一定的性能损耗，但相对于缓慢的 I/O 来说，这点损失是可以接受的。

### 计算密集型任务

计算密集型的任务却正好相反，比如一些耗时的算法逻辑。CPU 要想达到最高的利用率，提高吞吐量，最好的方式就是：让它尽量少地在任务之间切换，此时，线程数等于
CPU 数量，是效率最高的。

了解了任务的这些特点，就可以通过调整线程数量增加服务性能。比如，高性能的网络工具包 Netty，EventLoop 默认的线程数量，就是处理器的
2 倍。

