module dev.rico.remoting.server.spring {

    exports dev.rico.remoting.server.spring;

    requires transitive dev.rico.remoting.server;
    requires transitive dev.rico.server.spring;

    requires static org.apiguardian.api;
    requires spring.context;

    opens dev.rico.internal.remoting.server.spring to spring.core;

    exports dev.rico.internal.remoting.server.spring to spring.beans,
            spring.context;
}