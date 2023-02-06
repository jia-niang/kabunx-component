## Kabunx V1

本项目主要用于学习，依赖 SpringBoot 开发框架。

辅助开发包：主要针对开发人员进行技术开发支持，提供一系列通用的开发工具包，定义了基础工具类，如配置、缓存、路由、发号器等工具，减少开发人员重复造轮子，帮助提高代码编写效率。

## 技术栈

* 开发框架：Spring Boot 2.3.12.RELEASE
* 微服务框架：Spring Cloud Hoxton.SR12 、Spring Cloud Alibaba 2.2.7.RELEASE
* 安全框架：Spring Security
* 任务调度：Quartz 、 XXL-JOB
* 数据库支持: MySQL
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

|—— kabunx-autoconfigure - 是实现自动配置的核心工程。

|—— kabunx-cache - 二级缓存相关

|—— kabunx-common - 基础通用包

|—— kabunx-dependencies - 依赖版本

|—— kabunx-dev - 本地开发相关

|—— kabunx-elasticsearch - ES 相关（减法）

|—— kabunx-email - 邮件相关

|—— kabunx-jwt - JWT相关

|—— kabunx-log - 业务日志相关

|—— kabunx-mybatis - Mybatis 相关的进一步封装

|—— kabunx-openfeign - 依赖相关

|—— kabunx-parent - Parent 工程依赖

|—— kabunx-redis - Redis 的进一步封装（减法）

|—— kabunx-sass - SaaS 多数据源相关

|—— kabunx-security - Security 的进一步封装

|—— kabunx-sms - 短信相关

|—— kabunx-validation - 常用参数验证器

|—— kabunx-web - Web 的进一步封装

|—— kabunx-starters 自定义启动器

## 技术特色

### 基于 Java 主流开源技术构建

平台基于流行的 J2EE 技术栈，应用稳定的 Spring Cloud 微服务框架进行构建，主流开源的架构给我们带来了以下优势:

* 广泛的业界支持：流行的开源技术都是广泛使用的，是业界默认的标准。
* 提高开发效率：流行的开源技术会有大量的开发人员提供大量个性的解决方案，能更快速的找到满足需求的各种解决方案。
* 提高平台的技术稳定性：流行的开源技术已经通过大量的业务场景验证，保证了技术的成熟性，提高了平台和稳定性。
* 可维护性：流行的开源技术确保了各种社区的活跃度，可以更好的解决平台维护过程中遇到的问题。

### 基于微服务架构设计和扩展

采用微服务架构设计，平台基础模块的每一个应用功能都使用微服务完成：

* 每个服务都有一个清楚的功能边界
* 每一个后台服务开放 REST API，许多服务本身也采用了其它服务提供的 API
* 通过 API Gateway 来统一 API 暴露
* 通过 Nacos 配置中心来统一管理平台服务配置
* 每个微服务可以选择独立部署和弹性资源配置

可以很容易的将业务对象作为基本单元进行纵向拆分，以交互层次作为标准进行横向拆分，从而形成多应用交互的微应用架构体系，有效的实现应用的拆分，实现敏捷开发和部署。

### 架构

架构的意义 就是 要素和结构

1. 要素 是 组成架构的重要元素；
2. 结构 是 要素之间的关系。

而 应用架构的意义 就在于

1. 定义一套良好的结构；
2. 治理应用复杂度，降低系统熵值；
3. 从随心所欲的混乱状态，走向井井有条的有序状态。

## 应用架构的本质

什么是架构？十个人可能有十个回答，架构在技术的语境下，就和架构师一样魔幻。

实际上，定义架构也没那么难，架构的本质，简单来说，就是要素结构。所谓的要素（Components）是指架构中的主要元素，结构是指要素之间的相互关系（Relationship）。

同样，对于应用架构而言，代码是其核心组成要素，结也就构就是这些代码该如何被组织，是要如何处理模块（Module）、组件（Component）、包（Package）和类（Class）之间的关系。简而言之，应用架构就是要解决代码要如何被组织的问题。

一个没有架构的应用系统，就像一堆随意堆放、杂乱无章的玩具，只有熵值，没有熵减。而一个有良好架构的应用系统，有章法、有结构，一切都显得紧紧有条。

好的组织架构会遵循一定的架构模式，大部分的组织都会按职能和业务来设计自己的架构。如果你反其道而行之，硬要把销售、财务和技术人员放在一个部门，就会显得很奇怪。

同样，好的应用架构，也遵循一些共同模式，不管是六边形架构、洋葱圈架构、整洁架构，都提倡以业务为核心，解耦外部依赖，分离业务复杂度和技术复杂度。

应用架构的本质，就是要从繁杂的业务系统中提炼出共性，找到解决业务问题的最佳共同模式，为开发人员提供统一的认知，治理混乱。帮助应用系统“从混乱到有序”，整洁架构就是为此而生，其核心职责就是定义良好的应用结构，提供最佳实践。

### 分层结构

所有的复杂系统都会呈现出层级结构，管理如此，软件设计也不例外，你能想象如果网络协议不是四层，而是一层，意味着，你要在应用层去处理链路层的bit数据流会是怎样的情景吗？同样，应用系统处理复杂业务逻辑也应该是分层的，下层对上层屏蔽处理细节，每一层各司其职，分离关注点，而不是一个
ServiceImpl 解决所有问题。

对于一个典型的业务应用系统来说，DDD 中会做如下层次定义，每一层都有明确的职责定义：

