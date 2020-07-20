package dev.rico.metrics.spi.log4j2;

import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;

public class Log4j2MetricsBinder implements MetricsBinder {

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        new Log4j2Metrics(tags).bindTo(registry);
    }
}
