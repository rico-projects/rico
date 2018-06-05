package dev.rico.internal.core.tracing;

import brave.ScopedSpan;
import brave.propagation.TraceContext;
import dev.rico.core.trace.Span;
import dev.rico.core.trace.SpanType;
import dev.rico.core.trace.Tracer;
import dev.rico.internal.core.Assert;

import java.util.WeakHashMap;
import java.util.function.Supplier;

public class TracerImpl implements Tracer {

    private brave.Tracer innerTracer;

    private WeakHashMap<Span, TraceContext> contextMap;

    public TracerImpl(final brave.Tracer innerTracer) {
        this.innerTracer = Assert.requireNonNull(innerTracer, "innerTracer");
        contextMap = new WeakHashMap<>();
    }

    @Override
    public boolean isInSpan() {
        return innerTracer.currentSpan() != null;
    }

    @Override
    public Span startSpan(String name, SpanType type) {
        final brave.Span span = innerTracer.nextSpan().name(name).kind(null).start();
        return new SpanImpl(span);
    }

    @Override
    public Span startChildSpan(Span parent, String name, SpanType type) {
        final TraceContext context = contextMap.get(parent);
        if(context == null) {
            throw new IllegalArgumentException("ERROR");
        }
        final brave.Span span = innerTracer.newChild(context).name(name).kind(null).start();
        return new SpanImpl(span);
    }

    @Override
    public Span getCurrentSpan() {
        final brave.Span span = innerTracer.currentSpan();
        return new SpanImpl(span);
    }

    @Override
    public <T> T runInSpan(final String name, final Supplier<T> task) {
        Assert.requireNonBlank(name, "name");
        Assert.requireNonNull(task, "task");
        final ScopedSpan span = innerTracer.startScopedSpan(name);
        try {
            return task.get();
        } catch (Throwable e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}
