## Redis 解决了什么问题？

大规模读写数据与数据库读写能力之间的矛盾

简单回顾一下 CPU 高速缓存的发展历程，为了解决 CPU
的计算速度与内存的读取速度之间的巨大差异，CPU 使用高速缓存来存放指令和数据。高速缓存从最初的主板缓存到现在的3级缓存，缓存大小也不断变大。来自网络的数据表明：CPU
高速缓存的命中率大约为80%。

类比电脑发展过程中 CPU
与内存的矛盾，可以察觉到大型网站中大规模读写数据与数据库读写能力之间的矛盾与此矛盾类似。我们也可以在数据库与应用之间构建一块比数据库速度更快存储区域——缓存。大家最熟悉的也莫过于
Redis 用作缓存，我们知道 Redis 的作者设计 Redis 的初衷是因为他使用关系型数据库时，无论如何优化，性能都不能达到自己的期望，于是便自己手写了一个内存数据库。

在作为缓存的情况下，我们有一下应用场景：

1. 热点数据 例如我们可以将 SQL 查询结果保存在内存中，也可以将用户经常查看的图片保存在内存中。
2. 排行榜基于 Redis 提供的 zSet
   这种数据结构我们可以更加便捷的实现排行榜。实现排行榜的相关内容可以参考排行榜算法设计实现比较。在小规模数据的情况下，使用Mysql实现排行榜没有多少问题，但是一旦数据量上去了，那么持续的进行
   Mysql 读写将会成为瓶颈。
3. 计数器/限速器计数器的应用场景之一是统计用户的点赞数，限速器的应用场景之一是限制用户ip的访问次数。之所以 Redis 能用于计数器是因为
   Redis 是单线程的，每次都必须前一个指令执行完，再执行下一个指令。这样就保证不会同时执行多条指令；也即不会出现并发问题。限速器的原理类似。
4. 共同好友 利用 Redis 提供的 Set 数据结构的求交集操作 sinter 可以更加便捷地求两个 Set
   集合的交集；而使用数据库的连表查询将造成性能的开销很多，因为大型网站的用户数量巨大。
5. 简单消息队列 Redis 的提供的发布/订阅是一个极其简单的消息系统。它不像 Kafka 那样提供了分成不同的 topic
   并且分成不同的分区并且提供持久化的功能。Redis 的消息队列用在不需要高可靠的场景。

### 为什么使用 redis？

分析：在项目中使用 redis，主要是从两个角度去考虑: 性能和并发。

当然，Redis 还具备可以做分布式锁等其他功能，但是如果只是为了分布式锁这些其他功能，完全还有其他中间件(
如 ZooKeeper 等)代替，并不是非要使用 redis。因此，这个问题主要从性能和并发两个角度去答。

（一）性能

我们在碰到需要执行耗时特别久，且结果不频繁变动的 SQL，就特别适合将运行结果放入缓存。这样，后面的请求就去缓存中读取，使得请求能够迅速响应。

（二）并发

在大并发的情况下，所有的请求直接访问数据库，数据库会出现连接异常。这个时候，就需要使用 redis 做一个缓冲操作，让请求先访问到
redis，而不是直接访问数据库。

## SpringBoot 整合 Redis

### 依赖

在 pom.xml 文件中添加redis的starter，这里已经将连接池替换为Jedis

```xml

<dependency>
    <groupId>com.kabunx.component</groupId>
    <artifactId>redis-spring-boot-starter</artifactId>
</dependency>
```

### 配置

在 application.yml 文件中添加相关配置

```yaml
spring:
  redis:
    host: xxx.xxx.99.232 # Redis服务器地址
    database: 0 # Redis数据库索引（默认为0）
    port: 6379 # Redis服务器连接端口
    password: xxx # Redis服务器连接密码（默认为空）
    timeout: 1000 # 连接超时时间（毫秒）
```

### 使用连接池

Redis 是基于内存的数据库，本来是为了提高程序性能的，但如果不使用 Redis 连接池的话，建立连接、断开连接就需要消耗大量的时间。

用了连接池，就可以实现在客户端建立多个连接，需要的时候从连接池拿，用完了再放回去，这样就节省了连接建立、断开的时间。

要使用连接池，我们得先了解 Redis 的客户端，常用的有两种：Jedis（1.5.x默认） 和 Lettuce（2.x默认）。

Lettuce 是一个可伸缩线程安全的 Redis 客户端。多个线程可以共享同一个 RedisConnection。它利用优秀 Netty NIO 框架来高效地管理多个连接。

相比较 Jedis ，我觉得 Lettuce 的优点有如下几个方面：

* 更加直观、结构更加良好的接口设计
* 基于 Netty NIO 可以高效管理 Redis 连接，不用连接池方式
* 支持异步操作（J2Cache 暂时没用到这个特性）
* 文档非常详尽

#### Lettuce

配置

```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 32 # 连接池最大连接数（使用负值表示没有限制） 默认 8
        max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
        max-idle: 16 # 连接池中的最大空闲连接 默认 8
        min-idle: 8 # 连接池中的最小空闲连接 默认 0
```

#### Jedis

相关依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
        <exclusions>
            <exclusion>
                <groupId>io.lettuce</groupId>
                <artifactId>lettuce-core</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
</dependencies>

```

```yaml
spring:
  redis:
    jedis:
      pool:
        max-active: 8 # 连接池最大连接数
        max-idle: 8 # 连接池最大空闲连接数
        min-idle: 0 # 连接池最小空闲连接数
        max-wait: -1 # 连接池最大阻塞等待时间，负值表示没有限制
```

## 补充
