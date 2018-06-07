package dev.rico.internal.core.tracing;

import dev.rico.internal.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class LogReporter extends AsyncReporter<Span> {

    private final static Logger LOG = LoggerFactory.getLogger(LogReporter.class);

    @Override
    public void report(final Span span) {
        Assert.requireNonNull(span, "span");

//        [Trace: d7eeac5e6e335469,
//                Span: c1eeb9cfde9ebef2, Parent: c22aee84089a4640, exportable:true] begin:
//        2017-05-02T07:43:32,337+02:00 end: 2017-05-02T07:43:32,352+02:00 duration:
//        15 tags: {SLEUTH_CASE=4, API_INTERFACE=com.dachser.devtools.pri.api.
//                notification.service.NotificationService, METHOD=recieveMessage,
//                IMPLEMENTATION_CLASS=com.dachser.devtools.pri.backend.notification.service.
//                        NotificationServiceImpl}

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        final String traceId = span.traceId();
        final String spanId = span.id();
        final String parentId = span.parentId();
        final boolean exportable = Optional.ofNullable(span.shared()).orElse(false);
        final LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(span.timestamp()), ZoneId.systemDefault());
        final LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(span.timestamp() + span.duration()), ZoneId.systemDefault());
        final String begin = formatter.format(startTime);
        final String end = formatter.format(endTime);
        final long duration = Optional.ofNullable(span.duration()).orElse(-1L);
        final Map<String, String> tagMap = span.tags();
        final String tags = "{" +  tagMap.keySet()
                .stream()
                .map(k -> k.toString() + "=" + Optional.ofNullable(tagMap.get(k)).map(v -> v.toString()).orElse("null"))
                .reduce("", (a, b) -> a +", " + b) + "}";


        LOG.info("[Trace: {}, Span: {}, Parent: {}, exportable:{}] begin: {} end: {} duration: {} tags: {}", traceId, spanId, parentId, exportable, begin, end, duration, tags);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
