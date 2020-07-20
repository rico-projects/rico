import dev.rico.metrics.spi.MetricsBinderProvider;
import dev.rico.metrics.spi.logback.LogbackMetricsBinderProvider;

module dev.rico.metrics.binding.logback {
    requires dev.rico.metrics;
    requires static org.apiguardian.api;
    requires micrometer.core;

    provides MetricsBinderProvider with LogbackMetricsBinderProvider;

}
