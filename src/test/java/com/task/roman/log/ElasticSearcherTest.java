package com.task.roman.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.task.roman.log.appender.dto.LogMessage;
import com.task.roman.log.mock.MockElasticSearcher;
import com.task.roman.log.searcher.ElasticSearcher;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElasticSearcherTest {
    private static final int NUMBER_OF_RESULTS = 3;

    private static final String SEARCH_TERM = "test";

    @DisplayName("Search with results")
    @Test
    void searchWithResults() throws IOException {
        // mocking
        RestHighLevelClient elasticClient = mock(RestHighLevelClient.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit[] foundHits = createSearchHitArray(NUMBER_OF_RESULTS);

        // preparing mock objects
        when(searchHits.getHits())
                .thenReturn(foundHits);

        when(searchResponse.getHits())
                .thenReturn(searchHits);

        when(elasticClient.search(any(SearchRequest.class), any(RequestOptions.class)))
                .thenReturn(searchResponse);

        // running tested code
        ElasticSearcher elasticSearcher = new MockElasticSearcher(elasticClient, objectMapper);
        List<LogMessage> searchResult = elasticSearcher.searchInMessage(SEARCH_TERM);

        // assertions
        assertEquals(NUMBER_OF_RESULTS, searchResult.size());
    }

    @DisplayName("Search with no results")
    @Test
    void searchWithNoResults() throws IOException {
        // mocking
        RestHighLevelClient elasticClient = mock(RestHighLevelClient.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);

        // preparing mock objects
        when(searchHits.getHits())
                .thenReturn(null);

        when(searchResponse.getHits())
                .thenReturn(searchHits);

        when(elasticClient.search(any(SearchRequest.class), any(RequestOptions.class)))
                .thenReturn(searchResponse);

        // running tested code
        ElasticSearcher elasticSearcher = new MockElasticSearcher(elasticClient, objectMapper);
        List<LogMessage> searchResult = elasticSearcher.searchInMessage(SEARCH_TERM);

        // assertions
        assertTrue(CollectionUtils.isEmpty(searchResult));
    }

    @DisplayName("Search with exception")
    @Test
    void searchWithException() throws IOException {
        // mocking
        RestHighLevelClient elasticClient = mock(RestHighLevelClient.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);

        // preparing mock objects
        when(elasticClient.search(any(SearchRequest.class), any(RequestOptions.class)))
                .thenThrow(IOException.class);

        // running tested code
        ElasticSearcher elasticSearcher = new MockElasticSearcher(elasticClient, objectMapper);
        List<LogMessage> searchResult = elasticSearcher.searchInMessage(SEARCH_TERM);

        // assertions
        assertTrue(CollectionUtils.isEmpty(searchResult));
    }

    private SearchHit[] createSearchHitArray(int count) {
        SearchHit[] searchHits = new SearchHit[count];

        for (int idx = 0; idx < count; idx++) {
            searchHits[idx] = createSearchHit();
        }

        return searchHits;
    }

    private SearchHit createSearchHit() {
        SearchHit searchHit = mock(SearchHit.class);

        when(searchHit.getSourceAsString())
                .thenReturn("{\"level\":\"DEBUG\",\"loggerName\":\"com.task.roman.log.appender.ElasticAppender\",\"message\":\"test\",\"eventTime\":\"2019-12-16T23:04:25.659326900+01:00\"}");

        return searchHit;
    }


}