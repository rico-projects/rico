package dev.rico.internal.server.tracing;

import dev.rico.internal.core.Assert;
import dev.rico.internal.tracing.TracerImpl;
import dev.rico.tracing.Span;
import dev.rico.tracing.SpanType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static dev.rico.internal.tracing.TracingConstants.PARENT_SPAN_ID_HEADER;
import static dev.rico.internal.tracing.TracingConstants.SPAN_ID_HEADER;
import static dev.rico.internal.tracing.TracingConstants.TRACE_ID_HEADER;

public class TraceServletFilter implements Filter {

    private final TracerImpl tracer;

    public TraceServletFilter(TracerImpl tracer) {
        this.tracer = tracer;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String tracerIdString = httpRequest.getHeader(TRACE_ID_HEADER);

        if(tracerIdString != null && !tracerIdString.trim().isEmpty()) {
            handleWithGivenSpan(httpRequest, httpResponse, chain, tracerIdString);
        } else {
            handleWithoutGivenSpan(httpRequest, httpResponse, chain);
        }
    }

    private void handleWithoutGivenSpan(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(response, "response");
        Assert.requireNonNull(chain, "chain");

        final String endpoint = getEndpoint(request);
        final Span span = tracer.startSpan(endpoint, SpanType.SERVICE);
        try {
            chain.doFilter(request, response);
            span.complete();
        } catch (final Throwable exception) {
            span.completeExceptional(exception);
        }
    }

    private void handleWithGivenSpan(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final String tracerIdString) throws IOException, ServletException {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(response, "response");
        Assert.requireNonNull(chain, "chain");
        Assert.requireNonBlank(tracerIdString, "tracerIdString");

        final String spanIdHeader = request.getHeader(SPAN_ID_HEADER);
        final String parentSpanIdHeader = request.getHeader(PARENT_SPAN_ID_HEADER);

        final long tracerId = Long.parseLong(tracerIdString);
        final Long spanId = Optional.ofNullable(spanIdHeader).map(v -> Long.parseLong(v)).orElse(null);
        final Long parentSpanId = Optional.ofNullable(parentSpanIdHeader).map(v -> Long.parseLong(v)).orElse(null);
        final Span span = tracer.setIncomingContext(tracerId, spanId, parentSpanId);
        try {
            final String endpoint = getEndpoint(request);
            final Span childSpan = tracer.startChildSpan(span, endpoint, SpanType.SERVICE);
            try {
                chain.doFilter(request, response);
                childSpan.complete();
            } catch (final Throwable exception) {
                childSpan.completeExceptional(exception);
            }
        } catch (final Throwable exception) {
            span.completeExceptional(exception);
        }
    }

    private String getEndpoint(final HttpServletRequest request) {
        Assert.requireNonNull(request, "request");
        return "/example/123";
    }

    @Override
    public void destroy() {}
}
