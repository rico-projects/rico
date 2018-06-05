package dev.rico.internal.core.tracing;

import dev.rico.core.http.HttpClient;
import dev.rico.core.http.HttpResponse;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.HttpHeaderConstants;
import zipkin2.Call;
import zipkin2.Callback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class HttpCall extends Call<Void> {

    private final HttpClient client;

    private final URI endpoint;

    private final List<byte[]> encodedSpans;

    private final List<Callback<Void>> callbacks;

    private CompletableFuture<HttpResponse<Void>> call;

    private boolean canceled = false;

    public HttpCall(final HttpClient client, final URI endpoint, final List<byte[]> encodedSpans, final List<Callback<Void>> callbacks) {
        this.client = Assert.requireNonNull(client, "client");
        this.endpoint = Assert.requireNonNull(endpoint, "endpoint");
        this.encodedSpans = Assert.requireNonNull(encodedSpans, "encodedSpans");
        Assert.requireNonNull(callbacks, "callbacks");
        this.callbacks = new CopyOnWriteArrayList<>(callbacks);
    }

    public HttpCall(final HttpClient client, final URI endpoint, final List<byte[]> encodedSpans) {
        this(client, endpoint, encodedSpans, Collections.emptyList());
    }

    @Override
    public synchronized Void execute() throws IOException {
        // [a, b, c]
        final int lenght = encodedSpans.size() + Math.max(0, encodedSpans.size() - 1) + 2;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(lenght);
        outputStream.write('[');

        for (int i = 0, length = encodedSpans.size(); i < length; ) {
            byte[] next = encodedSpans.get(i++);
            outputStream.write(next);
            if (i < length) outputStream.write(',');
        }
        outputStream.write(']');

        final byte[] data = outputStream.toByteArray();
        call = client.post(endpoint)
                .withContent(data, HttpHeaderConstants.JSON_MIME_TYPE)
                .withoutResult()
                .onDone(r -> callbacks.forEach(c -> c.onSuccess(null)))
                .onError(e -> callbacks.forEach(c -> c.onError(e)))
                .execute();
        return null;
    }

    @Override
    public void enqueue(final Callback<Void> callback) {
        Assert.requireNonNull(callback, "callback");
        callbacks.add(callback);
    }

    @Override
    public synchronized void cancel() {
        if (call != null) {
            canceled = call.cancel(true);
        }
    }

    @Override
    public synchronized boolean isCanceled() {
        return canceled;
    }

    @Override
    public Call<Void> clone() {
        return new HttpCall(client, endpoint, encodedSpans, callbacks);
    }
}
