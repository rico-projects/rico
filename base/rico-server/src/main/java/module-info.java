import dev.rico.internal.server.bootstrap.BasicConfigurationProvider;
import dev.rico.server.spi.ConfigurationProvider;
import dev.rico.server.spi.components.ManagedBeanFactory;

module dev.rico.server {

    exports dev.rico.server;
    exports dev.rico.server.client;
    exports dev.rico.server.spi;
    exports dev.rico.server.spi.components;
    exports dev.rico.server.timing;

    exports dev.rico.internal.server.beans to dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.remoting.server;
    exports dev.rico.internal.server.bootstrap to dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.metrics.server,
            dev.rico.remoting.server,
            dev.rico.remoting.server.distributed,
            dev.rico.remoting.server.javaee,
            dev.rico.remoting.server.spring,
            dev.rico.remoting.server.spring.test,
            dev.rico.security.server;
    exports dev.rico.internal.server.bootstrap.modules to dev.rico.remoting.server;
    exports dev.rico.internal.server.client to dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.server.config to dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.server.servlet to dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.metrics.server,
            dev.rico.remoting.server;
    exports dev.rico.internal.server.context to dev.rico.metrics.server;
    exports dev.rico.internal.server.scanner to dev.rico.remoting.server.spring.test;

    uses ConfigurationProvider;
    uses ManagedBeanFactory;

    provides ConfigurationProvider with BasicConfigurationProvider;

    requires transitive dev.rico.core;
    requires org.slf4j;
    requires org.apiguardian.api;
    requires io.github.classgraph;
    requires java.management;
    requires java.servlet;
    requires java.annotation;
}