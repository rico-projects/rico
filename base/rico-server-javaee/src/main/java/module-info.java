module dev.rico.server.javaee {

    exports dev.rico.server.javaee;
    exports dev.rico.server.javaee.timing;

    requires transitive dev.rico.server;
    requires org.apiguardian.api;
    requires jakarta.inject.api;
    requires deltaspike.core.api;
    requires java.servlet;
    requires jakarta.enterprise.cdi.api;
    requires jakarta.interceptor.api;
}