module dev.rico.client {

    exports dev.rico.client;
    exports dev.rico.client.concurrent;
    exports dev.rico.client.session;
    exports dev.rico.client.spi;

    exports dev.rico.internal.client to dev.rico.remoting.client,
            dev.rico.security.client;
    exports dev.rico.internal.client.session to dev.rico.remoting.client,
            dev.rico.remoting.server.spring.test;

    requires transitive dev.rico.core;
    requires org.slf4j;
    requires org.apiguardian.api;
    requires com.google.gson;
}