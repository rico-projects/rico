package dev.rico.internal.client.tracing.http;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.DefaultHttpURLConnectionFactory;
import dev.rico.tracing.Span;
import dev.rico.tracing.SpanType;
import dev.rico.tracing.Tracer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import static dev.rico.internal.tracing.TracingConstants.PARENT_SPAN_ID_HEADER;

public class TracingHttpURLConnectionFactory extends DefaultHttpURLConnectionFactory {

    private final Tracer tracer;

    public TracingHttpURLConnectionFactory(final Tracer tracer) {
        this.tracer = Assert.requireNonNull(tracer, "tracer");
    }

    @Override
    public HttpURLConnection create(URI url) throws IOException {
        final HttpURLConnection connection = super.create(url);

        final Span span = tracer.getCurrentSpan();
        if(span != null) {
            connection.setRequestProperty(PARENT_SPAN_ID_HEADER, span.getSpanId());
        }

        return connection;
    }

}
