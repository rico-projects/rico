package dev.rico.internal.tracing.service;

import dev.rico.core.http.HttpClient;
import dev.rico.internal.core.Assert;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.codec.Encoding;
import zipkin2.reporter.Sender;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HttpSender extends Sender {

    private final int messageMaxBytes = 500;

    private final HttpClient client;

    private final URI endpoint;

    private Callback<Void> httpResultCallback;

    public HttpSender(final URI endpoint, final HttpClient httpClient) {
        this.endpoint = Assert.requireNonNull(endpoint, "endpoint");
        this.client = httpClient;
    }

    @Override
    public Encoding encoding() {
        return Encoding.JSON;
    }

    @Override
    public int messageMaxBytes() {
        return messageMaxBytes;
    }

    @Override
    public int messageSizeInBytes(final List<byte[]> encodedSpans) {
        return encoding().listSizeInBytes(encodedSpans);
    }

    @Override
    public Call<Void> sendSpans(final List<byte[]> encodedSpans) {
        final List<Callback<Void>> callbacks = Optional.ofNullable(httpResultCallback)
                .map(c -> Collections.singletonList(c))
                .orElse(Collections.emptyList());

        return new HttpCall(client, endpoint, encodedSpans, callbacks);
    }

    public void setHttpResultCallback(Callback<Void> httpResultCallback) {
        this.httpResultCallback = httpResultCallback;
    }
}
