## JVM 如何完成垃圾回收？

Java 中的一些代码优化技巧，和 JVM 的关系非常的大，比如逃逸分析对非捕获型 Lambda 表达式的优化。

### JVM 内存区域划分

内存区域划分主要包括堆、Java 虚拟机栈、程序计数器、本地方法栈、元空间和直接内存这五部分。

#### 1. 堆

JVM 中占用内存最大的区域，就是堆（Heap），我们平常编码创建的对象，大多数是在这上面分配的，也是垃圾回收器回收的主要目标区域。

#### 2. Java 虚拟机栈

JVM 的解释过程是基于栈的，程序的执行过程也就是入栈出栈的过程，这也是 Java 虚拟机栈这个名称的由来。

Java 虚拟机栈是和线程相关的。当你启动一个新的线程，Java 就会为它分配一个虚拟机栈，之后所有这个线程的运行，都会在栈里进行。

Java 虚拟机栈，从方法入栈到具体的字节码执行，其实是一个双层的栈结构，也就是栈里面还包含栈。

Java 虚拟机栈里的每一个元素，叫作栈帧。每一个栈帧，包含四个区域： 局部变量表 、操作数栈、动态连接和返回地址。

其中，操作数栈就是具体的字节码指令所操作的栈区域，考虑到下面这段代码：

```text
public void test(){
    int a = 1;
    a++;
}
```

JVM 将会为 test 方法生成一个栈帧，然后入栈，等 test 方法执行完毕，就会将对应的栈帧弹出。在对变量 a
进行加一操作的时候，就会对栈帧中的操作数栈运用相关的字节码指令。

#### 3. 程序计数器

既然是线程，就要接受操作系统的调度，但总有时候，某些线程是获取不到 CPU 时间片的，那么当这个线程恢复执行的时候，它是如何确保找到切换之前执行的位置呢？这就是程序计数器的功能。

和 Java 虚拟机栈一样，它也是线程私有的。程序计数器只需要记录一个执行位置就可以，所以不需要太大的空间。事实上，程序计数器是
JVM 规范中唯一没有规定 OutOfMemoryError 情况的区域。

#### 4. 本地方法栈

与 Java 虚拟机栈类似，本地方法栈，是针对 native 方法的。我们常用的 HotSpot，将 Java
虚拟机栈和本地方法栈合二为一，其实就是一个本地方法栈，大家注意规范里的这些差别就可以了。

#### 5. 元空间

元空间是一个容易引起混淆的区域，原因就在于它经历了多次迭代才成为现在的模样。

* 元空间是在堆上吗？

答案：元空间并不是在堆上分配的，而是在堆外空间进行分配的，它的大小默认没有上限，我们常说的方法区，就在元空间中。

* 字符串常量池在那个区域中？

答案：这个要看 JDK 版本。

在 JDK 1.8 之前，是没有元空间这个概念的，当时的方法区是放在一个叫作永久代的空间中。

从 1.7 版本开始，字符串常量池就一直存在于堆上。

#### 6. 直接内存

直接内存，指的是使用了 Java 的直接内存 API，进行操作的内存。这部分内存可以受到 JVM 的管控。

需要注意的是直接内存和本地内存不是一个概念。

* 直接内存比较专一，有具体的 API（这里指的是 ByteBuffer），也可以使用 -XX:MaxDirectMemorySize 参数控制它的大小；
* 本地内存是一个统称，比如使用 native 函数操作的内存就是本地内存，本地内存的使用 JVM 是限制不住的，使用的时候一定要小心。

### GC Roots

对象主要是在堆上分配的，我们可以把它想象成一个池子，对象不停地创建，后台的垃圾回收进程不断地清理不再使用的对象。当内存回收的速度，赶不上对象创建的速度，这个对象池子就会产生溢出，也就是我们常说的
OOM。

把不再使用的对象及时地从堆空间清理出去，是避免 OOM 有效的方法。那 JVM 是如何判断哪些对象应该被清理，哪些对象需要被继续使用呢？

了解了这个概念，我们就可以看下一些基本的衍生分析：

* GC 的速度，和堆内存活对象的多少有关，与堆内所有对象的数量无关；
* GC 的速度与堆的大小无关，32GB 的堆和 4GB 的堆，只要存活对象是一样的，垃圾回收速度也会差不多；
* 垃圾回收不必每次都把垃圾清理得干干净净，最重要的是不要把正在使用的对象判定为垃圾。

那么，如何找到这些存活对象，也就是哪些对象是正在被使用的，就成了问题的核心。

大家可以想一下写代码的时候，如果想要保证一个 HashMap 能够被持续使用，可以把它声明成静态变量，这样就不会被垃圾回收器回收掉。我们把这些正在使用的引用的入口，叫作
GC Roots。

这种使用 tracing 方式寻找存活对象的方法，还有一个好听的名字，叫作可达性分析法。

概括来讲，GC Roots 包括：

