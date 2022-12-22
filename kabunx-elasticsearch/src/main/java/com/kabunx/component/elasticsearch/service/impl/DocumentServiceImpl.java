package com.kabunx.component.elasticsearch.service.impl;

import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.elasticsearch.exception.ElasticsearchException;
import com.kabunx.component.elasticsearch.service.DocumentService;
import com.kabunx.component.elasticsearch.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.io.IOException;

@Slf4j
public class DocumentServiceImpl implements DocumentService {
    private final RestHighLevelClient client;

    private final ElasticsearchOperations operations;

    public DocumentServiceImpl(RestHighLevelClient client, ElasticsearchOperations operations) {
        this.client = client;
        this.operations = operations;
    }

    public <T> T save(T entity) {
        return operations.save(entity);
    }

    @Override
    public String get(String index, String id) throws ElasticsearchException {
        GetRequest request = new GetRequest(index, id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.getSourceAsString();
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }

    @Override
    public boolean exists(String index, String id) throws ElasticsearchException {
        GetRequest request = new GetRequest(index, id);
        try {
            return client.exists(request, RequestOptions.DEFAULT);
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }

    @Override
    public boolean update(String index, String id, String doc) throws ElasticsearchException {
        UpdateRequest request = new UpdateRequest(index, id);
        request.doc(doc, XContentType.JSON);
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            byte op = response.getResult().getOp();
            return op == 1 || op == 4;
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }

    @Override
    public <T> boolean update(String id, T entity) throws ElasticsearchException {
        String index = ReflectionUtils.getIndexName(entity);
        if (index == null) {
            throw new ElasticsearchException("index error");
        }
        String jsonDoc = JsonUtils.object2Json(entity);
        return update(index, id, jsonDoc);
    }

    @Override
    public int delete(String index, String id) {
        DeleteRequest request = new DeleteRequest(index, id);
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            RestStatus status = response.status();
            return status.getStatus();
        } catch (IOException ie) {
            throw new ElasticsearchException(ie.getMessage());
        }
    }
}
