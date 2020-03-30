package com.task.roman.log.appender.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.core.LogEvent;

import java.io.Serializable;
import java.time.OffsetDateTime;

@EqualsAndHashCode
@NoArgsConstructor
@Setter
@Getter
public class LogMessage implements Serializable {

    private static final long serialVersionUID = 4654150035861747946L;

    /**
     * Level of log message
     */
    private String level;

    /**
     * Logger name
     */
    private String loggerName;

    /**
     * Message to be logged
     */
    private String message;

    /**
     * When the event occurred
     */
    private String eventTime;

    public LogMessage(LogEvent logEvent) {
        level = logEvent.getLevel().name();
        loggerName = logEvent.getLoggerName();
        message = logEvent.getMessage().getFormattedMessage();
        eventTime = OffsetDateTime.now().toString();
    }

}
