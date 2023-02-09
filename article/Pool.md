在我们平时的编码中，通常会将一些对象保存起来，这主要考虑的是对象的创建成本。比如像线程资源、数据库连接资源或者 TCP
连接等，这类对象的初始化通常要花费比较长的时间，如果频繁地申请和销毁，就会耗费大量的系统资源，造成不必要的性能损失。

并且这些对象都有一个显著的特征，就是通过轻量级的重置工作，可以循环、重复地使用。
这个时候，我们就可以使用一个虚拟的池子，将这些资源保存起来，当使用的时候，我们就从池子里快速获取一个即可。

池化技术应用非常广泛，常见的就有数据库连接池、线程池等。

## 连接池

### Redis 连接池

### 数据库连接池

#### 问题1：连接池设置成多大

连接池的大小设置得越大越好，这是一种误解。根据经验，数据库连接，只需要 20~50 个就够用了。具体大小，要根据业务调整。

当你遇到下面的场景，就可以考虑使用池化来增加系统性能：

* 对象的创建或者销毁，需要耗费较多的系统资源；
* 对象的创建或者销毁，耗时长，需要繁杂的操作和较长时间的等待；
* 对象创建后，通过一些状态重置，可被反复使用。

将对象池化之后，只是开启了第一步优化。要想达到最优性能，就不得不调整池的一些关键参数，合理的池大小加上合理的超时时间，就可以让池发挥更大的价值。

## 大对象复用的目标和注意点

为什么大对象会影响我们的应用性能呢？

* 第一，大对象占用的资源多，垃圾回收器要花一部分精力去对它进行回收；
* 第二，大对象在不同的设备之间交换，会耗费网络流量，以及昂贵的 I/O；
* 第三，对大对象的解析和处理操作是耗时的，对象职责不聚焦，就会承担额外的性能开销。

从数据的结构纬度和时间维度出发，分别逐步看一下一些把对象变小，把操作聚焦的策略。

### 保持合适的对象粒度

实际场景：在我们业务系统中，需要频繁使用到用户的基本数据。

为了加快数据的查询速度，我们一般会对数据缓存，放入到 Redis 中，查询性能有了很大的改善，但存在很多冗余数据。

Redis Key 是这样设计的

```text
type: string 
key: user_${userid} 
value: json
```

这样的设计有两个问题：

* 查询其中某个字段的值，需要把所有 json 数据查询出来，并自行解析；
* 更新其中某个字段的值，需要更新整个 json 串，代价较高。

针对这种大粒度 json 信息，就可以采用打散的方式进行优化，使得每次更新和查询，都有聚焦的目标。

接下来对 Redis 中的数据进行了以下设计，采用 hash 结构而不是 json 结构：

```text
type: hash 
key: user_${userid} 
value: {sex:f, id:1223, age:23}
```

这样，我们使用 hget 命令，或者 hmget 命令，就可以获取到想要的数据，加快信息流转的速度。

### 数据的冷热分离

数据除了横向的结构纬度，还有一个纵向的时间维度，对时间维度的优化，最有效的方式就是冷热分离。

所谓热数据，就是靠近用户的，被频繁使用的数据；而冷数据是那些访问频率非常低，年代非常久远的数据。

## 从池化对象原理看线程池

线程的资源也是比较昂贵的，频繁地创建和销毁同样会影响系统性能。

### 多线程资源盘点

#### 1. 线程安全的类

HashMap 和 ConcurrentHashMap，后者相对于前者，是线程安全的。多线程的细节非常多，下面我们就来盘点一下，一些常见的线程安全的类。

* StringBuilder 对应着 StringBuffer。后者主要是通过 synchronized 关键字实现了线程的同步。值得注意的是，在单个方法区域里，这两者是没有区别的，JIT
  的编译优化会去掉 synchronized 关键字的影响。
* HashMap 对应着 ConcurrentHashMap。ConcurrentHashMap 的话题很大，这里提醒一下 JDK1.7 和 1.8 之间的实现已经不一样了。1.8
  已经去掉了分段锁的概念（锁分离技术），并且使用 synchronized 来代替了 ReentrantLock。
