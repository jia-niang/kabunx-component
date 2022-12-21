package com.kabunx.component.log.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogRecord {
    /**
     * 操作日志的文本模板
     *
     * @return 文本版本
     */
    String success();

    /**
     * 操作日志失败的文本版本
     *
     * @return 文本版本
     */
    String fail() default "";

    /**
     * 操作日志的执行人
     *
     * @return 执行人
     */
    String operator() default "";

    /**
     * 操作日志绑定的业务对象标识
     *
     * @return 业务对象标识
     */
    String bizNo();

    /**
     * 操作日志的种类
     *
     * @return 种类
     */
    String category() default "";

    /**
     * 扩展参数，记录操作日志的修改详情
     *
     * @return 修改详情
     */
    String detail() default "";

    /**
     * 记录日志的条件
     *
     * @return 条件
     */
    String condition() default "";
}
