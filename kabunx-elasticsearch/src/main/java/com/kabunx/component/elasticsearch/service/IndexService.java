package com.kabunx.component.elasticsearch.service;

import com.kabunx.component.elasticsearch.exception.ElasticsearchException;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.util.Map;

public interface IndexService {
    String create(String index) throws ElasticsearchException;

    String create(String index, String mappings) throws ElasticsearchException;

    boolean exists(String index);

    boolean delete(String index);

    boolean putMappings(String index, String mappings);

    Map<String, MappingMetaData> getMappings(String index) throws ElasticsearchException;
}
