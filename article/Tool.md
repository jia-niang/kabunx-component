想要进行深入排查，需要收集较详细的性能数据，包括操作系统性能数据、JVM 的性能数据、应用的性能数据等。

## 如何获取代码性能数据？

### JMC —— 获取 Java 应用详细性能数据

对于我们常用的 HotSpot 来说，有更强大的工具，那就是 JMC。 JMC 集成了一个非常好用的功能：JFR（Java Flight Recorder）。

JFR 功能是建在 JVM 内部的，不需要额外依赖，可以直接使用，它能够监测大量数据。比如，我们提到的锁竞争、延迟、阻塞等；甚至在 JVM
内部，比如 SafePoint、JIT 编译等，也能去分析。

### Arthas —— 获取单个请求的调用链耗时

Arthas 是一个 Java 诊断工具，可以排查内存溢出、CPU 飙升、负载高等内容，可以说是一个 jstack、jmap 等命令的大集合。

## 基准测试 JMH，精确测量方法性能

有时候，我们想要测量某段具体代码的性能情况，这时经常会写一些统计执行时间的代码，这些代码穿插在我们的逻辑中，进行一些简单的计时运算。

我们通常会在代码中这样统计

```text
long start = System.currentTimeMillis(); 
// logic 
long cost = System.currentTimeMillis() - start; 
System.out.println("Logic cost : " + cost);
```

可惜的是，这段代码的统计结果，并不一定准确。举个例子来说，JVM 在执行时，会对一些代码块，或者一些频繁执行的逻辑，进行 JIT
编译和内联优化，在得到一个稳定的测试结果之前，需要先循环上万次进行预热。预热前和预热后的性能差别非常大。

### JMH —— 基准测试工具

JMH（the Java Microbenchmark Harness）就是这样一个能做基准测试的工具。它的测量精度非常高，可达纳秒级别。

JMH 已经在 JDK 12中被包含，由于我们大部分还在使用 JDK 1.8版本，需要自行引入 maven，坐标如下：

```xml

<dependencies>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.36</version>
    </dependency>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.36</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

JMH 是一个 jar 包，它和单元测试框架 JUnit 非常像，可以通过注解进行一些基础配置。这部分配置有很多是可以通过 main 方法的
OptionsBuilder 进行设置的。

JMH 程序执行，通过开启多个进程，多个线程，先执行预热，然后执行迭代，最后汇总所有的测试数据进行分析。在执行前后，还可以根据粒度处理一些前置和后置操作。

一段简单的 JMH 代码如下所示：

```java

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@Threads(2)
public class BenchmarkTest {
    @Benchmark
    public long shift() {
        long t = 455565655225562L;
        long a = 0;
        for (int i = 0; i < 1000; i++) {
            a = t >> 30;
        }
        return a;
    }

    @Benchmark
    public long div() {
        long t = 455565655225562L;
        long a = 0;
        for (int i = 0; i < 1000; i++) {
            a = t / 1024 / 1024 / 1024;
        }
        return a;
    }

    public static void main(String[] args) {
        Options opts = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opts).run();
    }
}
```

### 关键注解

#### 1. @Warmup

```text
@Warmup( 
iterations = 5, 
time = 1, 
timeUnit = TimeUnit.SECONDS)
```

我们不止一次提到预热 warmup 这个注解，可以用在类或者方法上，进行预热配置。

* timeUnit：时间的单位，默认的单位是秒；
* iterations：预热阶段的迭代数；
* time：每次预热的时间；
* batchSize：批处理大小，指定了每次操作调用几次方法。

一般来说，基准测试都是针对比较小的、执行速度相对较快的代码块，这些代码有很大的可能性被 JIT 编译、内联，所以在编码时保持方法的精简，是一个好的习惯。

其他注解后续补充，先知道有这个东西。

### 将结果图形化

使用 JMH 测试的结果，可以二次加工，进行图形化展示。结合图表数据，更加直观。通过运行时，指定输出的格式文件，即可获得相应格式的性能测试结果。

#### JMH 支持 5 种格式结果

* TEXT 导出文本文件。
* CSV 导出 csv 格式文件。
* SCSV 导出 scsv 等格式的**文件。
* JSON 导出成 json 文件。
* LATEX 导出到 latex，一种基于 ΤΕΧ 的排版系统。

一般来说，我们导出成 CSV 文件，直接在 Excel 中操作。


