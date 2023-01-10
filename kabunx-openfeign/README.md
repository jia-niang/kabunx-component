## 背景

最近在做微服务的集成，为了解决各服务间的rpc调用问题，使用到了openFeign，
虽然是简单地在springboot项目中集成openFeign，但是里面其实还是有很多需要注意的点，
下面就依次列举出来。

### 使用openfeign

依赖引入后，我们需要使用openFeign的功能，首先需要在启动类上添加@EnableFeignClients注解；

### 连接池

默认的情况下，openFeign使用的上是HttpURLConnection发起请求，具体代码可查看feign.Client.Default类实现，
也就是说，openFeign每次需要创建一个新的请求，而不是使用的链接池，所以我们的需要替换掉这个默认的实现，改用一个有链接池的实现

#### 添加依赖

我们打算把HttpURLConnection实现替换成okhttp的实现
<!-- 替换默认的HttpURLConnection，改为okhttp，并添加链接池-->

```xml

<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
</dependency>
```

通过javaConfig的方式把okhttp的实现引入进来

更改配置

```yaml
feign:
  # 不使用httpclient，改用okhttp
  httpclient:
    enabled: false
  okhttp:
    enabled: true
    # 是否禁用重定向
    follow-redirects: true
    connect-timeout: 5000
    # 链接失败是否重试
    retry-on-connection-failure: true
    read-timeout: 5000
    write-timeout: 5000
    # 最大空闲数量
    max-idle-connections: 5
    # 生存时间
    keep-alive-duration: 15000
```

这样我们就把openFeign的请求发送改造成链接池了，避免了每次请求都创建HttpURLConnection对象；

#### 开启请求压缩功能

为了更好地减少请求发送的时间，我们可以针对请求数据进行压缩处理，openFeign也内置了压缩功能，不过需要我们自己开启：

```yaml
feign:
  # 开启压缩功能
  compression:
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:
      enabled: true
```

#### 配置超时时间

我们还可以给指定的FeignClient指定对应的超时时间，因为并不是所有的服务超时时间都是统一的，有些特殊的业务场景需要针对性地设置超时时间：

```yaml
feign:
  client:
    config:
      # 设置超时，囊括了okhttp的超时，okhttp属于真正执行的超时，openFeign属于服务间的超时
      # 设置全局超时时间
      default:
        connectTimeout: 2000
        readTimeout: 5000
        # 针对特定contextId设置超时时间
      ucServer:
        connectTimeout: 1000
        readTimeout: 2000
```

### LoadBalancer简介

LoadBalancer是Spring Cloud官方提供的负载均衡组件，可用于替代Ribbon。其使用方式与Ribbon基本兼容，可以从Ribbon进行平滑过渡。

LoadBalancer为了提高性能，不会在每次请求时去获取实例列表，而是将服务实例列表进行了本地缓存。

默认的缓存时间为35s，为了减少服务不可用还会被选择的可能性，我们可以进行如下配置。

```yaml
spring:
  cloud:
    loadbalancer:
      cache: # 负载均衡缓存配置
        enabled: true # 开启缓存
        ttl: 5s # 设置缓存时间
        capacity: 256 # 设置缓存大小
```

## 服务雪崩效应及容错方案

### 雪崩效应

雪崩是一种自然现象，当山坡积雪内部的内聚力抗拒不了它所受到的重力拉引时，便向下滑动，引起大量的雪体崩溃。

#### 服务雪崩

### Hystrix

在微服务架构中，服务与服务之间通过远程调用的方式进行通信，一旦某个被调用的服务发生了故障，其依赖服务也会发生故障，此时就会发生故障的蔓延，最终导致系统瘫痪。Hystrix实现了断路器模式，当某个服务发生故障时，通过断路器的监控，给调用方返回一个错误响应，而不是长时间的等待，这样就不会使得调用方由于长时间得不到响应而占用线程，从而防止故障的蔓延。
Hystrix具备服务降级、服务熔断、线程隔离、请求缓存、请求合并及服务监控等强大功能。

#### Hystrix 状态

Hystrix 不再处于积极开发阶段，目前处于维护模式

#### Hystrix 有什么用？

Hystrix 旨在执行以下操作：

通过第三方客户端库访问（通常通过网络）的依赖项，提供对延迟和故障的保护和控制。
解决复杂分布式系统中的级联故障。
快速失败并快速恢复。
在可能的情况下回退并优雅地降级。
实现近乎实时的监控、警报和操作控制。

#### 如何使用

低版本的 OpenFeign默认引入了Hystrix包，主需要配置就可以开启了：

```yaml
feign:
  hystrix:
    enabled: true
# 设置hystrix超时时间（如果不配置默认为1000毫秒）
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 5000
```

在配置Feign 客户端配置Hystrix时，可以指定fallback或者fallbackFactory。

#### 方式1 直接实现Feign 客户端接口

然后在@FeignClient中指定fallback属性为上面的类即可。

```java
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "x", fallback = "")
```

#### 方式2 实现FallbackFactory 接口

还通过实现FallbackFactory接口，指定泛型为Feign 接口，然后实现其方法。使用工厂类，可以获取到当前发生的异常信息。

然后配置下fallbackFactory属性就可以了。

```java
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "x", fallbackFactory = "")
```