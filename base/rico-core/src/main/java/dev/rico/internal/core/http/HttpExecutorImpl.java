package dev.rico.internal.core.http;

import dev.rico.core.http.BadResponseException;
import dev.rico.core.http.HttpException;
import dev.rico.core.http.HttpExecutor;
import dev.rico.core.http.HttpResponse;
import dev.rico.internal.core.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class HttpExecutorImpl<R> implements HttpExecutor<R> {

    private final Executor backgroundExecutor;

    private final HttpProvider<R> provider;

    private final Executor callbackExecutor;

    private Consumer<HttpResponse<R>> onDone;

    private Consumer<HttpException> errorHandler;

    public HttpExecutorImpl(final Executor backgroundExecutor, final Executor callbackExecutor, final HttpProvider<R> provider) {
        this.backgroundExecutor = Assert.requireNonNull(backgroundExecutor, "backgroundExecutor");
        this.callbackExecutor = Assert.requireNonNull(callbackExecutor, "callbackExecutor");
        this.provider = Assert.requireNonNull(provider, "provider");
    }

    @Override
    public HttpExecutorImpl<R> onDone(final Consumer<HttpResponse<R>> onDone) {
        this.onDone = onDone;
        return this;
    }

    @Override
    public HttpExecutorImpl<R> onError(Consumer<HttpException> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public CompletableFuture<HttpResponse<R>> execute() {
        final CompletableFuture<HttpResponse<R>> completableFuture = new CompletableFuture<>();
        backgroundExecutor.execute(() -> {
            try {
                final HttpResponse<R> result = provider.get();

                final int statusCode = result.getStatusCode();
                if (statusCode >= 300) {
                    final HttpException e = new BadResponseException(result, "Bad Response: " + statusCode);
                    if (errorHandler != null) {
                        callbackExecutor.execute(() -> errorHandler.accept(e));
                    }
                    completableFuture.completeExceptionally(e);
                } else {
                    if (onDone != null) {
                        callbackExecutor.execute(() -> onDone.accept(result));
                    }
                    completableFuture.complete(result);
                }
            } catch (final HttpException e) {
                if (errorHandler != null) {
                    callbackExecutor.execute(() -> errorHandler.accept(e));
                }
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }
}