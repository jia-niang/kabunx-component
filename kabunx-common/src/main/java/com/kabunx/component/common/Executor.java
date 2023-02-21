package com.kabunx.component.common;

/**
 * 复杂服务业务组合使用
 *
 * @param <R> 返回数据
 * @param <P> 接收参数
 */
public interface Executor<R, P> {

    R exec(P object);
}

