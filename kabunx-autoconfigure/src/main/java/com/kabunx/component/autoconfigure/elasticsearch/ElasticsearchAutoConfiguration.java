package com.kabunx.component.autoconfigure.elasticsearch;

import com.kabunx.component.elasticsearch.service.DocumentService;
import com.kabunx.component.elasticsearch.service.IndexService;
import com.kabunx.component.elasticsearch.service.SearchService;
import com.kabunx.component.elasticsearch.service.impl.DocumentServiceImpl;
import com.kabunx.component.elasticsearch.service.impl.IndexServiceImpl;
import com.kabunx.component.elasticsearch.service.impl.SearchServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({
        DocumentServiceImpl.class,
        IndexServiceImpl.class,
        SearchServiceImpl.class
})
public class ElasticsearchAutoConfiguration {

    @Bean
    DocumentService documentService(RestHighLevelClient restHighLevelClient) {
        return new DocumentServiceImpl(restHighLevelClient);
    }

    @Bean
    IndexService indexService(RestHighLevelClient restHighLevelClient) {
        return new IndexServiceImpl(restHighLevelClient);
    }

    @Bean
    SearchService searchService(RestHighLevelClient restHighLevelClient) {
        return new SearchServiceImpl(restHighLevelClient);
    }
}
