## 过滤器、拦截器、监听器

### 前言

* 过滤器（Filter）：当有一堆请求，只希望符合预期的请求进来。
* 拦截器（Interceptor）：想要干涉预期的请求。
* 监听器（Listener）：想要监听这些请求具体做了什么。

过滤器是在请求进入容器后，但还没有进入 Servlet 之前进行预处理的。

拦截器是在请求进入控制器（Controller） 之前进行预处理的。

过滤器依赖于 Servlet 容器，而拦截器依赖于 Spring 的 IoC 容器，因此可以通过注入的方式获取容器当中的对象。

监听器用于监听 Web 应用中某些对象的创建、销毁、增加、修改、删除等动作，然后做出相应的处理。

### 过滤器

* 过滤敏感词汇（防止sql注入）
* 设置字符编码
* URL级别的权限访问控制
* 压缩响应信息

过滤器的创建和销毁都由 Web 服务器负责，Web 应用程序启动的时候，创建过滤器对象，为后续的请求过滤做好准备。

过滤器可以有很多个，一个个过滤器组合起来就成了 FilterChain，也就是过滤器链。

在 Spring 中，过滤器都默认继承了 OncePerRequestFilter，顾名思义，OncePerRequestFilter 的作用就是确保一次请求只通过一次过滤器，而不重复执行。

### 拦截器

* 登录验证，判断用户是否登录
* 权限验证，判断用户是否有权限访问资源，如校验token
* 日志记录，记录请求操作日志（用户ip，访问时间等），以便统计请求访问量
* 处理cookie、本地化、国际化、主题等
* 性能监控，监控请求处理时长等

一个拦截器必须实现 HandlerInterceptor 接口，preHandle 方法是 Controller 方法调用前执行，postHandle 是 Controller
方法正常返回后执行，afterCompletion 方法无论 Controller 方法是否抛异常都会执行。

只有 preHandle 返回 true 的话，其他两个方法才会执行。

如果 preHandle 返回 false 的话，表示不需要调用Controller方法继续处理了，通常在认证或者安全检查失败时直接返回错误响应。

#### 如何配置

```java

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor()).addPathPatterns("/**");
    }
}
```

无论是过滤器还是拦截器，都属于AOP（面向切面编程）思想的具体实现。除了这两种实现之外，还有另一种更灵活的AOP实现技术，即 Aspect。
具体可以参考AdapterLogAspect的实现。

### 监听器
