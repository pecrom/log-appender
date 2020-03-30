package com.task.roman.log.appender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.task.roman.log.appender.dto.LogMessage;
import com.task.roman.log.appender.mock.MockElasticAppender;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ElasticAppenderTest {

    @DisplayName("Append without exception")
    @Test
    void appendWithoutException() throws IOException {
        String loggerName = "Logger name";
        String formattedMessage = "formatted message";

        // mocking
        Message message = mock(Message.class);
        RestHighLevelClient elasticClient = mock(RestHighLevelClient.class);
        LogEvent logEvent = new Log4jLogEvent(loggerName, null, null, Level.DEBUG, message, null, null);

        // preparing mock objects
        when(message.getFormattedMessage())
                .thenReturn(formattedMessage);

        ArgumentCaptor<IndexRequest> searchRequest = ArgumentCaptor.forClass(IndexRequest.class);

        // testing
        ElasticAppender elasticAppender = new MockElasticAppender(new ObjectMapper(), elasticClient, "ElasticAppender", mock(Filter.class));
        elasticAppender.append(logEvent);

        // asserting
        verify(elasticClient).index(searchRequest.capture(), any());
        LogMessage actual = new ObjectMapper().readValue(searchRequest.getValue().source().utf8ToString(), LogMessage.class);

        assertEquals(Level.DEBUG.toString(), actual.getLevel());
        assertEquals(loggerName, actual.getLoggerName());
        assertEquals(formattedMessage, actual.getMessage());
    }

}