package dev.rico.core.http;

import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
public abstract class DownloadInputStream extends InputStream {

    public abstract CompletableFuture<String> getHash();

    public abstract Subscription addDownloadStartListener(final Consumer<Long> listener);

    public abstract Subscription addDownloadPercentageListener(final Consumer<Double> listener);

    public abstract Subscription addDownloadDoneListener(final Consumer<Long> listener);
}
