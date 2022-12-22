package com.kabunx.component.elasticsearch.service;

import com.kabunx.component.elasticsearch.exception.ElasticsearchException;

public interface DocumentService {

    <T> T save(T entity);

    String get(String index, String id) throws ElasticsearchException;

    boolean exists(String index, String id) throws ElasticsearchException;

    boolean update(String index, String id, String doc) throws ElasticsearchException;

    <T> boolean update(String id, T entity) throws ElasticsearchException;

    int delete(String index, String id);
}
