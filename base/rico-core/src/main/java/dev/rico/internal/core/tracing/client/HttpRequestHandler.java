package dev.rico.internal.core.tracing.client;

import dev.rico.core.http.HttpURLConnectionHandler;
import dev.rico.internal.core.Assert;

import java.net.HttpURLConnection;

public class HttpRequestHandler implements HttpURLConnectionHandler {

    private final HttpClientTracingManager manager;

    public HttpRequestHandler(final HttpClientTracingManager manager) {
        this.manager = Assert.requireNonNull(manager, "manager");
    }

    @Override
    public void handle(final HttpURLConnection connection) {
        Assert.requireNonNull(connection, "connection");
        final String name = "/example/123";
        manager.start(name);
    }
}
