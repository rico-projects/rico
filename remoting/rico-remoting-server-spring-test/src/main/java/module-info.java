module dev.rico.remoting.server.spring.test {

    exports dev.rico.server.remoting.test;

    requires transitive dev.rico.remoting.server.spring;
    requires transitive dev.rico.remoting.client;

    requires static org.apiguardian.api;
    requires spring.beans;
    requires spring.context;
    requires spring.web;
    requires spring.test;
    requires spring.boot.test;
    requires static java.servlet;
    requires testng;
    requires org.junit.jupiter;

}
