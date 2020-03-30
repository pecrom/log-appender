package com.task.roman.log.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import com.task.roman.log.searcher.ElasticSearcher;

public class MockElasticSearcher extends ElasticSearcher {

    public MockElasticSearcher(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        super(restHighLevelClient, objectMapper);
    }

}