1. 适配层（Adapter Layer）：负责对前端展示（web，wireless，wap）的路由和适配，对于传统B/S系统而言，adapter就相当于MVC中的controller；
2. 应用层（Application Layer）：主要负责获取输入，组装上下文，参数校验，调用领域层做业务处理，如果需要的话，发送消息通知等。层次是开放的，应用层也可以绕过领域层，直接访问基础实施层；
3. 领域层（Domain Layer）：主要是封装了核心业务逻辑，并通过领域服务（Domain Service）和领域对象（Domain
   Entity）的方法对App层提供业务实体和业务逻辑计算。领域是应用的核心，不依赖任何其他层次；
4. 基础实施层（Infrastructure
   Layer）：主要负责技术细节问题的处理，比如数据库的 CRUD 、搜索引擎、文件系统、分布式服务的 RPC 等。此外，领域防腐的重任也落在这里，外部依赖需要通过
   gateway 的转义处理，才能被上面的 App 层和 Domain 层使用。

#### 包结构

所谓的内聚，就是把功能类似的玩具放在一个盒子里，这样可以让应用结构清晰，极大的降低系统的认知成本和维护成本。

各个包结构的简要功能描述，如下表所示：

| 层次         | 包名           | 功能                             | 必选  |
|------------|--------------|--------------------------------|-----|
| Adapter 层  | web          | 处理页面请求的 controller             | 否   |
| Adapter 层  | wap          | 处理 wap 端的适配	                   | 否   |
| App 层      | service      | 处理 request，包括 command 和 query	 | 是   |
| App 层      | job          | 处理定时任务	                        | 否   |
| Domain 层   | model	       | 领域模型	                          | 是   |
| Domain 层   | gateway      | 领域网关，解耦利器	                     | 是   |
| Infra 层    | acl          | 防腐层	                           | 否   |
| Infra 层    | gateway.impl | 网关实现	                          | 是   |
| Infra 层    | mapper       | ibatis 数据库映射	                  | 否   |
| Infra 层    | config       | 配置信息	                          | 否   |
| Client SDK | api          | 服务对外透出的 API	                   | 是   |
| Client SDK | dto	         | 服务对外的 DTO	                     | 是   |

### 解耦

“高内聚，低耦合”这句话，你工作的越久，就越会觉得其有道理。

所谓耦合就是联系的紧密程度，只要有依赖就会有耦合，不管是进程内的依赖，还是跨进程的 RPC
依赖，都会产生耦合。依赖不可消除，同样，耦合也不可避免。我们所能做的不是消除耦合，而是把耦合降低到可以接受的程度。在软件设计中，有大量的设计模式，设计原则都是为了解耦这一目的。

在 DDD 中有一个很棒的解耦设计思想——防腐层（Anti-Corruption），简单说，就是应用不要直接依赖外域的信息，要把外域的信息转换成自己领域上下文（Context）的实体再去使用，从而实现本域和外部依赖的解耦。

在简洁架构中，我们把 AC 这个概念进行了泛化，将数据库、搜索引擎等数据存储都列为外部依赖的范畴。利用依赖倒置，统一使用 gateway
来实现业务领域和外部依赖的解耦。

## CQRS 概述

CQRS 是 Command and Query Responsibility Segregation （命令查询职责分离）的缩写。

它是一种将数据存储的读取操作和更新操作分离的模式。 在应用程序中实现 CQRS 可以最大限度地提高其性能、可缩放性和安全性。 通过迁移到
CQRS 而创建的灵活性使系统能够随着时间的推移而更好地发展，并防止更新命令在域级别导致合并冲突。

其基本思想在于，任何一个对象的方法可以分为两大类：

* 命令(Command):不返回任何结果(void)，但会改变对象的状态。
* 查询(Query):返回结果，但是不会改变对象的状态，对系统没有副作用。

### 上下文和问题

在传统的体系结构中，使用同一数据模型查询和更新数据库。 这十分简单，非常适用于基本的 CRUD 操作。 但是，在更复杂的应用程序中，此方法会变得难以操作。
例如，在读取方面，应用程序可能执行大量不同的查询，返回具有不同形状的数据传输对象 (DTO)。 对象映射可能会变得复杂。
在写入方面，模型可能实施复杂验证和业务逻辑。 结果，模型执行太多操作，过度复杂。

读取和写入工作负载通常是非对称的，两者的性能和缩放要求有很大的差异。

* 数据的读取和写入表示形式之间通常不匹配，例如必须正确更新的附加列或属性（即使它们不需要是操作的一部分）。
* 对同一组数据并行执行操作时，可能会发生数据争用。
* 由于数据存储和数据访问层上的负载以及检索消息所需查询的复杂性，传统方法可能对性能造成负面影响。
* 管理安全性和权限可能变得复杂，因为每个实体同时受读取和写入操作的影响，这可能会在错误的上下文中公开数据。

### 解决方案

CQRS 将读取和写入分离到不同的模型，使用命令来更新数据，使用查询来读取数据。

* 命令应基于任务，而不是以数据为中心。 （“预订酒店客房”，而不是“将 ReservationStatus 设置为 Reserved”）。
* 可将命令排入队列，以进行异步处理而不是同步处理。
* 查询从不修改数据库。 查询返回的 DTO 不封装任何域知识。

CQRS 的好处包括：

* 独立缩放。 CQRS 允许读取和写入工作负载独立缩放，这可能会减少锁争用。
* 优化的数据架构。 读取端可使用针对查询优化的架构，写入端可使用针对更新优化的架构。
* 安全性。 更轻松地确保仅正确的域实体对数据执行写入操作。
* 关注点分离。 分离读取和写入端可使模型更易维护且更灵活。 大多数复杂的业务逻辑被分到写模型。 读模型会变得相对简单。
* 查询更简单。 通过将具体化视图存储在读取数据库中，应用程序可在查询时避免复杂联接。