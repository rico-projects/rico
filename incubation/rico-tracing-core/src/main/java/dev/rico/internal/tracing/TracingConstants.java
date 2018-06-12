package dev.rico.internal.tracing;

public interface TracingConstants {

    String TRACE_ID_HEADER = "X-B3-TraceId";

    String SPAN_ID_HEADER = "X-B3-SpanId";

    String PARENT_SPAN_ID_HEADER = "X-B3-ParentSpanId";

    String SAMPLED_HEADER = "X-B3-Sampled";

    String DEBUG_HEADER = "X-B3-Flags";

    String REPORTER_TYPE_CONFIG_PROPERTY = "tracing.reporter";

    String REPORTER_TYPE_NONE = "none";

    String REPORTER_TYPE_LOG = "log";

    String REPORTER_TYPE_CONSOLE = "console";

    String REPORTER_TYPE_HTTP = "http";

    String REPORTER_TYPE_HTTP_ENDPOINT_PROPERTY = "tracing.reporter.http.endpoint";
}
