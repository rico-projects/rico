package dev.rico.metrics.spi;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public interface MetricsBinder {

    void init(final MeterRegistry registry, Iterable<Tag> tags);

    default void unregister(final MeterRegistry registry) {
    }
}
