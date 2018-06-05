package dev.rico.internal.core.tracing;

import brave.Tracer;
import brave.Tracing;
import dev.rico.internal.core.Assert;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

import java.io.Closeable;
import java.io.IOException;

public class TracingImpl implements Closeable {

    private final Tracing tracing;

    private final AsyncReporter<Span> reporter;

    private final Sender sender;

    private final Tracer tracer;

    public TracingImpl(final String appName, final Sender sender) {
        Assert.requireNonBlank(appName, "appName");
        this.sender = Assert.requireNonNull(sender, "sender");
        this.reporter = AsyncReporter.create(sender);
        this.tracing = Tracing.newBuilder()
                .localServiceName(appName)
                .spanReporter(reporter).build();

        tracer = tracing.tracer();
    }

    public Tracer getTracer() {
        return tracer;
    }

    @Override
    public void close() throws IOException {
        tracing.close();
        reporter.close();
        sender.close();
    }
}
