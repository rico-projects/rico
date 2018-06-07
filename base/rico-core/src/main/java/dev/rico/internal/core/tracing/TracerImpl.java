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

    private WeakHashMap<Span, brave.Span> contextMap;

    private ThreadLocal<brave.Span> currentLocalSpan;

    public TracerImpl(final brave.Tracer innerTracer) {
        this.innerTracer = Assert.requireNonNull(innerTracer, "innerTracer");
        currentLocalSpan = new ThreadLocal<>();
        contextMap = new WeakHashMap<>();
    }

    @Override
    public boolean isInSpan() {
        return currentLocalSpan.get() != null;
    }

    @Override
    public Span startSpan(String name, SpanType type) {
        final brave.Span currentSpan = currentLocalSpan.get();
        if(currentSpan != null) {
            return startChildSpan(currentSpan.context(), name, type);
        }
        final brave.Span span = innerTracer.newTrace().name(name).kind(SpanUtils.convert(type));
        span.start();
        Span result = new SpanImpl(span);
        contextMap.put(result, span);
        currentLocalSpan.set(span);
        return result;
    }

    @Override
    public Span startChildSpan(Span parent, String name, SpanType type) {
        Assert.requireNonNull(parent, "parent");
        final brave.Span currentSpan = contextMap.get(parent);
        if(currentSpan == null) {
             throw new IllegalStateException("No span open");
        }
        return startChildSpan(currentSpan.context(), name, type);
    }

    private Span startChildSpan(TraceContext context, String name, SpanType type) {
        Assert.requireNonNull(context, "context");
        final brave.Span span = innerTracer.newChild(context).name(name).kind(SpanUtils.convert(type)).start();
        Span result = new SpanImpl(span);
        contextMap.put(result, span);
        currentLocalSpan.set(span);
        return result;
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
        final Span span = startSpan(name, SpanType.LOCAL);
        try {
            return task.get();
        } catch (Throwable e) {
            span.completeExceptional(e);
            throw e;
        } finally {
            span.complete();
        }
    }

    @Override
    public void runInSpan(String name, Runnable task) {
        runInSpan(name, () -> {
            task.run();
            return null;
        });
    }
}
