package dev.rico.internal.metrics.binder;

import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;

public class SystemFileDescriptorMetricsBinder implements MetricsBinder {

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        new FileDescriptorMetrics(tags).bindTo(registry);
    }
}