* ArrayList 对应着 CopyOnWriteList。后者是写时复制的概念，适合读多写少的场景。
* LinkedList 对应着 ArrayBlockingQueue。ArrayBlockingQueue 对默认是不公平锁，可以修改构造参数，将其改成公平阻塞队列，它在
  concurrent 包里使用得非常频繁。
* HashSet 对应着 CopyOnWriteArraySet。

来说一个实际案例

SimpleDateFormat 是我们经常用到的日期处理类，但它本身不是线程安全的，在多线程运行环境下，会产生很多问题。

```java
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaultDateFormat {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        final FaultDateFormat faultDateFormat = new FaultDateFormat();
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                try {
                    System.out.println(faultDateFormat.format.parse("2020-08-10 23:59:59"));
                } catch (ParseException ex) {
                    throw new IllegalStateException();
                }
            });
        }
        executor.shutdown();
    }
}
```

在打印结果的时候，我们可以看到，时间上已经错乱了。

解决方式就是使用 ThreadLocal 局部变量，可以有效地解决线程安全问题。

```java
import java.text.SimpleDateFormat;

public class GoodDateFormat {
    ThreadLocal<SimpleDateFormat> format = ThreadLocal.withInitial(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
}
```

#### 2. 线程的同步方式

Java 中实现线程同步的方式有很多，大体可以分为以下 8 类。

* 使用 Object 类中的 wait、notify、notifyAll 等函数。由于这种编程模型非常复杂，现在已经很少用了。这里有一个关键点，那就是对于这些函数的调用，必须放在同步代码块里才能正常运行。
* 使用 ThreadLocal 线程局部变量的方式，每个线程一个变量，本课时会详细讲解。
* 使用 synchronized 关键字修饰方法或者代码块。这是 Java 中最常见的方式，有锁升级的概念。
* 使用 Concurrent 包里的可重入锁 ReentrantLock。使用 CAS 方式实现的可重入锁。
* 使用 volatile 关键字控制变量的可见性，这个关键字保证了变量的可见性，但不能保证它的原子性。
* 使用线程安全的阻塞队列完成线程同步。比如，使用 LinkedBlockingQueue 实现一个简单的生产者消费者。
* 使用原子变量。Atomic* 系列方法，也是使用 CAS 实现的，关于 CAS，我们将在下一课时介绍。
* 使用 Thread 类的 join 方法，可以让多线程按照指定的顺序执行。

如何使用 LinkedBlockingQueue 实现的一个简单生产者和消费者实例？

### 在多线程使用中都会遇到什么问题？

* 线程池的不正确使用，造成了资源分配的不可控；
* I/O 密集型场景下，线程池开得过小，造成了请求的频繁失败；
* 线程池使用了 CallerRunsPolicy 饱和策略，造成了业务线程的阻塞；
* SimpleDateFormat 造成的时间错乱。

另外，我想要着重提到的一点是，在处理循环的任务时，一定不要忘了捕捉异常。尤其需要说明的是，像 NPE 这样的异常，由于是非捕获型的，IDE
的代码提示往往不起作用。

```text
  while (! xyz()) {
      try {
        ....
      } catch (Exception ex) {
        ...
      }
  }
```

多线程环境中，异常日志是非常重要的，但线程池的默认行为并不是特别切合实际。

使用 submit 方法提交的任务，会返回一个 Future 对象，只有调用了它的 get 方法，这个异常才会打印。

### 关于异步

一开始我一直有一个疑问“异步，并没有减少任务的执行步骤，也没有算法上的改进，那么为什么说异步的速度更快呢？”

异步是一种编程模型，它通过将耗时的操作转移到后台线程运行，从而减少对主业务的堵塞，所以我们说异步让速度变快了。但如果你的系统资源使用已经到了极限，异步就不能产生任何效果了，它主要优化的是那些阻塞性的等待。

异步还能够对业务进行解耦，它比较像是生产者消费者模型。主线程负责生产任务，并将它存放在待执行列表中；消费线程池负责任务的消费，进行真正的业务逻辑处理。

