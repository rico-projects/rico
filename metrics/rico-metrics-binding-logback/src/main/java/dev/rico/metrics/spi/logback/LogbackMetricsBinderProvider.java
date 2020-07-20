package dev.rico.metrics.spi.logback;

import dev.rico.core.Configuration;
import dev.rico.metrics.spi.MetricsBinder;
import dev.rico.metrics.spi.MetricsBinderProvider;

public class LogbackMetricsBinderProvider implements MetricsBinderProvider {

    @Override
    public MetricsBinder createBinder(final Configuration configuration) {
        return new LogbackMetricsBinder();
    }
}
