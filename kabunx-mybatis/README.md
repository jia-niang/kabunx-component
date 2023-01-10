## MyBatis

ORM 框架的本质是简化操作数据库的编码工作，常用的框架有两个，一个是可以灵活执行动态 SQL 的 MyBatis；一个是崇尚不用写 SQL 的
Hibernate。前者互联网行业用的多，后者传统行业用的多。

### 整合 MyBatis

第一步，在 pom.xml 文件中引入 starter。

```xml

<dependency>
    <groupId>com.kabunx.component</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
```

第二步，在 application.yml 文件中添加数据库连接配置。

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root@123
    url: jdbc:mysql://localhost:3306/db_name?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
```

## SpringBoot 整合 Druid

Druid 是阿里巴巴开源的一款数据库连接池，结合了C3P0、DBCP 等 DB 池的优点，同时还加入了日志监控。

Druid 包含了三个重要的组成部分：

* DruidDriver，能够提供基于 Filter-Chain 模式的插件体系；
* DruidDataSource，高效可管理的数据库连接池；
* SQLParser，支持所有 JDBC 兼容的数据库，包括 Oracle、MySQL 等。

Spring Boot2.0 以上默认使用的是 Hikari 连接池。

那如果我们想使用 Druid 的话，该怎么整合呢？

#### 使用

第一步，在 pom.xml 文件中添加 Druid 的依赖，官方已经提供了 starter，我们直接使用。

```xml

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
</dependency>
```

第二步，在 application.yml 文件中添加 Druid 配置。

```yaml
# 引入 数据库的相关配置
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    # 配置连接池的相关的信息
    druid:
      # 初始化大小
      initial-size: 5
      # 最小连接数
      min-idle: 10
      # 最大连接数
      max-active: 20
      # 获取连接时的最大等待时间
      max-wait: 60000
      # 一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 300000
      # 多久才进行一次检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-millis: 60000
      # 检测连接是否有效的 SQL语句，为空时以下三个配置均无效
      validation-query: SELECT 1
      # 申请连接时执行validationQuery检测连接是否有效，默认true，开启后会降低性能
      test-on-borrow: true
      # 归还连接时执行validationQuery检测连接是否有效，默认false，开启后会降低性能
      test-on-return: true
      # 申请连接时如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效，默认false，建议开启，不影响性能
      test-while-idle: true
```

更多配置
