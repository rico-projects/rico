module dev.rico.core {

    exports dev.rico.core;
    exports dev.rico.core.concurrent;
    exports dev.rico.core.context;
    exports dev.rico.core.functional;
    exports dev.rico.core.http;
    exports dev.rico.core.http.spi;
    exports dev.rico.core.lang;

    exports dev.rico.internal.core to dev.rico.client,
            dev.rico.client.javafx,
            dev.rico.client.swing,
            dev.rico.server,
            dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.metrics,
            dev.rico.metrics.server,
            dev.rico.remoting.common,
            dev.rico.remoting.client,
            dev.rico.remoting.client.javafx,
            dev.rico.remoting.validation,
            dev.rico.remoting.server,
            dev.rico.remoting.server.distributed,
            dev.rico.remoting.server.javaee,
            dev.rico.remoting.server.spring,
            dev.rico.remoting.server.spring.test,
            dev.rico.security.client,
            dev.rico.security.server,
            dev.rico.security.server.spring;
    exports dev.rico.internal.core.ansi to dev.rico.client,
            dev.rico.server;
    exports dev.rico.internal.core.context to dev.rico.client,
            dev.rico.server,
            dev.rico.server.javaee,
            dev.rico.server.spring,
            dev.rico.metrics,
            dev.rico.metrics.server,
            dev.rico.remoting.server,
            dev.rico.security.client,
            dev.rico.security.server,
            dev.rico.logback.appender,
            dev.rico.log4j.appender;
    exports dev.rico.internal.core.http to dev.rico.client,
            dev.rico.server,
            dev.rico.remoting.client,
            dev.rico.security.client,
            dev.rico.security.server;
    exports dev.rico.internal.core.lang to dev.rico.remoting.server;

    requires org.slf4j;
    requires static org.apiguardian.api;
}
