import dev.rico.internal.metrics.server.module.MetricsConfigurationProvider;
import dev.rico.server.spi.ConfigurationProvider;

module dev.rico.metrics.server {

    requires transitive dev.rico.metrics;
    requires transitive dev.rico.server;

    provides ConfigurationProvider with MetricsConfigurationProvider;

    requires org.slf4j;
    requires static java.servlet;
    requires static org.apiguardian.api;
    requires micrometer.core;
    requires micrometer.registry.prometheus;
}
