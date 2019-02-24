package dev.rico.internal.client.concurrent;

import dev.rico.client.ClientConfiguration;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.internal.client.AbstractServiceProvider;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.SimpleThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutorProvider extends AbstractServiceProvider<BackgroundExecutor> {

    public BackgroundExecutorProvider() {
        super(BackgroundExecutor.class);
    }

    @Override
    protected BackgroundExecutor createService(final ClientConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        final Thread.UncaughtExceptionHandler exceptionHandler = configuration.getUncaughtExceptionHandler();
        final ExecutorService executorService = Executors.newCachedThreadPool(new SimpleThreadFactory(exceptionHandler));
        return new BackgroundExecutorImpl(executorService);
    }
}
