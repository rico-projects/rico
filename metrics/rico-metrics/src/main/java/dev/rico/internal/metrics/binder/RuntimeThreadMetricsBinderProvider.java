package dev.rico.internal.metrics.binder;

import dev.rico.core.Configuration;
import dev.rico.metrics.spi.MetricsBinder;
import dev.rico.metrics.spi.MetricsBinderProvider;

public class RuntimeThreadMetricsBinderProvider implements MetricsBinderProvider {

    @Override
    public MetricsBinder createBinder(final Configuration configuration) {
        return new RuntimeThreadMetricsBinder();
    }
}
