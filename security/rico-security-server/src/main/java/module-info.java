module dev.rico.security.server {

    exports dev.rico.security.server;
    exports dev.rico.internal.security.server to dev.rico.security.server.javaee,
            dev.rico.security.server.spring;

    requires transitive dev.rico.security.common;
    requires transitive dev.rico.server;

    requires keycloak.core;
    requires keycloak.adapter.core;
    requires keycloak.adapter.spi;
    requires keycloak.servlet.filter.adapter;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires javax.servlet.api;
}