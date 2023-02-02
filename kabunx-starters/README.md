## Starters 是什么？

Starters 可以理解为启动器，它包含了一系列可以集成到应用里面的依赖包，你可以一站式集成 Spring 及其他技术，而不需要到处找示例代码和依赖包。

Starters 包含了许多项目中需要用到的依赖，它们能快速持续的运行，都是一系列得到支持的管理传递性依赖。

## Starters 命名

Spring Boot 官方的启动器都是以 spring-boot-starter-xx 命名的，代表了一个特定的应用类型。

第三方的启动器不能以 spring-boot 开头命名，它们都被 Spring Boot 官方保留。
一般一个第三方的应该这样命名，像 mybatis的mybatis-spring-boot-starter。

## 自定义 Starter

一个完整的 Spring Boot Starter 需要包含以下组件：

* 包含自动配置代码的自动配置模块
* Starter 模块提供对自动模块的依赖关系，和相关依赖库，以及任何需要用到的依赖。

### 创建

