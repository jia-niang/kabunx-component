package com.kabunx.component.log.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BizLog {

    /**
     * 操作日志的文本模板
     *
     * @return 成功模板
     */
    String success();

    /**
     * 操作日志失败的文本版本
     *
     * @return 失败模板
     */
    String error() default "";

    /**
     * 操作日志的执行人
     *
     * @return 执行人
     */
    String operator() default "{#auth.username}";

    /**
     * 操作日志绑定的业务对象标识
     *
     * @return 业务对象标识
     */
    String bizNo() default "";

    /**
     * 保存的操作日志的类型，比如：订单类型、商品类型
     *
     * @return type
     */
    String type() default "";

    /**
     * 操作日志的种类
     *
     * @return 子类
     */
    String subType() default "";

    /**
     * 满足条件才会被执行
     */
    String condition() default "";

    /**
     * 是否启用同步
     */
    boolean sync() default false;
}
