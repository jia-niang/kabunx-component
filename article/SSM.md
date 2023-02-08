## Spring Boot 服务性能优化

在开始对 SpringBoot 服务进行性能优化之前，你需要做一些准备，把 SpringBoot
服务的一些数据暴露出来。比如，你的服务用到了缓存，就需要把缓存命中率这些数据进行收集；用到了数据库连接池，就需要把连接池的参数给暴露出来。

### 优化思路

对一个普通的 Web 服务来说，我们来看一下，要访问到具体的数据，都要经历哪些主要的环节？

在浏览器中输入相应的域名，需要通过 DNS 解析到具体的 IP 地址上，为了保证高可用，我们的服务一般都会部署多份，然后使用 Nginx
做反向代理和负载均衡。

Nginx 根据资源的特性，会承担一部分动静分离的功能。其中，动态功能部分，会进入我们的SpringBoot 服务。

SpringBoot 默认使用内嵌的 tomcat 作为 Web 容器，使用典型的 MVC 模式，最终访问到我们的数据。

#### HTTP 优化

* 1.使用 CDN 加速文件获取

比较大的文件，尽量使用 CDN（Content Delivery Network）分发，甚至是一些常用的前端脚本、样式、图片等，都可以放到 CDN 上。CDN
通常能够加快这些文件的获取，网页加载也更加迅速。

* 2.合理设置 Cache-Control 值

浏览器会判断 HTTP 头 Cache-Control 的内容，用来决定是否使用浏览器缓存，这在管理一些静态文件的时候，非常有用，相同作用的头信息还有
Expires。Cache-Control 表示多久之后过期；Expires 则表示什么时候过期。

这个参数可以在 Nginx 的配置文件中进行设置。

```text
location ~* ^.+\.(ico|gif|jpg|jpeg|png)$ { 
    # 缓存1年
    add_header Cache-Control: no-cache, max-age=31536000;
}
```

* 3.减少单页面请求域名的数量

减少每个页面请求的域名数量，尽量保证在 4 个之内。这是因为，浏览器每次访问后端的资源，都需要先查询一次 DNS，然后找到 DNS 对应的
IP 地址，再进行真正的调用。

DNS 有多层缓存，比如浏览器会缓存一份、本地主机会缓存、ISP 服务商缓存等。从 DNS 到 IP 地址的转变，通常会花费 20-120ms
的时间。减少域名的数量，可加快资源的获取。

* 4.开启 gzip

开启 gzip，可以先把内容压缩后，浏览器再进行解压。由于减少了传输的大小，会减少带宽的使用，提高传输效率。

在 nginx 中可以很容易地开启，配置如下：

```text
gzip on;
gzip_min_length 1k;
gzip_buffers 4 16k;
gzip_comp_level 6;
gzip_http_version 1.1;
gzip_types text/plain application/javascript text/css;
```

* 5.对资源进行压缩

对 JavaScript 和 CSS，甚至是 HTML 进行压缩。道理类似，现在流行的前后端分离模式，一般都是对这些资源进行压缩的。

* 6.使用 keepalive

由于连接的创建和关闭，都需要耗费资源。用户访问我们的服务后，后续也会有更多的互动，所以保持长连接可以显著减少网络交互，提高性能。

nginx 默认开启了对客户端的 keepalive 支持，你可以通过下面两个参数来调整它的行为。

```text
http {
    keepalive_timeout  120s 120s;
    keepalive_requests 10000;
}
```

nginx 与后端 upstream 的长连接，需要手工开启，参考配置如下：

```text
location ~ /{ 
       proxy_pass http://backend;
       proxy_http_version 1.1;
       proxy_set_header Connection "";
}
```

### 各个层次的优化方向

#### 1. Controller 层

controller 层用于接收前端的查询参数，然后构造查询结果。现在很多项目都采用前后端分离的架构，所以 controller 层的方法，一般会使用
@ResponseBody 注解，把查询的结果，解析成 JSON 数据返回（兼顾效率和可读性）。

由于 controller 只是充当了一个类似功能组合和路由的角色，所以这部分对性能的影响就主要体现在数据集的大小上。如果结果集合非常大，JSON
解析组件就要花费较多的时间进行解析，

大结果集不仅会影响解析时间，还会造成内存浪费。

所以，对于一般的服务，保持结果集的精简，是非常有必要的，这也是 DTO（data transfer
object）存在的必要。如果你的项目，返回的结果结构比较复杂，对结果集进行一次转换是非常有必要的。

#### 2. Service 层

service 层用于处理具体的业务，大部分功能需求都是在这里完成的。service 层一般是使用单例模式（prototype），很少会保存状态，而且可以被
controller 复用。

service 层的代码组织，对代码的可读性、性能影响都比较大。我们常说的设计模式，大多数都是针对 service 层来说的。

service 层会频繁使用更底层的资源，通过组合的方式获取我们所需要的数据，大多数可以通过我们前面课时提供的优化思路进行优化。

分布式事务是性能杀手，因为它要使用额外的步骤去保证一致性，常用的方法有：两阶段提交方案、TCC、本地消息表、MQ 事务消息、分布式事务中间件等。

##### 关于传统事务和柔性事务，我们来简单比较一下。

**ACID**

关系数据库, 最大的特点就是事务处理, 即满足 ACID。

* 原子性（Atomicity）：事务中的操作要么都做，要么都不做。
* 一致性（Consistency）：系统必须始终处在强一致状态下。
* 隔离性（Isolation）：一个事务的执行不能被其他事务所干扰。
* 持久性（Durability）：一个已提交的事务对数据库中数据的改变是永久性的。

**BASE**

BASE 方法通过牺牲一致性和孤立性来提高可用性和系统性能。

BASE 为 Basically Available、Soft-state、Eventually consistent 三者的缩写，其中 BASE 分别代表：

* 基本可用（Basically Available）：系统能够基本运行、一直提供服务。
* 软状态（Soft-state）：系统不要求一直保持强一致状态。
* 最终一致性（Eventual consistency）：系统需要在某一时刻后达到一致性要求。

互联网业务，推荐使用补偿事务，完成最终一致性。比如，通过一系列的定时任务，完成对数据的修复。

#### 3. Dao 层

经过合理的数据缓存，我们都会尽量避免请求穿透到 Dao 层。除非你对 ORM 本身提供的缓存特性特别的熟悉；否则，都推荐你使用更加通用的方式去缓存数据。





