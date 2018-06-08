package dev.rico.internal.tracing.client;

import dev.rico.core.http.HttpException;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import dev.rico.core.http.RequestChain;
import dev.rico.internal.core.Assert;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpTracingHandler implements HttpURLConnectionInterceptor {

    private final HttpClientTracingManager manager;

    public HttpTracingHandler(final HttpClientTracingManager manager) {
        this.manager = Assert.requireNonNull(manager, "manager");
    }

    @Override
    public void handle(final HttpURLConnection request, final RequestChain chain) throws IOException {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(chain, "chain");
        final String name = "/example/123";
        manager.start(name);
        try {
            final HttpURLConnection response = chain.call();
            final int responseCode = response.getResponseCode();
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
