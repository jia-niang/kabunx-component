## 一、前言

Tomcat 作为 Java Web 程序比较常用的 Servlet 容器实现，在 Web 开发中有比较重要的地位。

## 二、Tomcat使用的IO模式

Tomcat 有三种 IO 模式，BIO、NIO、APR。

## 三、Tomcat主要的配置参数

### 3.1 maxConnections 最大连接数

这个参数是指在同一时间，Tomcat 能够接受的最大连接数。对于 Java 的阻塞式 BIO，默认值是 maxthreads 的值；可以通过配置 Executor
执行器来修改这个值。

对于 Java 新的 NIO 模式，maxConnections 默认值是 10000。

简单来说就是 Tomcat 总共允许建立多少连接。

### 3.2 maxThreads 最大线程数

每一次 Http 请求到达 Web 服务，Tomcat 都会创建一个线程来处理该请求，最大线程数决定了 Web 服务同时可以处理多少请求。maxThreads
默认值为 200，建议增加，但是增加线程是有成本的，更多的线程代表会有更多的上下文切换，也意味着 JVM 会分配更多的内存。

### 3.3 acceptCount 排队连接数

当 Tomcat 的最大连接数 maxConnections 被占满之后，后续的请求就会进行排队，排队的最大数量就是 acceptCount，举个例子，当前
maxConnections 为 10，acceptCount 为 5，并且 maxConnections 已经使用了 10，那么后续的请求就会排队，每来一个请求，acceptCount
就会 +1 ，当 acceptCount 增加到 5 ，在后续的请求就会被直接放弃。

### 3.4 connection-timeout 连接建立时间

HTTP 协议运行在 TCP 之上，所以每次请求到来，客户端和服务端会建立一次 TCP 连接，建立连接需要三次握手，所以就需要一定的时间，connection-timeout
限制了连接建立的时间，当建立连接时间超过这个值，连接就会建立失败。默认为 20000ms。


## 四、高效配置

推荐配置，需要结合业务调整

```yaml
server:
  tomcat:
    accept-count: 100
    threads:
      # 1核2g内存为 200, 2和4G 400, 4核8g内存为800
      max: 400
      # 最小空闲线程数量，保证线程池中一直有最少100个线程，防止流量激增
      min-spare: 100
    # 连接超时时间
    connection-timeout: 2000
    # 最大连接数，可以适应 APR 模式
    max-connections: 8192
```