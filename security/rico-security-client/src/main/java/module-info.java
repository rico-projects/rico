module dev.rico.security.client {

    exports dev.rico.security.client;

    requires transitive dev.rico.security.common;
    requires transitive dev.rico.client;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires com.google.gson;
}