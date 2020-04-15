module dev.rico.security.server.spring {

    exports dev.rico.security.server.spring;
    opens dev.rico.internal.security.server.spring to spring.core,
            spring.beans,
            spring.context;

    requires transitive dev.rico.security.server;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires spring.web;
    requires spring.context;
    requires org.apache.httpcomponents.httpclient;
}