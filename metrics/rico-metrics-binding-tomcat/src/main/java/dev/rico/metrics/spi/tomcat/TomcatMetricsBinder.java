package dev.rico.metrics.spi.tomcat;

import dev.rico.internal.core.Assert;
import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.tomcat.TomcatMetrics;
import org.apache.catalina.Manager;

public class TomcatMetricsBinder implements MetricsBinder {

    private final Manager manager;

    public TomcatMetricsBinder(final Manager manager) {
        this.manager = Assert.requireNonNull(manager, "manager");
    }

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        TomcatMetrics.monitor(registry, manager, tags);
    }
}
