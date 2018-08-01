package dev.rico.internal.server.tracing;

import io.opentracing.SpanContext;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;
import io.opentracing.util.GlobalTracer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddTracingServlet {

    public void add(ServletContext context) {
        GlobalTracer.register(null);

        TracingFilter filter = new TracingFilter(GlobalTracer.get());
        context.addFilter("tracingFilter", filter);
    }


    public SpanContext getForRequest(HttpServletRequest httpservletRequest) {
        SpanContext spanContext = (SpanContext)httpservletRequest.getAttribute(TracingFilter.SERVER_SPAN_CONTEXT);
        return spanContext;
    }

}
