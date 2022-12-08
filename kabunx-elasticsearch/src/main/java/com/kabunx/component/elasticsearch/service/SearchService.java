package com.kabunx.component.elasticsearch.service;

import com.kabunx.component.core.dto.Page;
import com.kabunx.component.core.dto.Pagination;
import com.kabunx.component.core.dto.SimplePagination;
import com.kabunx.component.elasticsearch.exception.ElasticsearchException;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

public interface SearchService {
    <T> List<T> filter(Class<T> clazz, SearchSourceBuilder builder) throws ElasticsearchException;

    <T> Pagination<T> paginate(Class<T> clazz, SearchSourceBuilder builder, Page page) throws ElasticsearchException;

    <T> SimplePagination<T> simplePaginate(Class<T> clazz, SearchSourceBuilder builder, Page page) throws ElasticsearchException;

    <T> long count(Class<T> clazz, SearchSourceBuilder builder) throws ElasticsearchException;
}
