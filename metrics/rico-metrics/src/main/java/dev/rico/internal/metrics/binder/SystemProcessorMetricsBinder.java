package dev.rico.internal.metrics.binder;

import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

public class SystemProcessorMetricsBinder implements MetricsBinder {

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        new ProcessorMetrics(tags).bindTo(registry);
    }
}
