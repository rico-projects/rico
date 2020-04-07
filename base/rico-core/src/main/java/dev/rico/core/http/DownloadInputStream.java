package dev.rico.core.http;

import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A {@link InputStream} that adds functionality to handle downloads.
 */
@API(since = "0.x", status = EXPERIMENTAL)
public abstract class DownloadInputStream extends InputStream {

    /**
     * Sets the chunk size that is used to check for updates of listeners. While the stream is used listeners
     * (see {@link #addDownloadPercentageListener(Consumer)}) will be called several times. The chunk size defines
     * after what byte count the listener will be called again.
     *
     * @param updateChunkSize the new chunk size
     */
    public abstract void setUpdateChunkSize(final long updateChunkSize);

    /**
     * Adds a listener that is triggered once the download starts
     *
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadStartListener(final Consumer<Long> listener);

    /**
     * Adds a listener that is triggered automatically several times while the download is running.
     *
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadPercentageListener(final Consumer<Double> listener);

    /**
     * Adds a listener that is triggered once the download is done
     *
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadDoneListener(final Consumer<Long> listener);

    /**
     * Adds a listener that is triggered if the download ends with an error
     * (like a {@link java.io.IOException} while reading from the stream)
     *
     * @param listener the listener
     * @return the subscription
     */
    public abstract Subscription addDownloadErrorListener(final Consumer<Exception> listener);

    /**
     * Returns the type of the download
     *
     * @return the type of the download
     */
    public abstract DownloadType getDownloadType();

    /**
     * Returns the count of bytes that was already downloaded.
     *
     * @return count of bytes that was already downloaded
     */
    public abstract long getDownloaded();

    /**
     * Returns the data size of the complete download (if that is known), otherwise -1. If the size is not known the
     * download is defined as indeterminate (see {@link #getDownloadType()}).
     *
     * @return the data size of the complete download (if that is known), otherwise -1.
     */
    public abstract long getDataSize();
}
