import dev.rico.internal.security.server.SecurityDefaultValueProvider;
import dev.rico.server.spi.ConfigurationProvider;

module dev.rico.security.server {

    exports dev.rico.security.server;
    exports dev.rico.internal.security.server to dev.rico.security.server.javaee,
            dev.rico.security.server.spring;

    provides ConfigurationProvider with SecurityDefaultValueProvider;

    requires transitive dev.rico.security.common;
    requires transitive dev.rico.server;

    requires keycloak.core;
    requires keycloak.adapter.core;
    requires keycloak.adapter.spi;
    requires keycloak.servlet.filter.adapter;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires java.servlet;
}