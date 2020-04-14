module dev.rico.server.javaee {

    exports dev.rico.server.javaee;
    exports dev.rico.server.javaee.timing;

    requires transitive dev.rico.server;
    requires org.apiguardian.api;
    requires jakarta.inject.api;
    requires deltaspike.core.api;
    requires javax.servlet.api;
    requires jakarta.enterprise.cdi.api;
    requires jakarta.interceptor.api;
}