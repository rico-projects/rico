package dev.rico.internal.client.tracing;

import dev.rico.client.Client;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.context.ContextManager;
import dev.rico.core.spi.DependsOn;
import dev.rico.internal.client.AbstractServiceProvider;
import dev.rico.internal.tracing.TracingBootstrap;
import dev.rico.internal.tracing.service.LogReporter;
import dev.rico.tracing.Tracer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;

import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_DEFAULT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_PROPERTY;

@DependsOn(ContextManager.class)
public class TracerProvider extends AbstractServiceProvider<Tracer> {

    public TracerProvider() {
        super(Tracer.class);
    }

    @Override
    protected Tracer createService(ClientConfiguration configuration) {
        final String appName = configuration.getProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME_DEFAULT);
        final AsyncReporter<Span> reporter = new LogReporter();
        final ContextManager contextManager = Client.getService(ContextManager.class);
        final TracingBootstrap tracingBootstrap = new TracingBootstrap(appName, reporter, contextManager);
        return tracingBootstrap.getTracer();
    }
}
