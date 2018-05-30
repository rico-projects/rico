package dev.rico.docker;

import dev.rico.client.Client;
import dev.rico.core.http.HttpClient;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface Wait {

    public static Wait forHttp(final Executor executor, final URI uri, final int httpStatus) {
        return (time, timeUnit) -> {
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
                            System.out.println("No server");
                            Thread.sleep(1_000);
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

    void waitFor(long time, TimeUnit timeUnit) throws TimeoutException;
}
