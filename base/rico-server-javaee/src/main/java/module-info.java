import dev.rico.internal.server.javaee.CdiManagedBeanFactory;
import dev.rico.internal.server.javaee.ClientScopeExtension;
import dev.rico.internal.server.javaee.JavaeeBootstrap;
import dev.rico.server.spi.components.ManagedBeanFactory;

import javax.enterprise.inject.spi.Extension;
import javax.servlet.ServletContainerInitializer;

module dev.rico.server.javaee {

    exports dev.rico.server.javaee;
    exports dev.rico.server.javaee.timing;

    provides ManagedBeanFactory with CdiManagedBeanFactory;

    provides Extension with ClientScopeExtension; //TODO: Jakarta has no module-info and therefore no 'uses' keyword? Is this working?

    provides ServletContainerInitializer with JavaeeBootstrap;

    requires transitive dev.rico.server;
    requires static org.apiguardian.api;
    requires jakarta.inject.api;
    requires deltaspike.core.api;
    requires java.servlet;
    requires jakarta.enterprise.cdi.api;
    requires jakarta.interceptor.api;
    requires jakarta.enterprise.concurrent.api;
    requires java.annotation;
}
