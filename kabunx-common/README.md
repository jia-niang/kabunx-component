## Java术语（PO/POJO/VO/BO/DAO/DTO）

最近在写代码的时候，对于Entity、VO、DTO、BO的概念有些混淆，不太了解具体的用途以及作用，所以想梳理一下。

阿里巴巴 Java 开发手册中的分层领域模型规约：

* DO（Data Object）：此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
* DTO（Data Transfer Object）：数据传输对象，Service 或 Manager 向外传输的对象。
* BO（Business Object）：业务对象，可以由 Service 层输出的封装业务逻辑的对象。
* Query：数据查询对象，各层接收上层的查询请求。注意超过 2 个参数的查询封装，禁止使用 Map 类 来传输。
* VO（View Object）：显示层对象，通常是 Web 向模板渲染引擎层传输的对象。

分包没有对错之分，下面采用的分包方式与阿里巴巴 Java 开发手册中定义的规范略有不同。

* DTO : 数据传输对象，Service 或 Manager 向外传输的对象。
* Entity : 领域对象
* Cmd（Command） : 用于接收前端传递的命令参数
* Event : 用于接收前端传递的事件参数
* Query : 用于接收前端传递查询参数
* PageQuery : 用于接收前端传递的分页查询参数
* RestResponse : 统一响应的数据格式（Json格式）
* Pagination : 用于定义向前端返回的分页数据
* SimplePagination : 用于定义向前端返回的简单分页数据
* Resource : 用于定义向前端返回的资源信息
* OptionResource : 特定的可选资源信息

## TTL

### 需求场景

ThreadLocal的需求场景即TransmittableThreadLocal的潜在需求场景，如果你的业务需要『在使用线程池等会池化复用线程的执行组件情况下传递ThreadLocal值』则是TransmittableThreadLocal目标场景。

#### 1. 分布式跟踪系统

分布式跟踪系统作为基础设施，不会限制『使用线程池等会池化复用线程的组件』，并期望对业务逻辑尽可能的透明。

#### 2. 日志收集记录系统上下文

由于不限制用户应用使用线程池，系统的上下文需要能跨线程的传递，且不影响应用代码。

#### 3.应用容器或上层框架跨应用代码给下层SDK传递信息

应用代码会使用线程池，并且这样的使用是正常的业务需求。上下文信息需要传递到下层 SDK，要支持这样的用法。

## Lombok

### 优势

使用 Lombok 提供的注解大大减少了代码量，使代码非常简洁，这也是很多开发者热衷于使用 Lombok 的主要原因。

其他：

* 减少模板代码：Lombok 处理 get，set，toString，hash，equal 等方法，大量的模板代码进行封装，减少重复代码，当增加新属性的时候，以上方法都不需要再重新编写；
* 增强代码可读性：专注于类的属性定义，不需要再去为排版浪费时间；
* 减少代码维护：新增属性的时候，会减少非常多的代码维护工作。

### 劣势

* 强迫队友
* 代码可调试性降低
* 影响版本升级

## Mapstruct

### JavaBean 的困扰

数据传输对象(Data Transfer Objects, DTO)
经常被用于这些应用中。DTO只是持有另一个对象中被请求的信息的对象。通常情况下，这些信息是有限的一部分。例如，在持久化层定义的实体和发往客户端的DTO之间经常会出现相互之间的转换。由于DTO是原始对象的反映，因此这些类之间的映射器在转换过程中扮演着关键角色。

### MapStruct 带来的改变

MapStruct 是一个生成类型安全， 高性能且无依赖的 JavaBean 映射代码的注解处理器（annotation processor）。

* 注解处理器
* 可以生成 JavaBean 之间那的映射代码
* 类型安全， 高性能， 无依赖性

### MapStruct 入门


