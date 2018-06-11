package dev.rico.internal.client.concurrent;

import dev.rico.client.ClientConfiguration;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.internal.client.AbstractServiceProvider;
import dev.rico.internal.core.SimpleThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutorProvider extends AbstractServiceProvider<BackgroundExecutor> {

    public BackgroundExecutorProvider() {
        super(BackgroundExecutor.class);
    }

    @Override
    protected BackgroundExecutor createService(final ClientConfiguration configuration) {
        final ExecutorService executorService = Executors.newCachedThreadPool(new SimpleThreadFactory());
        return new BackgroundExecutorImpl(executorService);
    }
}
