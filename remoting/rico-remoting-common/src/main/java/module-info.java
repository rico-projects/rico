module dev.rico.remoting.common {

    exports dev.rico.remoting;
    exports dev.rico.remoting.converter;

    exports dev.rico.internal.remoting to dev.rico.remoting.client,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.codec to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.commands to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.collections to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.info to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.legacy to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.legacy.core to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.legacy.communication to dev.rico.remoting.client,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.legacy.commands to dev.rico.remoting.client,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring.test;

    requires transitive dev.rico.core;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires java.desktop;
}