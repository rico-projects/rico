package dev.rico.internal.tracing;

import brave.propagation.TraceContext;
import dev.rico.core.context.ContextManager;
import dev.rico.internal.core.Assert;
import dev.rico.tracing.Span;
import dev.rico.tracing.SpanType;
import dev.rico.tracing.Tracer;

import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class TracerImpl implements Tracer {

    private final brave.Tracer innerTracer;

    private final WeakHashMap<Span, brave.Span> contextMap;

    private final ThreadLocal<brave.Span> currentLocalSpan;

    private final ContextManager contextManager;

    public TracerImpl(final brave.Tracer innerTracer, final ContextManager contextManager) {
        this.innerTracer = Assert.requireNonNull(innerTracer, "innerTracer");
        this.contextManager = Assert.requireNonNull(contextManager, "contextManager");
        currentLocalSpan = new ThreadLocal<>();
        contextMap = new WeakHashMap<>();
    }

    @Override
    public boolean isInSpan() {
        return currentLocalSpan.get() != null;
    }

    @Override
    public Span startSpan(final String name, final SpanType type) {
        Assert.requireNonBlank(name, "name");
        final brave.Span currentSpan = currentLocalSpan.get();
        if(currentSpan != null) {
            return startChildSpan(currentSpan.context(), name, type);
        }
        final brave.Span span = innerTracer.newTrace().name(name).kind(SpanUtils.convert(type));
        span.start();
        return prepairSpan(span);
    }

    @Override
    public Span startChildSpan(final Span parent, final String name, final SpanType type) {
        Assert.requireNonNull(parent, "parent");
        final brave.Span currentSpan = contextMap.get(parent);
        if(currentSpan == null) {
             throw new IllegalStateException("No span open");
        }
        return startChildSpan(currentSpan.context(), name, type);
    }

    private Span startChildSpan(final TraceContext context, final String name, final SpanType type) {
        Assert.requireNonNull(context, "context");
        Assert.requireNonBlank(name, "name");
        final brave.Span span = innerTracer.newChild(context).name(name).kind(SpanUtils.convert(type)).start();
        span.start();
        return prepairSpan(span);
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
        } catch (final Throwable e) {
            span.completeExceptional(e);
            throw e;
        } finally {
            span.complete();
        }
    }

    @Override
    public void runInSpan(final String name, final Runnable task) {
        Assert.requireNonNull(task, "task");
        runInSpan(name, () -> {
            task.run();
            return null;
        });
    }

    public Span setIncomingContext(final long traceId, final Long spanId, final Long parentSpanId) {
        final TraceContext.Builder builder = TraceContext.newBuilder()
                .traceId(traceId)
                .spanId(Optional.ofNullable(spanId).orElse(0L))
                .parentId(Optional.ofNullable(parentSpanId).orElse(0L));
        //TODO: better use brave.Tracer.nextSpan(TraceContextOrSamplingFlags)
        final brave.Span span =  innerTracer.joinSpan(builder.build());
        span.start();
        return prepairSpan(span);
    }

    private Span prepairSpan(final brave.Span braveSpan) {
        final Span span = new SpanImpl(braveSpan);
        contextManager.getGlobalContexts().forEach(c -> span.addContext(c));
        contextManager.getThreadContexts().forEach(c -> span.addContext(c));
        contextMap.put(span, braveSpan);
        currentLocalSpan.set(braveSpan);
        return span;
    }
}
