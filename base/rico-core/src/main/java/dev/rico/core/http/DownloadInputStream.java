package dev.rico.core.http;

import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A {@link InputStream} that adds functionallity to handle downloads.
 */
@API(since = "0.x", status = EXPERIMENTAL)
public abstract class DownloadInputStream extends InputStream {

    /**
     * Returns a {@link CompletableFuture} to access the hash once the download is done
     * @return a {@link CompletableFuture} to access the hash once the download is done
     */
    public abstract CompletableFuture<String> getHash();

    /**
     * Adds a listener that is triggered once the download starts
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadStartListener(final Consumer<Long> listener);

    /**
     * Adds a listener that is triggered automatically several times while the download is running.
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadPercentageListener(final Consumer<Double> listener);

    /**
     * Adds a listener that is triggered once the download is done
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadDoneListener(final Consumer<Long> listener);
}
