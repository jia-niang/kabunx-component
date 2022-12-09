package com.kabunx.component.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public class AuthContextHolder {
    /**
     * 保存用户的ThreadLocal
     * 在拦截器操作 添加、删除相关用户数据
     */
    private static final ThreadLocal<AuthContext> AUTH_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 添加当前登录用户方法
     * 在拦截器方法执行前调用设置获取用户
     *
     * @param authContext 当前用户
     */
    public static void setCurrentAuth(AuthContext authContext) {
        AUTH_CONTEXT_THREAD_LOCAL.set(authContext);
    }

    /**
     * 获取当前登录用户方法
     */
    public static AuthContext getCurrentAuth() {
        return AUTH_CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * 删除当前登录用户方法
     * 在拦截器方法执行后 移除当前用户对象
     */
    public static void removeCurrentAuth() {
        AUTH_CONTEXT_THREAD_LOCAL.remove();
    }
}
