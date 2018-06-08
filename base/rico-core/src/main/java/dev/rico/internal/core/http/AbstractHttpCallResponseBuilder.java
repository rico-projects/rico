package dev.rico.internal.core.http;

import com.google.gson.Gson;
import dev.rico.core.http.*;
import dev.rico.internal.core.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.rico.internal.core.http.HttpHeaderConstants.*;
import static dev.rico.internal.core.http.HttpHeaderConstants.ACCEPT_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;

public abstract class AbstractHttpCallResponseBuilder implements HttpCallResponseBuilder {

    private final HttpClientConnection connection;

    private final Gson gson;

    private final AtomicBoolean handled = new AtomicBoolean(false);

    private final List<HttpURLConnectionInterceptor> requestChainHandlers;

    private final ByteArrayProvider dataProvider;

    public AbstractHttpCallResponseBuilder(final HttpClientConnection connection, final ByteArrayProvider dataProvider, final Gson gson, final List<HttpURLConnectionInterceptor> requestChainHandlers) {
        this.connection = Assert.requireNonNull(connection, "connection");
        this.dataProvider = Assert.requireNonNull(dataProvider, "dataProvider");
        this.gson = Assert.requireNonNull(gson, "gson");

        Assert.requireNonNull(requestChainHandlers, "requestChainHandlers");
        this.requestChainHandlers = Collections.unmodifiableList(requestChainHandlers);
    }

    @Override
    public HttpExecutor<InputStream> streamBytes() {
        return createExecutor();
    }

    @Override
    public HttpExecutor<ByteArrayProvider> readBytes() {
        final ResponseContentConverter<ByteArrayProvider> converter = b -> new SimpleByteArrayProvider(b);
        return createExecutor(converter);
    }

    @Override
    public HttpExecutor<String> readString() {
        connection.addRequestHeader(new HttpHeaderImpl(ACCEPT_CHARSET_HEADER, CHARSET));

        final ResponseContentConverter<String> converter = b -> new String(b, CHARSET);
        return createExecutor(converter);
    }

    @Override
    public <R> HttpExecutor<R> readObject(final Class<R> responseType) {
        Assert.requireNonNull(responseType, "responseType");

        connection.addRequestHeader(new HttpHeaderImpl(ACCEPT_CHARSET_HEADER, CHARSET));
        connection.addRequestHeader(new HttpHeaderImpl(ACCEPT_HEADER, JSON_MIME_TYPE));

        final ResponseContentConverter<R> converter = b -> gson.fromJson(new String(b, CHARSET), responseType);
        return createExecutor(converter);
    }

    @Override
    public HttpExecutor<Void> withoutResult() {
        final ResponseContentConverter<Void> converter = b -> null;
        return createExecutor(converter);
    }

    @Override
    public HttpExecutor<ByteArrayProvider> readBytes(final String contentType) {
        Assert.requireNonNull(contentType, "contentType");

        connection.addRequestHeader(new HttpHeaderImpl(ACCEPT_HEADER, contentType));
        return readBytes();
    }

    @Override
    public HttpExecutor<String> readString(final String contentType) {
        Assert.requireNonNull(contentType, "contentType");

        connection.addRequestHeader(new HttpHeaderImpl(ACCEPT_HEADER, contentType));
        return readString();
    }

    protected abstract <R> HttpExecutor<R> createExecutor(final ResponseContentConverter<R> converter);

    protected abstract HttpExecutor<InputStream> createExecutor();

    protected <R> HttpResponse<R> handleRequest(final ResponseContentConverter<R> converter) throws HttpException {
        final HttpResponse<InputStream> response = handleRequest();
        try {
            final InputStream inputStream = response.getContent();
            final R content = converter.convert(ConnectionUtils.readContent(inputStream));
            return new HttpResponseImpl<>(response.getHeaders(), response.getStatusCode(), content, response.getContentSize());
        } catch (IOException e) {
            throw new ConnectionException("No response from server", e);
        } catch (Exception e) {
            throw new HttpException("Can not handle response", e);
        }
    }

    protected HttpResponse<InputStream> handleRequest() throws HttpException {
        if (handled.get()) {
            throw new RuntimeException("Http call already handled");
        }
        handled.set(true);

        try {
            final RequestChain innerChain = () -> {
                final byte[] rawBytes = dataProvider.get();
                connection.writeRequestContent(rawBytes);
                connection.readResponseCode();
                return connection.getConnection();
            };
            RequestChain currentChain = innerChain;
            for (final HttpURLConnectionInterceptor interceptor : requestChainHandlers) {
                currentChain = wrapInChain(interceptor, currentChain);
            }
            currentChain.call();

            final List<HttpHeader> headers = connection.getResponseHeaders();
            final int responseCode = connection.readResponseCode();
            return new HttpResponseImpl<>(headers, responseCode, connection.getContentStream(), connection.getContentSize());
        } catch (IOException e) {
            throw new ConnectionException("No response from server", e);
        } catch (Exception e) {
            throw new HttpException("Can not handle response", e);
        }
    }

    private RequestChain wrapInChain(final HttpURLConnectionInterceptor interceptor, final RequestChain chain) {
        return () -> {
            interceptor.handle(connection.getConnection(), chain);
            return connection.getConnection();
        };
    }
}
