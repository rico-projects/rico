package dev.rico.internal.tracing.client;

import dev.rico.core.http.HttpException;
import dev.rico.core.http.HttpURLConnectionHandler;
import dev.rico.internal.core.Assert;

import java.net.HttpURLConnection;

public class HttpResponseHandler implements HttpURLConnectionHandler {

    private final HttpClientTracingManager manager;

    public HttpResponseHandler(final HttpClientTracingManager manager) {
        this.manager = Assert.requireNonNull(manager, "manager");
    }

    @Override
    public void handle(final HttpURLConnection connection) {
        Assert.requireNonNull(connection, "connection");
        try {
            final int responseCode = connection.getResponseCode();
            if(responseCode < 300) {
                manager.complete();
            } else {
                manager.completeExceptional(new HttpException("Bad response: " + responseCode));
            }
        } catch (final Exception e) {
            manager.completeExceptional(e);
        }
    }
}
