module dev.rico.metrics.server {

    requires transitive dev.rico.metrics;
    requires transitive dev.rico.server;

    requires org.slf4j;
    requires java.servlet;
    requires org.apiguardian.api;
    requires micrometer.core;
    requires micrometer.registry.prometheus;
}