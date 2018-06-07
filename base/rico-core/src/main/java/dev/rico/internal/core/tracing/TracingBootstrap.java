package dev.rico.internal.core.tracing;

import brave.Tracer;
import brave.Tracing;
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

    public TracingBootstrap(final String appName, final Sender sender) {
        this(appName, AsyncReporter.create(Assert.requireNonNull(sender, "sender")));
    }

    public TracingBootstrap(final String appName, final AsyncReporter<Span> reporter) {
        Assert.requireNonBlank(appName, "appName");
        this.reporter = Assert.requireNonNull(reporter, "reporter");
        this.tracing = Tracing.newBuilder()
                .localServiceName(appName)
                .spanReporter(reporter).build();

        innerTracer = tracing.tracer();
    }

    public Tracer getInnerTracer() {
        return innerTracer;
    }

    public TracerImpl getTracer() {
        return new TracerImpl(innerTracer);
    }

    @Override
    public void close() throws IOException {
        tracing.close();
        reporter.close();
    }
}
