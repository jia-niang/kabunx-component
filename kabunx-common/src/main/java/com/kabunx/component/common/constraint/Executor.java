package com.kabunx.component.common.constraint;

/**
 * 复杂服务业务组合使用
 *
 * @param <T> 接收参数
 */
public interface Executor<T> {

    Boolean exec(T object);
}

