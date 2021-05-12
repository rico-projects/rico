import dev.rico.internal.metrics.server.module.MetricsConfigurationProvider;
import dev.rico.server.spi.ConfigurationProvider;

module dev.rico.metrics.server {

    requires transitive dev.rico.metrics;
    requires transitive dev.rico.server;
    requires transitive java.management;


    provides ConfigurationProvider with MetricsConfigurationProvider;

    exports dev.rico.internal.metrics.server.module to dev.rico.server;
    exports dev.rico.internal.metrics.server.servlet to spring.beans;

    requires static java.servlet;
    requires static org.apiguardian.api;
    requires micrometer.core;
    requires micrometer.registry.prometheus;
}
