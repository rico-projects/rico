package dev.rico.internal.client.tracing.http;

import dev.rico.core.http.HttpException;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import dev.rico.core.http.RequestChain;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.RicoConstants;
import dev.rico.internal.tracing.TracingConstants;
import dev.rico.tracing.Span;
import dev.rico.tracing.SpanType;
import dev.rico.tracing.Tracer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.function.Supplier;

import static dev.rico.internal.tracing.TracingConstants.PARENT_SPAN_ID_HEADER;
import static dev.rico.internal.tracing.TracingConstants.SPAN_ID_HEADER;
import static dev.rico.internal.tracing.TracingConstants.TRACE_ID_HEADER;

public class HttpTracingHandler implements HttpURLConnectionInterceptor {

    private final Supplier<Tracer> tracerSupplier;

    public HttpTracingHandler(final Supplier<Tracer> tracerSupplier) {
        this.tracerSupplier = Assert.requireNonNull(tracerSupplier, "tracerSupplier");
    }

    @Override
    public void handle(final HttpURLConnection request, final RequestChain chain) throws IOException {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(chain, "chain");

        //TODO: get name based on URI
        final String name = "/example/123";

        final Tracer tracer = tracerSupplier.get();
        Assert.requireNonNull(tracer, "tracer");

        final Span span = tracer.startSpan(name, SpanType.CLIENT);
        request.setRequestProperty(TRACE_ID_HEADER, span.getTraceId());
        request.setRequestProperty(SPAN_ID_HEADER, span.getSpanId());
        span.getParentSpanId().ifPresent(v -> request.setRequestProperty(PARENT_SPAN_ID_HEADER, v));
        try {
            final HttpURLConnection response = chain.call();
            final int responseCode = response.getResponseCode();
            if(responseCode < 300) {
                span.complete();
            } else {
                span.completeExceptional(new HttpException("Bad response: " + responseCode));
            }
        } catch (final Exception e) {
            span.completeExceptional(e);
        }
    }
}
