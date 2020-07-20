package dev.rico.metrics.spi.logback;

import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;

public class LogbackMetricsBinder implements MetricsBinder {

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        new LogbackMetrics(tags).bindTo(registry);
    }
}
