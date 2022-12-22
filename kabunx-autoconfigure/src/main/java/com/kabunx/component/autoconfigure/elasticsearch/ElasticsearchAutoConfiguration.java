package com.kabunx.component.autoconfigure.elasticsearch;

import com.kabunx.component.elasticsearch.service.DocumentService;
import com.kabunx.component.elasticsearch.service.IndexService;
import com.kabunx.component.elasticsearch.service.SearchService;
import com.kabunx.component.elasticsearch.service.impl.DocumentServiceImpl;
import com.kabunx.component.elasticsearch.service.impl.IndexServiceImpl;
import com.kabunx.component.elasticsearch.service.impl.SearchServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
@ConditionalOnBean({RestHighLevelClient.class, ElasticsearchOperations.class})
public class ElasticsearchAutoConfiguration {

    @Bean
    @ConditionalOnClass(DocumentServiceImpl.class)
    DocumentService documentService(RestHighLevelClient client, ElasticsearchOperations operations) {
        return new DocumentServiceImpl(client, operations);
    }

    @Bean
    @ConditionalOnClass(IndexServiceImpl.class)
    IndexService indexService(RestHighLevelClient client, ElasticsearchOperations operations) {
        return new IndexServiceImpl(client, operations);
    }

    @Bean
    @ConditionalOnClass(SearchServiceImpl.class)
    SearchService searchService(RestHighLevelClient client, ElasticsearchOperations operations) {
        return new SearchServiceImpl(client, operations);
    }
}
