package dev.rico.internal.server.trace;

import brave.Tracing;
import brave.servlet.TracingFilter;
import dev.rico.internal.core.Assert;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.EnumSet;

public class TracingServletContextListener implements ServletContextListener {

    private final static String FILTER_NAME = "tracingFilter";

    private final static String ALL_ENDPOINTS = "/*";

    private final Tracing tracing;

    public TracingServletContextListener(final Tracing tracing) {
        this.tracing = Assert.requireNonNull(tracing, "tracing");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent
                .getServletContext()
                .addFilter(FILTER_NAME, TracingFilter.create(tracing))
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, ALL_ENDPOINTS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}