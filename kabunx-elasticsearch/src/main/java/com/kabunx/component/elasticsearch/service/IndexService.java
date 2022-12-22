package com.kabunx.component.elasticsearch.service;

import com.kabunx.component.elasticsearch.exception.ElasticsearchException;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.util.Map;

public interface IndexService {

    boolean create(Class<?> tClass);

    void refresh(Class<?> tClass);

    String create(String index) throws ElasticsearchException;

    String create(String index, String mappings) throws ElasticsearchException;

    boolean exists(Class<?> tClass);

    boolean exists(String index);

    boolean delete(String index);

    boolean putMapping(Class<?> tClass);

    boolean putMappings(String index, String mappings);

    Map<String, MappingMetaData> getMappings(String index) throws ElasticsearchException;
}
