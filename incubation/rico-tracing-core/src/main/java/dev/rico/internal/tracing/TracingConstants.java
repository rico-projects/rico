package dev.rico.internal.tracing;

public interface TracingConstants {

    String TRACE_ID_HEADER = "X-B3-TraceId";

    String SPAN_ID_HEADER = "X-B3-SpanId";

    String PARENT_SPAN_ID_HEADER = "X-B3-ParentSpanId";

    String SAMPLED_HEADER = "X-B3-Sampled";

    String DEBUG_HEADER = "X-B3-Flags";
}
