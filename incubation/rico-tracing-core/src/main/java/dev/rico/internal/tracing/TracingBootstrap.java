package dev.rico.internal.tracing;

import brave.Tracer;
import brave.Tracing;
import dev.rico.core.context.ContextManager;
import dev.rico.internal.core.Assert;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

import java.io.Closeable;
import java.io.IOException;

public class TracingBootstrap implements Closeable {

    private final Tracing tracing;

    private final AsyncReporter<Span> reporter;

    private final Tracer innerTracer;

    private final TracerImpl tracer;

    public TracingBootstrap(final String appName, final Sender sender, final ContextManager contextManager) {
        this(appName, AsyncReporter.create(Assert.requireNonNull(sender, "sender")), contextManager);
    }

    public TracingBootstrap(final String appName, final AsyncReporter<Span> reporter, final ContextManager contextManager) {
        Assert.requireNonBlank(appName, "appName");
        this.reporter = Assert.requireNonNull(reporter, "reporter");
        this.tracing = Tracing.newBuilder()
                .localServiceName(appName)
                .spanReporter(reporter).build();

        innerTracer = tracing.tracer();
        tracer = new TracerImpl(innerTracer, contextManager);
    }

    public Tracer getInnerTracer() {
        return innerTracer;
    }

    public TracerImpl getTracer() {
        return tracer;
    }

    @Override
    public void close() throws IOException {
        tracing.close();
        reporter.close();
    }
}
