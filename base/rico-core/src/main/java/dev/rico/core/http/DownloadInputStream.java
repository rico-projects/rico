package dev.rico.core.http;

import dev.rico.core.functional.Subscription;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class DownloadInputStream extends InputStream {

    /**
     * Returns the hash value of the
     * @return
     */
    public abstract CompletableFuture<String> getHash();

    public abstract Subscription addDownloadStartListener(final Consumer<Long> listener);

    public abstract Subscription addDownloadPercentageListener(final Consumer<Double> listener);

    public abstract Subscription addDownloadDoneListener(final Consumer<Long> listener);
}
