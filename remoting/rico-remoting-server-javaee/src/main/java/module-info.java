module dev.rico.remoting.server.javaee {

    requires transitive dev.rico.remoting.server;
    requires transitive dev.rico.server.javaee;

    requires static org.apiguardian.api;
    requires jakarta.enterprise.cdi.api;

}