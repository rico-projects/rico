module dev.rico.remoting.server {

    exports dev.rico.remoting.server;
    exports dev.rico.remoting.server.binding;
    exports dev.rico.remoting.server.event;
    exports dev.rico.remoting.server.event.spi;
    exports dev.rico.remoting.server.error;

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


    requires transitive dev.rico.remoting.common;
    requires transitive dev.rico.server;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires java.servlet;
}