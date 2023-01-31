## Kabunx V1

本项目主要用于学习，依赖SpringBoot开发框架。

辅助开发包：主要针对开发人员进行技术开发支持，提供一系列通用的开发工具包，定义了基础工具类，如配置、缓存、路由、发号器等工具，减少开发人员重复造轮子，帮助提高代码编写效率。

### 概述

架构的意义 就是 要素和结构：

1. 要素 是 组成架构的重要元素；
2. 结构 是 要素之间的关系。

而 应用架构的意义 就在于

1. 定义一套良好的结构；
2. 治理应用复杂度，降低系统熵值；
3. 从随心所欲的混乱状态，走向井井有条的有序状态。

## 技术栈

* 开发框架：Spring Boot 2.3.12.RELEASE
* 微服务框架：Spring Cloud Hoxton.SR12、Spring Cloud Alibaba 2.2.7.RELEASE
* 安全框架：Spring Security + Spring Authorization Server
* 任务调度：Quartz 、 XXL-JOB
* 数据库支持: MySQL、Oracle
* 持久层框架：MyBatis
* 数据库连接池：Druid
* 服务注册与发现: Nacos
* 客户端负载均衡：Spring Cloud Loadbalancer
* 熔断组件：Hystrix
* 网关组件：Spring Cloud Gateway
* 日志管理：Logback
* 运行容器：Docker

## 模块说明

kabunx

|--- kabunx-autoconfigure

|--- kabunx-common

|--- kabunx-dependencies

|--- kabunx-dev

|--- kabunx-elasticsearch

|--- kabunx-email

|--- kabunx-jwt

|--- kabunx-log

|--- kabunx-mybatis

|--- kabunx-openfeign

|--- kabunx-parent

|--- kabunx-redis

|--- kabunx-security

|--- kabunx-sms

|--- kabunx-starters

|- |--- elasticsearch-spring-boot-starter

|- |--- email-spring-boot-starter

|- |---log-spring-boot-starter

|- |---mybatis-spring-boot-starter

|- |---openfeign-spring-boot-starter

|--- kabunx-validation

|--- kabunx-web

## 技术特色

### 基于 Java 主流开源技术构建

平台基于流行的 J2EE 技术栈，应用稳定的 Spring Cloud 微服务框架进行构建，主流开源的架构给我们带来了以下优势:

* 广泛的业界支持：流行的开源技术都是广泛使用的，是业界默认的标准。
* 提高开发效率：流行的开源技术会有大量的开发人员提供大量个性的解决方案，能更快速的找到满足需求的各种解决方案。
* 提高平台的技术稳定性：流行的开源技术已经通过大量的业务场景验证，保证了技术的成熟性，提高了平台和稳定性。
* 可维护性：流行的开源技术确保了各种社区的活跃度，可以更好的解决平台维护过程中遇到的问题。

### 基于微服务架构设计和扩展