* Java 线程中，当前所有正在被调用的方法的引用类型参数、局部变量、临时值等。也就是与我们栈帧相关的各种引用；
* 所有当前被加载的 Java 类；
* Java 类的引用类型静态变量；
* 运行时常量池里的引用类型常量（String 或 Class 类型）；
* JVM 内部数据结构的一些引用，比如 sun.jvm.hotspot.memory.Universe 类；
* 用于同步的监控对象，比如调用了对象的 wait() 方法；
* JNI handles，包括 global handles 和 local handles。

入口大约有三个：线程、静态变量和 JNI 引用。

### 强、软、弱、虚引用

那么，通过 GC Roots 能够追溯到的对象，就一定不会被垃圾回收吗？这要看情况。

Java 对象与对象之间的引用，存在着四种不同的引用级别，强度从高到低依次是：强引用、软引用、弱引用、虚引用。

* 强应用 默认的对象关系是强引用，也就是我们默认的对象创建方式。这种引用属于最普通最强硬的一种存在，只有在和 GC Roots
  断绝关系时，才会被消灭掉。
* 软引用 用于维护一些可有可无的对象。在内存足够的时候，软引用对象不会被回收；只有在内存不足时，系统则会回收软引用对象；如果回收了软引用对象之后，仍然没有足够的内存，才会抛出内存溢出异常。
* 弱引用 级别就更低一些，当 JVM 进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。软引用和弱引用在堆内缓存系统中使用非常频繁，可以在内存紧张时优先被回收掉。
* 虚引用 是一种形同虚设的引用，在现实场景中用得不是很多。这里有一个冷门的知识点：Java 9.0 以后新加入了 Cleaner 类，用来替代
  Object 类的 finalizer 方法，这就是虚引用的一种应用场景。

### 分代垃圾回收

垃圾回收的速度，是和存活的对象数量有关系的，如果这些对象太多，JVM 再做标记和追溯的时候，就会很慢。

一般情况下，JVM 在做这些事情的时候，都会停止业务线程的所有工作，进入 SafePoint 状态，这也就是我们通常说的 Stop the
World。所以，现在的垃圾回收器，有一个主要目标，就是减少 STW 的时间。

其中一种有效的方式，就是采用分代垃圾回收，减少单次回收区域的大小。这是因为，大部分对象，可以分为两类：

* 大部分对象的生命周期都很短
* 其他对象则很可能会存活很长时间

这个假设我们称之为弱代假设（weak generational hypothesis）。

堆空间划分图：年轻代和老年代。

#### 1. 年轻代

年轻代中又分为一个伊甸园空间（Eden），两个幸存者空间（Survivor）。对象会首先在年轻代中的 Eden 区进行分配，当 Eden
区分配满的时候，就会触发年轻代的 GC。

此时，存活的对象会被移动到其中一个 Survivor 分区（以下简称 from）；年轻代再次发生垃圾回收，存活对象，包括 from 区中的存活对象，会被移动到
to 区。所以，from 和 to 两个区域，总有一个是空的。

Eden、from、to 的默认比例是 8:1:1，所以只会造成 10% 的空间浪费。这个比例，是由参数 -XX:SurvivorRatio 进行配置的（默认为 8）。

#### 2. 老年代

对垃圾回收的优化，就是要让对象尽快在年轻代就回收掉，减少到老年代的对象。那么对象是如何进入老年代的呢？它主要有以下四种方式。

* 正常提升（Promotion）

上面提到了年轻代的垃圾回收，如果对象能够熬过年轻代垃圾回收，它的年龄（age）就会加一，当对象的年龄达到一定阈值，就会被移动到老年代中。

* 分配担保

如果年轻代的空间不足，又有新的对象需要分配空间，就需要依赖其他内存（这里是老年代）进行分配担保，对象将直接在老年代创建。

* 大对象直接在老年代分配

超出某个阈值大小的对象，将直接在老年代分配，可以通过 -XX:PretenureSizeThreshold 配置这个阈值。

* 动态对象年龄判定

老年代的空间一般比较大，回收的时间更长，当老年代的空间被占满了，将发生老年代垃圾回收。

目前，被广泛使用的是 G1 垃圾回收器。G1 的目标是用来干掉 CMS 的，它同样有年轻代和老年代的概念。不过，G1
把整个堆切成了很多份，把每一份当作一个小目标，部分上目标很容易达成。

## JVM 常见优化参数

现在大家用得最多的 Java 版本是 Java 8，如果你的公司比较保守，那么使用较多的垃圾回收器就是 CMS 。但 CMS 已经在 Java 14
中被正式废除，随着 ZGC 的诞生和 G1 的稳定，CMS 终将成为过去式。

所以，在不同的 JVM 版本上，不同的垃圾回收器上，要先看一下这个参数默认是什么，不要轻信别人的建议，命令行示例如下：

```shell
java -XX:+PrintFlagsFinal -XX:+UseG1GC  2>&1 | grep UseAdaptiveSizePolicy
```

