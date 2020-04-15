import dev.rico.internal.server.spring.SpringManagedBeanFactory;
import dev.rico.server.spi.components.ManagedBeanFactory;

module dev.rico.server.spring {

    exports dev.rico.server.spring;

    exports dev.rico.internal.server.spring to dev.rico.remoting.server.spring.test,
            spring.beans,
            spring.context;

    provides ManagedBeanFactory with SpringManagedBeanFactory;

    requires transitive dev.rico.server;
    requires org.slf4j;
    requires org.apiguardian.api;
    requires java.servlet;

    requires spring.core;
    requires spring.context;
    requires spring.beans;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;

    opens dev.rico.internal.server.spring to spring.core;
}