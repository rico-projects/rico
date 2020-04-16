import dev.rico.internal.remoting.server.distributed.DistributedEventBusConfigProvider;
import dev.rico.internal.remoting.server.distributed.DistributedEventBusProvider;
import dev.rico.remoting.server.event.spi.EventBusProvider;
import dev.rico.server.spi.ConfigurationProvider;

module dev.rico.remoting.server.distributed {

    exports dev.rico.remoting.server.distributed;

    provides EventBusProvider with DistributedEventBusProvider;

    provides ConfigurationProvider with DistributedEventBusConfigProvider;

    requires transitive dev.rico.remoting.server;

    requires static org.apiguardian.api;
    requires org.slf4j;
    requires com.google.gson;
    requires com.hazelcast.core;
    requires com.hazelcast.client;
}
