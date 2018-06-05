package dev.rico.internal.core.tracing;

import dev.rico.core.http.HttpClient;
import dev.rico.internal.core.Assert;
import zipkin2.Call;
import zipkin2.codec.Encoding;
import zipkin2.reporter.Sender;

import java.net.URI;
import java.util.List;

public class HttpSender extends Sender {

    private final int messageMaxBytes = 5 * 1024 * 1024;

    private final HttpClient client;

    private final URI endpoint;

    public HttpSender(final URI endpoint) {
        this.endpoint = Assert.requireNonNull(endpoint, "endpoint");
        this.client = null;
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
        return new HttpCall(client, endpoint, encodedSpans);
    }
}
