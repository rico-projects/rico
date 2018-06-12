package dev.rico.internal.client.tracing;

import dev.rico.client.Client;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.context.ContextManager;
import dev.rico.core.http.HttpClient;
import dev.rico.core.spi.DependsOn;
import dev.rico.internal.client.AbstractServiceProvider;
import dev.rico.internal.tracing.TracingBootstrap;
import dev.rico.internal.tracing.TracingConstants;
import dev.rico.internal.tracing.service.ConsoleReporter;
import dev.rico.internal.tracing.service.HttpSender;
import dev.rico.internal.tracing.service.LogReporter;
import dev.rico.internal.tracing.service.NoopReporter;
import dev.rico.tracing.Tracer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;

import java.net.URI;
import java.net.URISyntaxException;

import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_DEFAULT;
import static dev.rico.internal.core.RicoConstants.APPLICATION_NAME_PROPERTY;
import static dev.rico.internal.tracing.TracingConstants.REPORTER_TYPE_CONFIG_PROPERTY;
import static dev.rico.internal.tracing.TracingConstants.REPORTER_TYPE_NONE;

@DependsOn({ContextManager.class, HttpClient.class})
public class TracerProvider extends AbstractServiceProvider<Tracer> {

    public TracerProvider() {
        super(Tracer.class);
    }

    @Override
    protected Tracer createService(ClientConfiguration configuration) {
        final String appName = configuration.getProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME_DEFAULT);

        final String senderType = configuration.getProperty(REPORTER_TYPE_CONFIG_PROPERTY, REPORTER_TYPE_NONE);

        if (senderType.equalsIgnoreCase(TracingConstants.REPORTER_TYPE_HTTP)) {
            try {
                final URI endpoint = new URI(configuration.getProperty(TracingConstants.REPORTER_TYPE_HTTP_ENDPOINT_PROPERTY));
                final HttpClient httpClient = Client.getService(HttpClient.class);
                final Sender sender = new HttpSender(endpoint, httpClient);
                final AsyncReporter<Span> reporter = AsyncReporter.create(sender);
                final ContextManager contextManager = Client.getService(ContextManager.class);
                final TracingBootstrap tracingBootstrap = new TracingBootstrap(appName, reporter, contextManager);
                return tracingBootstrap.getTracer();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Can not generate URI for trace endpoint", e);
            }
        }

        if (senderType.equalsIgnoreCase(TracingConstants.REPORTER_TYPE_LOG)) {
            final AsyncReporter<Span> reporter = new LogReporter();
            final ContextManager contextManager = Client.getService(ContextManager.class);
            final TracingBootstrap tracingBootstrap = new TracingBootstrap(appName, reporter, contextManager);
            return tracingBootstrap.getTracer();
        }

        if (senderType.equalsIgnoreCase(TracingConstants.REPORTER_TYPE_CONSOLE)) {
            final AsyncReporter<Span> reporter = new ConsoleReporter();
            final ContextManager contextManager = Client.getService(ContextManager.class);
            final TracingBootstrap tracingBootstrap = new TracingBootstrap(appName, reporter, contextManager);
            return tracingBootstrap.getTracer();
        }

        final ContextManager contextManager = Client.getService(ContextManager.class);
        final TracingBootstrap tracingBootstrap = new TracingBootstrap(appName, new NoopReporter(), contextManager);
        return tracingBootstrap.getTracer();

    }
}
