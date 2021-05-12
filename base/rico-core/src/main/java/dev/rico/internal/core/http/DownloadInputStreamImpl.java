package dev.rico.internal.core.http;

import dev.rico.core.functional.Subscription;
import dev.rico.core.http.DownloadInputStream;
import dev.rico.core.http.DownloadType;
import dev.rico.core.http.HttpResponse;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class DownloadInputStreamImpl extends DownloadInputStream {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadInputStreamImpl.class);

    private final List<Consumer<Double>> downloadPercentageListeners;

    private final List<Consumer<Long>> downloadStartListeners;

    private final List<Consumer<Long>> downloadDoneListeners;

    private final List<Consumer<Exception>> onErrorListeners;

    private final Executor updateExecutor;

    private final DigestInputStream wrappedStream;

    private final long dataSize;

    private final AtomicLong updateChunkSize;

    private final AtomicLong downloaded;

    private final AtomicLong lastUpdateSize;

    private final AtomicBoolean firstRead;

    private final DownloadType downloadType;

    public DownloadInputStreamImpl(final InputStream inputStream, final long dataSize, final Executor updateExecutor) {
        this.updateExecutor = Assert.requireNonNull(updateExecutor, "updateExecutor");
        this.dataSize = dataSize > 0 ? dataSize : -1;
        if (dataSize > 0) {
            downloadType = DownloadType.NORMAL;
        } else {
            downloadType = DownloadType.INDETERMINATE;
        }
        this.downloaded = new AtomicLong(0);
        this.lastUpdateSize = new AtomicLong(0);
        this.firstRead = new AtomicBoolean(true);
        this.downloadPercentageListeners = new CopyOnWriteArrayList<>();
        this.downloadStartListeners = new CopyOnWriteArrayList<>();
        this.downloadDoneListeners = new CopyOnWriteArrayList<>();
        this.onErrorListeners = new CopyOnWriteArrayList<>();
        this.updateChunkSize = new AtomicLong(1000);
        if (dataSize > 0) {
            this.updateChunkSize.set(dataSize / 1000);
        }

        try {
            this.wrappedStream = ConnectionUtils.createMD5HashStream(inputStream);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No HASH_ALGORITHM support");
        }
    }

    public void setUpdateChunkSize(final long updateChunkSize) {
        if (updateChunkSize <= 0) {
            throw new IllegalArgumentException("chunk size must be > 0");
        }
        this.updateChunkSize.set(updateChunkSize);
    }

    public DownloadType getDownloadType() {
        return downloadType;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        try {
            final int count = super.read(b, off, len);
            if (count < 0) {
                onDone();
            }
            return count;
        } catch (final Exception e) {
            try {
                onError(e);
            } finally {
                throw e;
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } catch (final Exception e) {
            try {
                onError(e);
            } finally {
                throw e;
            }
        }
        onDone();
    }

    public int read() throws IOException {
        try {
            if (firstRead.get()) {
                onStart();
                firstRead.set(false);
            }
            final int value = wrappedStream.read();
            if (value >= 0) {
                update(1);
            }
            return value;
        } catch (final Exception e) {
            try {
                onError(e);
            } finally {
                throw e;
            }
        }
    }

    @Override
    public int available() throws IOException {
        try {
            final int available = super.available();
            if (available < 0) {
                onDone();
            }
            return available;
        } catch (final Exception e) {
            try {
                onError(e);
            } finally {
                throw e;
            }
        }
    }

    public Subscription addDownloadStartListener(final Consumer<Long> listener) {
        Assert.requireNonNull(listener, "listener");
        downloadStartListeners.add(listener);
        return () -> downloadStartListeners.remove(listener);
    }

    public Subscription addDownloadPercentageListener(final Consumer<Double> listener) {
        Assert.requireNonNull(listener, "listener");
        downloadPercentageListeners.add(listener);
        return () -> downloadPercentageListeners.remove(listener);
    }

    public Subscription addDownloadDoneListener(final Consumer<Long> listener) {
        Assert.requireNonNull(listener, "listener");
        downloadDoneListeners.add(listener);
        return () -> downloadDoneListeners.remove(listener);
    }

    public Subscription addDownloadErrorListener(final Consumer<Exception> listener) {
        Assert.requireNonNull(listener, "listener");
        onErrorListeners.add(listener);
        return () -> onErrorListeners.remove(listener);
    }

    private void onDone() {
        LOG.trace("Download of size {} done", dataSize);
        updateExecutor.execute(() -> downloadDoneListeners.forEach(l -> l.accept(dataSize)));
    }

    private void onStart() {
        LOG.trace("Downloaded of size {} started", dataSize);
        updateExecutor.execute(() -> downloadStartListeners.forEach(l -> l.accept(dataSize)));
    }

    private void onError(final Exception e) {
        LOG.trace("Downloaded of size {} started", dataSize);
        updateExecutor.execute(() -> onErrorListeners.forEach(l -> l.accept(e)));
    }

    private synchronized void update(final int len) {
        final long currentSize = downloaded.addAndGet(len);
        if (lastUpdateSize.get() + updateChunkSize.get() <= currentSize) {
            LOG.trace("Downloaded {} bytes of {}", currentSize, dataSize);
            lastUpdateSize.set(currentSize);
            updateExecutor.execute(() -> {
                if (downloadType == DownloadType.NORMAL) {
                    final double percentageDone = (((double) currentSize) / ((double) dataSize / 100.0)) / 100.0;
                    LOG.trace("Downloaded {} %", percentageDone);
                    downloadPercentageListeners.forEach(l -> l.accept(percentageDone));
                } else {
                    downloadPercentageListeners.forEach(l -> l.accept(-1d));
                }
            });
        }
    }

    public long getDownloaded() {
        return downloaded.get();
    }

    public long getDataSize() {
        return dataSize;
    }

    public static DownloadInputStreamImpl map(final HttpResponse<InputStream> response, final Executor executor) {
        return map(ConnectionUtils.getContentName(response), response.getContent(), response.getContentSize(), executor);
    }

    public static DownloadInputStreamImpl map(final String name, final InputStream inputStream, final long length, final Executor executor) {
        final DownloadInputStreamImpl downloadInputStream = new DownloadInputStreamImpl(inputStream, length, executor);
        return downloadInputStream;
    }
}
