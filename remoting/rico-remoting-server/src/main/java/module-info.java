import dev.rico.internal.remoting.server.config.RemotingDefaultValueProvider;
import dev.rico.internal.remoting.server.event.DefaultEventBusProvider;
import dev.rico.remoting.server.event.spi.EventBusProvider;
import dev.rico.server.spi.ConfigurationProvider;

module dev.rico.remoting.server {

    exports dev.rico.remoting.server;
    exports dev.rico.remoting.server.binding;
    exports dev.rico.remoting.server.event;
    exports dev.rico.remoting.server.event.spi;
    exports dev.rico.remoting.server.error;

    exports dev.rico.internal.remoting.server to dev.rico.server;
    exports dev.rico.internal.remoting.server.event to dev.rico.remoting.server.distributed,
            dev.rico.remoting.server.javaee,
            dev.rico.remoting.server.spring,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.server.config to dev.rico.remoting.server.distributed,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.server.context to dev.rico.remoting.server.javaee,
            dev.rico.remoting.server.spring,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.server.controller to dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.server.binding to dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.server.legacy to dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.server.legacy.communication to dev.rico.remoting.server.spring.test;

    uses EventBusProvider;

    provides EventBusProvider with DefaultEventBusProvider;

    provides ConfigurationProvider with RemotingDefaultValueProvider;

    requires transitive dev.rico.remoting.common;
    requires transitive dev.rico.server;

    requires static org.apiguardian.api;
    requires org.slf4j;
    requires static java.servlet;
}