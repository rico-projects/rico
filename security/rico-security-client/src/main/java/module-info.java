import dev.rico.client.spi.ServiceProvider;
import dev.rico.core.http.spi.RequestHandlerProvider;
import dev.rico.internal.security.client.KeycloakSecurityProvider;
import dev.rico.internal.security.client.KeycloakSecurityRequestHandlerProvider;

module dev.rico.security.client {

    exports dev.rico.security.client;

    opens dev.rico.internal.security.client to com.google.gson;

    provides ServiceProvider with KeycloakSecurityProvider;

    provides RequestHandlerProvider with KeycloakSecurityRequestHandlerProvider;

    requires transitive dev.rico.security.common;
    requires transitive dev.rico.client;

    requires org.apiguardian.api;
    requires org.slf4j;
    requires com.google.gson;
}