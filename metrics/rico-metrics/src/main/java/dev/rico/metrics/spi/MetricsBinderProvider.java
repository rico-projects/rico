package dev.rico.metrics.spi;

import dev.rico.core.Configuration;

public interface MetricsBinderProvider {

    MetricsBinder createBinder(final Configuration configuration);

    default boolean isActive(final Configuration configuration) {
        return true;
    }
}
