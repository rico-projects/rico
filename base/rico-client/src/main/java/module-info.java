import dev.rico.client.spi.ServiceProvider;
import dev.rico.core.http.spi.RequestHandlerProvider;
import dev.rico.core.http.spi.ResponseHandlerProvider;
import dev.rico.internal.client.concurrent.BackgroundExecutorProvider;
import dev.rico.internal.client.concurrent.SchedulerProvider;
import dev.rico.internal.client.concurrent.UiExecutorProvider;
import dev.rico.internal.client.context.ContextManagerServiceProvider;
import dev.rico.internal.client.http.HttpClientProvider;
import dev.rico.internal.client.http.cookie.CookieHandlerProvider;
import dev.rico.internal.client.http.cookie.CookieRequestHandlerProvider;
import dev.rico.internal.client.http.cookie.CookieResponseHandlerProvider;
import dev.rico.internal.client.json.JsonProvider;
import dev.rico.internal.client.session.ClientSessionRequestHandlerProvider;
import dev.rico.internal.client.session.ClientSessionResponseHandlerProvider;
import dev.rico.internal.client.session.ClientSessionStoreProvider;

module dev.rico.client {

    exports dev.rico.client;
    exports dev.rico.client.concurrent;
    exports dev.rico.client.session;
    exports dev.rico.client.spi;

    exports dev.rico.internal.client to dev.rico.remoting.client,
            dev.rico.security.client;
    exports dev.rico.internal.client.session to dev.rico.remoting.client,
            dev.rico.remoting.server.spring.test;

    uses ServiceProvider;
    uses RequestHandlerProvider;
    uses ResponseHandlerProvider;

    provides ServiceProvider with HttpClientProvider,
            JsonProvider,
            ClientSessionStoreProvider,
            CookieHandlerProvider,
            ContextManagerServiceProvider,
            SchedulerProvider,
            BackgroundExecutorProvider,
            UiExecutorProvider;

    provides RequestHandlerProvider with CookieRequestHandlerProvider,
            ClientSessionRequestHandlerProvider;

    provides ResponseHandlerProvider with CookieResponseHandlerProvider,
            ClientSessionResponseHandlerProvider;

    requires transitive dev.rico.core;
    requires org.slf4j;
    requires static org.apiguardian.api;
    requires com.google.gson;
}
