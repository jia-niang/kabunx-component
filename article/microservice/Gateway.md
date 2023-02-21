## 网关存在的意义

API 网关是一个服务器，是系统流量的唯一入口。从面向对象设计的角度看，它与外观模式类似。API 网关封装了系统内部架构，为每个客户端提供一个定制的
API。它可能还具有其它职责，如身份验证、监控、负载均衡、缓存、请求分片与管理、静态响应处理。API
网关方式的核心要点是，所有的客户端和消费端都通过统一的网关接入微服务，在网关层处理所有的非业务功能。通常，网关也是提供
REST/HTTP 的访问 API。

网关应当具备以下功能：

* 性能：API 高可用，负载均衡，容错机制。
* 安全：权限身份认证、脱敏，流量清洗，后端签名（保证全链路可信调用）,黑名单（非法调用的限制）。
* 日志：日志记录（traceId）一旦涉及分布式，全链路跟踪必不可少。
* 缓存：数据缓存。
* 监控：记录请求响应数据，api耗时分析，性能监控。
* 限流：流量控制，错峰流控，可以定义多种限流规则。
* 灰度：线上灰度部署，可以减小风险。
* 路由：动态路由规则。

### 性能

#### 负载均衡

## Spring Cloud Gateway 问题排查

### 熔断

熔断策略主要是线程配置和熔断配置，为了解决网关调用后台服务 Connection prematurely closed
BEFORE response 的问题，要设置后台服务线程的空闲时间和网关线程池线程的空闲时间，并让网关线程池线程的空闲时间小于后台服务的空闲时间

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 200 # 
          max-idle-time: 10000 # 空闲时间
```

#### 降级

Hystrix 降级也使用了线程池

如果上面的限流设置比较大，比如 1000，最大突发 2000，网关调用后台服务发生熔断降级， 熔断后降级的方法调用太频繁，10 个线程不够用，会导致
500 错误：

```yaml
hystrix:
  threadpool:
    group-accept: # 代码里面设置的 HystrixCommandGroupKey.Factory.asKey("group-accept")
      coreSize: 50 # 并发执行的最大线程数，默认10
      maxQueueSize: 1500 # BlockingQueue 的最大队列数
      # 即使 maxQueueSize 没有达到，达到 queueSizeRejectionThreshold 该值后，请求也会被拒绝
      queueSizeRejectionThreshold: 1400
```

### 总结

Spring Cloud Gateway 网关的配置中，需要综合考虑限流大小、网关调用后台连接池设置大小、后台服务的连接池以及空闲时间，包括网关调用降级方法的线程池配置，
都需要在压测中调整到一个合理的配置，才能发挥最大的功效。