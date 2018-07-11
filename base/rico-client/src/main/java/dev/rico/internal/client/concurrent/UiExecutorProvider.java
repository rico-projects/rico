package dev.rico.internal.client.concurrent;

import dev.rico.client.ClientConfiguration;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.internal.client.AbstractServiceProvider;
import dev.rico.internal.client.ClientImpl;

public class UiExecutorProvider extends AbstractServiceProvider<UiExecutor> {

    public UiExecutorProvider() {
        super(UiExecutor.class);
    }

    @Override
    protected UiExecutor createService(final ClientConfiguration configuration) {
        return ClientImpl.getUiExecutor();
    }
}
