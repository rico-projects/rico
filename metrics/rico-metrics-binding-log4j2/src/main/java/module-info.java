import dev.rico.metrics.spi.MetricsBinderProvider;
import dev.rico.metrics.spi.log4j2.Log4j2MetricsBinderProvider;

module dev.rico.metrics.binding.log4j2 {
    requires dev.rico.metrics;
    requires static org.apiguardian.api;
    requires micrometer.core;

    provides MetricsBinderProvider with Log4j2MetricsBinderProvider;
}
