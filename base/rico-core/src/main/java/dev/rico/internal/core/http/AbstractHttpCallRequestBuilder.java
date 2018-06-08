package dev.rico.internal.core.http;

import com.google.gson.Gson;
import dev.rico.core.http.ByteArrayProvider;
import dev.rico.core.http.HttpCallRequestBuilder;
import dev.rico.core.http.HttpCallResponseBuilder;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import dev.rico.internal.core.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_TYPE_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.JSON_MIME_TYPE;

public abstract class AbstractHttpCallRequestBuilder implements HttpCallRequestBuilder {

    private final HttpClientConnection connection;

    private final Gson gson;

    private final List<HttpURLConnectionInterceptor> requestChainHandlers;

    private final AtomicBoolean done = new AtomicBoolean(false);

    public AbstractHttpCallRequestBuilder(final HttpClientConnection connection, final Gson gson, final List<HttpURLConnectionInterceptor> requestChainHandlers) {
        this.connection = Assert.requireNonNull(connection, "connection");
        this.gson = Assert.requireNonNull(gson, "gson");


        Assert.requireNonNull(requestChainHandlers, "requestChainHandlers");
        this.requestChainHandlers = Collections.unmodifiableList(requestChainHandlers);
    }

    @Override
    public HttpCallRequestBuilder withHeader(final String name, final String content) {
        connection.addRequestHeader(new HttpHeaderImpl(name, content));
        return this;
    }

    @Override
    public HttpCallResponseBuilder withContent(final byte[] content, final String contentType) {
        withHeader(CONTENT_TYPE_HEADER, contentType);
        connection.setDoOutput(true);
        return continueWithResponseBuilder(() -> content);
    }

    @Override
    public <I> HttpCallResponseBuilder withContent(final I content) {
        return withContent(gson.toJson(content), JSON_MIME_TYPE);
    }

    @Override
    public HttpCallResponseBuilder withoutContent() {
        return continueWithResponseBuilder(() -> new byte[0]);
    }

    private HttpCallResponseBuilder continueWithResponseBuilder(final ByteArrayProvider dataProvider) {
        Assert.requireNonNull(dataProvider, "dataProvider");
        if (done.get()) {
            throw new RuntimeException("Request already defined!");
        }
        done.set(true);
        return createResponseBuilder(connection, dataProvider, gson, requestChainHandlers);
    }

    protected abstract HttpCallResponseBuilder createResponseBuilder(final HttpClientConnection connection, final ByteArrayProvider dataProvider, final Gson gson, final List<HttpURLConnectionInterceptor> requestChainHandlers);
}

