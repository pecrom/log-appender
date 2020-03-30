package com.task.roman.log.searcher;

import com.task.roman.log.appender.dto.LogMessage;

import java.io.IOException;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws IOException {
        ElasticSearcher searcher = ElasticSearcher.getInstance("localhost", 9200);

        Collection<LogMessage> logMessages = searcher.searchInMessage("dummy message");
        searcher.close();
    }
}
