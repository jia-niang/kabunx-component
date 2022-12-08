package com.kabunx.component.elasticsearch.service.impl;

import com.kabunx.component.elasticsearch.exception.ElasticsearchException;
import com.kabunx.component.elasticsearch.service.IndexService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.Map;

public class IndexServiceImpl implements IndexService {
    private final RestHighLevelClient client;

    public IndexServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public String create(String index) throws ElasticsearchException {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response.index();
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }

    @Override
    public String create(String index, String mappings) throws ElasticsearchException {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.mapping(mappings, XContentType.JSON);
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response.index();
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }

    @Override
    public boolean exists(String index) {
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException ig) {
            return false;
        }
    }

    @Override
    public boolean delete(String index) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException ig) {
            return false;
        }
    }

    @Override
    public boolean putMappings(String index, String mappings) {
        try {
            PutMappingRequest request = new PutMappingRequest(index);
            request.source(mappings, XContentType.JSON);
            AcknowledgedResponse response = client.indices().putMapping(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException ig) {
            return false;
        }
    }

    @Override
    public Map<String, MappingMetaData> getMappings(String index) throws ElasticsearchException {
        try {
            GetMappingsRequest request = new GetMappingsRequest();
            request.indices(index);
            GetMappingsResponse response = client.indices().getMapping(request, RequestOptions.DEFAULT);
            return response.mappings();
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }
}
