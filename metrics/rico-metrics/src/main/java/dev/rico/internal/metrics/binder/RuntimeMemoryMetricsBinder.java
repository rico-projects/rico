package dev.rico.internal.metrics.binder;

import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

public class RuntimeMemoryMetricsBinder implements MetricsBinder {

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        new JvmMemoryMetrics(tags).bindTo(registry);
    }
}
