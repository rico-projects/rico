import dev.rico.internal.security.server.SecurityDefaultValueProvider;
import dev.rico.server.spi.ConfigurationProvider;

module dev.rico.security.server {

    exports dev.rico.security.server;
    exports dev.rico.internal.security.server to dev.rico.server,
            dev.rico.security.server.javaee,
            dev.rico.security.server.spring;

    provides ConfigurationProvider with SecurityDefaultValueProvider;

    requires transitive dev.rico.security.common;
    requires transitive dev.rico.server;

    requires keycloak.core;
    requires keycloak.adapter.core;
    requires keycloak.adapter.spi;
    requires keycloak.servlet.adapter.spi;

    requires static org.apiguardian.api;
    requires org.slf4j;
    requires static java.servlet;
    requires java.logging;
}