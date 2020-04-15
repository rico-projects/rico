import dev.rico.client.spi.ServiceProvider;
import dev.rico.internal.remoting.client.ClientContextFactoryProvider;

module dev.rico.remoting.client {

    exports dev.rico.remoting.client;

    exports dev.rico.internal.remoting.client to dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.client.legacy to dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.client.legacy.communication to dev.rico.remoting.server.spring.test;

    provides ServiceProvider with ClientContextFactoryProvider;

    requires transitive dev.rico.remoting.common;
    requires transitive dev.rico.client;

    requires static org.apiguardian.api;
    requires org.slf4j;
    requires java.desktop;
}