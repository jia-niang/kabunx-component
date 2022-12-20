package com.kabunx.component.security.service;

import java.util.Map;

/**
 * 根据路由获取角色列表
 */
public interface ResourceService {

    Map<Object, Object> get(String path);
}
