module dev.rico.metrics.server.spring {

    exports dev.rico.metrics.server.spring;

    opens dev.rico.internal.metrics.server.spring to spring.core;
    exports dev.rico.internal.metrics.server.spring to spring.beans,
            spring.context;

    requires transitive dev.rico.metrics.server;
    requires static org.apiguardian.api;
    requires spring.context;
    requires spring.web;
}
