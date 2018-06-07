package dev.rico.internal.core.tracing.client;

import dev.rico.core.trace.Span;
import dev.rico.core.trace.SpanType;
import dev.rico.core.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientTracingManager {

    private final static Logger LOG = LoggerFactory.getLogger(HttpClientTracingManager.class);

    private final ThreadLocal<Span> spanHolder;

    private final Tracer tracer;

    public HttpClientTracingManager(final Tracer tracer) {
        this.tracer = tracer;
        this.spanHolder = new ThreadLocal<>();
    }

    public void start(String name) {
        final Span currentSpan = spanHolder.get();
        if (currentSpan != null) {
            LOG.error("Span was not closed!");
            final IllegalStateException exception = new IllegalStateException("Span was not closed!");
            currentSpan.completeExceptional(exception);
        }
        final Span span = tracer.startSpan(name, SpanType.CLIENT);
        spanHolder.set(span);
    }

    public void complete() {
        try {
            final Span span = spanHolder.get();
            if (span == null) {
                throw new IllegalStateException("No Span was started!");
            }
            span.complete();
        } finally {
            spanHolder.set(null);
        }
    }

    public void completeExceptional(final Throwable e) {
        try {
            final Span span = spanHolder.get();
            if (span == null) {
                throw new IllegalStateException("No Span was started!");
            }
            span.completeExceptional(e);
        } finally {
            spanHolder.set(null);
        }
    }
}
