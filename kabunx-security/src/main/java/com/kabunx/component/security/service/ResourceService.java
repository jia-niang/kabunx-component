package com.kabunx.component.security.service;

import java.util.List;
import java.util.Map;

/**
 * 根据路由获取角色列表
 */
public interface ResourceService {

    /**
     * @return 资源和角色的映射关系
     */
    Map<String, List<String>> get();

    /**
     * @param path 路径
     * @return 角色的集合
     */
    List<String> get(String path);
}
