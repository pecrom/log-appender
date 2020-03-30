**Task description**
   - try to implement Log4j appender which stores log data into data storage
   - try to implement fulltext search on your log data
   - choose any appropriate data storage
   - describe pros / cons of your solution

**ElasticSearch log appender**

For the log messages storage I have decided to use elastic search. The reason why I have chosen ElasticSearch is, 
that it provides capability of full text searching. So it was just necessary to implement the logic of saving and retrieving messages from the ElasticSearch. 
The rest is handled by Elastic.

**How to run the ElasticAppender**

At first it is necessary to run docker compose file, which contains ElasticSearch.

`cd docker && docker-compose up`

Now you can run 
`com.task.roman.log.appender.Main` which logs a `dummy message` to Elastic. Next you can run `com.task.roman.log.searcher.Main` which gets the message from
the Elastic.
