package dev.rico.internal.client.tracing.http;

import dev.rico.core.Configuration;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

import static dev.rico.internal.tracing.TracingConstants.*;

public class HttpTracingHandler implements HttpURLConnectionInterceptor {

    private final Supplier<Tracer> tracerSupplier;

    private final String reportingEndpoint;

    public HttpTracingHandler(final Supplier<Tracer> tracerSupplier, final Configuration configuration) {
        this.tracerSupplier = Assert.requireNonNull(tracerSupplier, "tracerSupplier");
        this.reportingEndpoint = configuration.getProperty(REPORTER_TYPE_HTTP_ENDPOINT_PROPERTY);
    }

    @Override
    public void handle(final HttpURLConnection request, final RequestChain chain) throws IOException {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(chain, "chain");

        if(!isReportingEndpoint(request.getURL())) {
            //TODO: get name based on URI
            final String name = getName(request);

            final Tracer tracer = tracerSupplier.get();
            Assert.requireNonNull(tracer, "tracer");

            final Span span = tracer.startSpan(name, SpanType.CLIENT);
            request.setRequestProperty(TRACE_ID_HEADER, span.getTraceId());
            request.setRequestProperty(TracingConstants.SAMPLED_HEADER, "1");
            request.setRequestProperty(SPAN_ID_HEADER, span.getSpanId());
            span.getParentSpanId().ifPresent(v -> request.setRequestProperty(PARENT_SPAN_ID_HEADER, v));
            try {
                final HttpURLConnection response = chain.call();
                final int responseCode = response.getResponseCode();
                if (responseCode < 300) {
                    span.complete();
                } else {
                    span.completeExceptional(new HttpException("Bad response: " + responseCode));
                }
            } catch (final Exception e) {
                span.completeExceptional(e);
            }
        } else {
            chain.call();
        }
    }

    private String getName(final HttpURLConnection request) {
        final String method = request.getRequestMethod();
        final String url =  request.getURL().toString();
        return method + " " + url;
    }

    private boolean isReportingEndpoint(final URL url) {
        Assert.requireNonNull(url, "url");
        if(reportingEndpoint == null) {
            return false;
        }
        try {
            final URL reportingEndpointUrl = new URL(reportingEndpoint);
            return Objects.equals(reportingEndpointUrl, url);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
