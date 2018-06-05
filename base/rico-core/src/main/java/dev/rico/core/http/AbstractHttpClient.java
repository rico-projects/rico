package dev.rico.core.http;

import com.google.gson.Gson;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.DefaultHttpURLConnectionFactory;
import dev.rico.internal.core.http.HttpClientConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpClient implements HttpClient {

    private final Gson gson;

    private final HttpURLConnectionFactory httpURLConnectionFactory;

    private final List<HttpURLConnectionHandler> requestHandlers = new CopyOnWriteArrayList<>();

    private final List<HttpURLConnectionHandler> responseHandlers = new CopyOnWriteArrayList<>();

    public AbstractHttpClient(final Gson gson) {
        this(gson, new DefaultHttpURLConnectionFactory());
    }

    public AbstractHttpClient(final Gson gson, final HttpURLConnectionFactory httpURLConnectionFactory) {
        this.gson = Assert.requireNonNull(gson, "gson");
        this.httpURLConnectionFactory = Assert.requireNonNull(httpURLConnectionFactory, "httpURLConnectionFactory");
    }

    public void addRequestHandler(final HttpURLConnectionHandler handler) {
        Assert.requireNonNull(handler, "handler");
        requestHandlers.add(handler);
    }

    @Override
    public void addResponseHandler(final HttpURLConnectionHandler handler) {
        Assert.requireNonNull(handler, "handler");
        responseHandlers.add(handler);
    }

    @Override
    public HttpCallRequestBuilder request(final String url, final RequestMethod method) {
        try {
            return request(new URI(url), method);
        } catch (final URISyntaxException e) {
            throw new RuntimeException("HTTP error", e);
        }
    }

    protected Gson getGson() {
        return gson;
    }

    protected HttpURLConnectionFactory getHttpURLConnectionFactory() {
        return httpURLConnectionFactory;
    }

    protected List<HttpURLConnectionHandler> getRequestHandlers() {
        return requestHandlers;
    }

    protected List<HttpURLConnectionHandler> getResponseHandlers() {
        return responseHandlers;
    }
}

