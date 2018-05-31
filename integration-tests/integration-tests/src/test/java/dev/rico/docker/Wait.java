package dev.rico.docker;

import dev.rico.client.Client;
import dev.rico.core.http.HttpClient;
import dev.rico.integrationtests.AbstractIntegrationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface Wait {

    Logger LOG = LoggerFactory.getLogger(Wait.class);

    long sleeptImeInMillis = 1_000;

    static Wait forHttp(final URI uri, final int httpStatus) {
        return (executor, time, timeUnit) -> {
            final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
            final Runnable task = () -> {
                try {
                    boolean sucessfull = false;
                    final HttpClient httpClient = Client.getService(HttpClient.class);
                    while (!sucessfull) {
                        try {
                            final int statusCode = httpClient.get(uri).withoutContent().withoutResult().execute().get().getStatusCode();
                            if (statusCode == httpStatus) {
                                sucessfull = true;
                                completableFuture.complete(null);
                            }
                        } catch (Exception e) {
                            LOG.trace("Endpoint not reachable. Will try again in " + sleeptImeInMillis + " ms");
                            Thread.sleep(sleeptImeInMillis);
                        }
                    }
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            };
            try {
                executor.execute(task);
                completableFuture.get(time, timeUnit);
            } catch (InterruptedException e) {
                throw new RuntimeException("Wait process was interrupted!", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Error in executing wait process!", e);
            }
        };
    }

    void waitFor(Executor executor, long time, TimeUnit timeUnit) throws TimeoutException;
}
