package com.task.roman.log.searcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.task.roman.log.appender.dto.LogMessage;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;


public class ElasticSearcher implements Closeable {

    // elastic search host scheme
    private static final String ELASTIC_HTTP_HOST_SCHEME = "http";

    // name of the elastic search index
    private static final String INDEX_NAME = "elastic-log";

    // elastic search client
    protected final RestHighLevelClient elasticClient;

    protected final ObjectMapper objectMapper;

    // instance of singleton of ElasticSearcher
    private static ElasticSearcher INSTANCE;

    protected ElasticSearcher(RestHighLevelClient elasticClient, ObjectMapper objectMapper) {
        this.elasticClient = elasticClient;
        this.objectMapper = objectMapper;
    }

    protected ElasticSearcher(String hostname, int port) {
        this(new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, ELASTIC_HTTP_HOST_SCHEME))), new ObjectMapper());
    }

    /**
     * Create instance of {@link ElasticSearcher}
     * @param hostname hostname of elastic search server
     * @param port port of elastic search server
     * @return instance of {@link ElasticSearcher}
     */
    public static ElasticSearcher getInstance(String hostname, int port) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ElasticSearcher(hostname, port);
        }

        return INSTANCE;
    }

    /**
     * Search in messages
     * @param text text which should be searched
     * @return {@link List<LogMessage>} list of log messages
     */
    public List<LogMessage> searchInMessage(String text) {
        return isNotEmpty(text) ? search(text) : Collections.emptyList();
    }


    // search in elastic search
    private List<LogMessage> search(String text) {

        return Try.of(() -> elasticClient.search(createSearchRequest(text), RequestOptions.DEFAULT))
                    .map(this::prepareResponse)
                    .getOrElse(Collections::emptyList);
    }

    // prepare response for the search
    private List<LogMessage> prepareResponse(SearchResponse searchResponse) {
        SearchHit[] hits = searchResponse.getHits().getHits();

        return isNotEmpty(hits) ? handleHits(hits) : Collections.emptyList();
    }

    // process returned hits
    private List<LogMessage> handleHits(SearchHit[] hits) {

        List<LogMessage> foundMessages = new ArrayList<>();

        for (SearchHit hit : hits) {
            Try.of(() -> parseLogMessage(hit))
                    .onSuccess(foundMessages::add);
        }

        return foundMessages;
    }

    // mapping SearchHit to LogMessage object
    private LogMessage parseLogMessage(SearchHit hit) throws JsonProcessingException {
        return objectMapper.readValue(hit.getSourceAsString(), LogMessage.class);
    }

    // prepare request which is sent to elastic search
    private SearchRequest createSearchRequest(String text) {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        // search query
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("message", text));
        sourceBuilder.sort("eventTime");

        return searchRequest.source(sourceBuilder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        elasticClient.close();
    }

}