还有一个与之类似的参数叫作 PrintCommandLineFlags，通过它，你能够查看当前所使用的垃圾回收器和一些默认的值。

```shell
# java -XX:+PrintCommandLineFlags -version
-XX:InitialHeapSize=532373376 -XX:MaxHeapSize=8517974016 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
java version "1.8.0_351"
Java(TM) SE Runtime Environment (build 1.8.0_351-b10)
Java HotSpot(TM) 64-Bit Server VM (build 25.351-b10, mixed mode)
```

JVM 的参数配置繁多，但大多数不需要我们去关心。 下面，我们通过对 ES 服务的 JVM 参数分析，来看一下常见的优化点。

### 堆空间的配置

JVM 中空间最大的一块就是堆，垃圾回收也主要是针对这块区域。通过 Xmx 可指定堆的最大值，通过 Xms
可指定堆的初始大小。我们通常把这两个参数，设置成一样大小的，可避免堆空间在动态扩容时的时间开销。

```text
-XX:+AlwaysPreTouch
```

其实，通过 Xmx 指定了的堆内存，只有在 JVM 真正使用的时候，才会进行分配。这个参数，在 JVM
启动的时候，就把它所有的内存在操作系统分配了。在堆比较大的时候，会加大启动时间，但它能够减少内存动态分配的性能损耗，提高运行时的速度。

对于普通的 Web 服务，通常会把堆内存设置为物理内存的 2/3，剩下的 1/3 就是给堆外内存使用的。

我们这张图，对堆外内存进行了非常细致的划分，解释如下：

* 元空间 参数 -XX:MaxMetaspaceSize 和 -XX:MetaspaceSize，分别指定了元空间的最大内存和初始化内存。因为元空间默认是没有上限的，所以极端情况下，元空间会一直挤占操作系统剩余内存。
* JIT 编译后代码存放 -XX:ReservedCodeCacheSize。JIT 是 JVM 一个非常重要的特性，CodeCahe 存放的，就是即时编译器所生成的二进制代码。另外，JNI
  的代码也是放在这里的。
* 本地内存 本地内存是一些其他 attch 在 JVM 进程上的内存区域的统称。比如网络连接占用的内存、线程创建占用的内存等。在高并发应用下，由于连接和线程都比较多，这部分内存累加起来还是比较可观的。
* 直接内存 这里要着重提一下直接内存，因为它是本地内存中唯一可以使用参数来限制大小的区域。使用参数 -XX:
  MaxDirectMemorySize，即可设定 ByteBuffer 类所申请的内存上限。
* JNI 内存 上面谈到 CodeCache 存放的 JNI 代码，JNI 内存就是指的这部分代码所 malloc 的具体内存。很可惜的是，这部分内存的使用
  JVM 是无法控制的，它依赖于具体的 JNI 代码实现。

### 日志参数配置

```text
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintTenuringDistribution
-XX:+PrintGCApplicationStoppedTime
-Xloggc:logs/gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=32
-XX:GCLogFileSize=64m
```

这些参数的意义

* PrintGCDetails 打印详细 GC 日志。
* PrintGCDateStamps 打印当前系统时间，更加可读；与之对应的是PrintGCDateStamps 打印的是JVM启动后的相对时间，可读性较差。
* PrintTenuringDistribution 打印对象年龄分布，对调优 MaxTenuringThreshold 参数帮助很大。
* PrintGCApplicationStoppedTime 打印 STW 时间
* 下面几个日志参数是配置了类似于 Logback 的滚动日志，比较简单，不再详细介绍

```text
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=data
-XX:ErrorFile=logs/hs_err_pid%p.log
```

HeapDumpOnOutOfMemoryError、HeapDumpPath、ErrorFile 是每个 Java 应用都应该配置的参数。正常情况下，我们通过 jmap
获取应用程序的堆信息；异常情况下，比如发生了 OOM，通过这三个配置参数，即可在发生OOM的时候，自动 dump 一份堆信息到指定的目录中。

### 垃圾回收器配置

### 其他调优

以上就是 ES 默认的 JVM 参数配置，大多数还是比较基础的。在平常的应用服务中，我们希望得到更细粒度的控制，其中比较常用的就是调整各个分代之间的比例。

* -Xmn 年轻代大小，默认年轻代占堆大小的 1/3。高并发快消亡场景可适当加大这个区域，对半或者更多都是可以的。但是在 G1 下，就不用再设置这个值了，它会自动调整；
* -XX:SurvivorRatio 默认值为 8，表示伊甸区和幸存区的比例；
* -XX:MaxTenuringThreshold 这个值在 CMS 下默认为 6，G1 下默认为 15。这个值和我们前面提到的对象提升有关，改动效果会比较明显。对象的年龄分布可以使用 -XX:+PrintTenuringDistribution 打印，如果后面几代的大小总是差不多，证明过了某个年龄后的对象总能晋升到老年代，就可以把晋升阈值设的小一些；
* PretenureSizeThreshold 超过一定大小的对象，将直接在老年代分配，不过这个参数用得不是很多。

