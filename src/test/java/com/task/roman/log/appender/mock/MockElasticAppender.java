package com.task.roman.log.appender.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.Filter;
import org.elasticsearch.client.RestHighLevelClient;
import com.task.roman.log.appender.ElasticAppender;

public class MockElasticAppender extends ElasticAppender {

    public MockElasticAppender(ObjectMapper objectMapper, RestHighLevelClient elasticClient, String name, Filter filter) {
        super(objectMapper, elasticClient, name, filter);
    }
}
