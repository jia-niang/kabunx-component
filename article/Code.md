## Java 代码优化法则

通常情况缓存、池化对象、大对象复用、并行计算、锁优化、NIO 等优化方法，它们对性能的提升往往是质的飞跃。

但语言本身对性能也是有影响的，在编码中保持好的习惯，让代码保持最优状态。

### 代码优化法则

#### 1. 使用局部变量可避免在堆上分配

由于堆资源是多线程共享的，是垃圾回收器工作的主要区域，过多的对象会造成 GC 压力。可以通过局部变量的方式，将变量在栈上分配。这种方式变量会随着方法执行的完毕而销毁，能够减轻
GC 的压力。

#### 2. 减少变量的作用范围

注意变量的作用范围，尽量减少对象的创建。

#### 3. 访问静态变量直接使用类名

#### 4. 字符串拼接使用 StringBuilder

字符串拼接，使用 StringBuilder 或者 StringBuffer，不要使用 + 号。

#### 5. 重写对象的 HashCode，不要简单地返回固定值

当这些对象存入 HashMap 时，性能就会非常低，因为 HashMap 是通过 HashCode 定位到 Hash 槽，有冲突的时候，才会使用链表或者红黑树组织节点。

#### 6. HashMap 等集合初始化的时候，指定初始值大小

通过指定初始值大小可减少扩容造成的性能损耗。

#### 7. 遍历 Map 的时候，使用 EntrySet 方法

使用 EntrySet 方法，可以直接返回 set 对象，直接拿来用即可；而使用 KeySet 方法，获得的是key 的集合，需要再进行一次 get
操作，多了一个操作步骤。所以更推荐使用 EntrySet 方式遍历 Map。

#### 8. 不要在多线程下使用同一个 Random

Random 类的 seed 会在并发访问的情况下发生竞争，造成性能降低，建议在多线程环境下使用 ThreadLocalRandom 类。

在 Linux 上，通过加入 JVM 配置 -Djava.security.egd=file:/dev/./urandom，使用 urandom 随机生成器，在进行随机数获取时，速度会更快。

#### 9. 自增推荐使用 LongAddr

自增运算可以通过 synchronized 和 volatile 的组合，或者也可以使用原子类（比如 AtomicLong）。

后者的速度比前者要高一些，AtomicLong 使用 CAS 进行比较替换，在线程多的情况下会造成过多无效自旋，所以可以使用 LongAdder 替换
AtomicLong 进行进一步的性能提升。

#### 10. 不要使用异常控制程序流程

异常，是用来了解并解决程序中遇到的各种不正常的情况，它的实现方式比较昂贵，比平常的条件判断语句效率要低很多。

这是因为异常在字节码层面，需要生成一个如下所示的异常表（Exception table），多了很多判断步骤。

#### 11. 不要在循环中使用 try catch ？

道理与上面类似，不要把异常处理放在循环里，而应该把它放在最外层，但实际测试情况表明这两种方式性能相差并不大。

既然性能没什么差别，那么就推荐根据业务的需求进行编码。

#### 12. 不要捕捉 RuntimeException

Java 异常分为两种，一种是可以通过预检查机制避免的 RuntimeException；另外一种就是普通异常。

其中，RuntimeException 不应该通过 catch 语句去捕捉，而应该使用编码手段进行规避。

比如，list 可能会出现数组越界异常。是否越界是可以通过代码提前判断的，而不是等到发生异常时去捕捉。提前判断这种方式，代码会更优雅，效率也更高。

#### 13. 日志打印的注意事项

我们平常会使用 debug 输出一些调试信息，然后在线上关掉它。

可以在每次打印之前都使用 isDebugEnabled 方法判断一下日志级别，代码如下：

```text
if(logger.isDebugEnabled()) { 
    logger.debug("debug: "+ topic + "  is debug"  );
}
```

使用占位符的方式，也可以达到相同的效果，就不用手动添加 isDebugEnabled 方法了，代码也优雅得多。

```text
logger.debug("debug: "+ topic + "  is debug"  );
```

对于业务系统来说，日志对系统的性能影响非常大，不需要的日志，尽量不要打印，避免占用 I/O 资源。

#### 14. 减少事务的作用范围

#### 15. 正则表达式可以预先编译，加快速度

Java 的正则表达式需要先编译再使用。

典型代码如下：

```text
Pattern pattern = Pattern.compile({pattern});
Matcher pattern = pattern.matcher({content});
```

Pattern 编译非常耗时，它的 Matcher 方法是线程安全的，每次调用方法这个方法都会生成一个新的 Matcher 对象。所以，一般 Pattern
初始化一次即可，可以作为类的静态成员变量。