package dev.rico.core.http;

import dev.rico.core.functional.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public abstract class DownloadInputStream extends InputStream {

    public abstract void readTillDone() throws IOException;

    public abstract String getHash();

    public abstract Subscription addDownloadStartListener(final Consumer<Long> listener);

    public abstract Subscription addDownloadPercentageListener(final Consumer<Double> listener);

    public abstract Subscription addDownloadDoneListener(final Consumer<Long> listener);
}
