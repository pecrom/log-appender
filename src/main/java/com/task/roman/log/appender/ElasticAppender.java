package com.task.roman.log.appender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import com.task.roman.log.appender.dto.LogMessage;


@Plugin(name = "ElasticAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class ElasticAppender extends AbstractAppender {

    // name of the index in elastic search
    private static final String INDEX_NAME = "elastic-log";

    private static final String ELASTIC_HTTP_HOST_SCHEME = "http";

    // client which communicates with elastic search
    private final RestHighLevelClient elasticClient;

    // converts objects to json
    private final ObjectMapper objectMapper;

    protected ElasticAppender(ObjectMapper objectMapper, RestHighLevelClient elasticClient, String name, Filter filter) {
        super(name, filter, null, true, null);

        this.objectMapper = objectMapper;
        this.elasticClient = elasticClient;
    }

    protected ElasticAppender(String name, String hostname, int port, Filter filter) {
        this(new ObjectMapper(), new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, ELASTIC_HTTP_HOST_SCHEME))), name, filter);
    }

    /**
     * Factory creating {@link ElasticAppender}
     *
     * @param name name of the appender
     * @param hostname hostname of elastic search server
     * @param port port of the elastic search server
     * @param filter filter
     * @return instance of {@link ElasticAppender}
     */
    @PluginFactory
    public static ElasticAppender createAppender(@PluginAttribute("name") String name,
                                                 @PluginAttribute("hostname") String hostname,
                                                 @PluginAttribute("port") int port,
                                                 @PluginElement("Filter") Filter filter) {

        return new ElasticAppender(name, hostname, port, filter);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void append(LogEvent logEvent) {
        try {
            IndexRequest request = createRequest(new LogMessage(logEvent));
            elasticClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            // blank because this appender ignores exceptions
        }
    }

    // creates elastic search index request
    private IndexRequest createRequest(LogMessage logMessage) throws JsonProcessingException {
        IndexRequest request = new IndexRequest(INDEX_NAME);

        String logMessageJson = objectMapper.writeValueAsString(logMessage);
        request.source(logMessageJson, XContentType.JSON);

        return request;
    }
}
