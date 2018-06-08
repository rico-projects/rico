package dev.rico.internal.client.tracing.http;

import dev.rico.client.Client;
import dev.rico.core.Configuration;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import dev.rico.core.http.spi.RequestHandlerProvider;
import dev.rico.tracing.Tracer;

public class HttpTracingHandlerProvider implements RequestHandlerProvider {

    @Override
    public HttpURLConnectionInterceptor getHandler(final Configuration configuration) {
        final Tracer tracer = Client.getService(Tracer.class);
        return new HttpTracingHandler(tracer);
    }
}
