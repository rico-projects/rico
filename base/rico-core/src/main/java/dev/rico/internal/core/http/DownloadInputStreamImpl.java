package dev.rico.internal.core.http;

import dev.rico.core.functional.Subscription;
import dev.rico.core.http.DownloadInputStream;
import dev.rico.core.http.HttpResponse;
import dev.rico.internal.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class DownloadInputStreamImpl extends DownloadInputStream {

    private final static Logger LOG = LoggerFactory.getLogger(DownloadInputStreamImpl.class);

    private final List<Consumer<Double>> downloadPercentageListeners;

    private final List<Consumer<Long>> downloadStartListeners;

    private final List<Consumer<Long>> downloadDoneListeners;

    private final Executor updateExecutor;

    private final DigestInputStream wrappedStream;

    private final long dataSize;

    private final long updateChunkSize;

    private final AtomicLong downloaded;

    private final AtomicLong lastUpdateSize;

    private final AtomicBoolean firstRead;

    public DownloadInputStreamImpl(final InputStream inputStream, final long dataSize, final Executor updateExecutor) {
        this.updateExecutor = Assert.requireNonNull(updateExecutor, "updateExecutor");
        this.dataSize = dataSize;
        this.downloaded = new AtomicLong(0);
        this.lastUpdateSize = new AtomicLong(0);
        this.firstRead = new AtomicBoolean(true);
        this.downloadPercentageListeners = new CopyOnWriteArrayList<>();
        this.downloadStartListeners = new CopyOnWriteArrayList<>();
        this.downloadDoneListeners = new CopyOnWriteArrayList<>();
        this.updateChunkSize = dataSize / 100;
        try {
            this.wrappedStream = ConnectionUtils.createMD5HashStream(inputStream);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No HASH_ALGORITHM support");
        }
    }

    public CompletableFuture<String> getHash() {
        final CompletableFuture<String> future = new CompletableFuture<>();
        addDownloadDoneListener(size -> {
            future.complete(ConnectionUtils.toHex(wrappedStream.getMessageDigest().digest()));
        });
        return future;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int count = super.read(b, off, len);
        if(count < 0) {
            onDone();
        }
        return count;
    }

    @Override
    public void close() throws IOException {
        super.close();
        onDone();
    }

    public int read() throws IOException {
        if (firstRead.get()) {
            onStart();
            firstRead.set(false);
        }
        final int value = wrappedStream.read();
        if(value >= 0) {
            update(1);
        }
        return value;
    }

    @Override
    public int available() throws IOException {
        final int available = super.available();
        if (available < 0) {
            onDone();
        }
        return available;
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

    private void onDone() {
        LOG.trace("Download of size {} done", dataSize);

        updateExecutor.execute(() -> downloadDoneListeners.forEach(l -> l.accept(dataSize)));
    }

    private void onStart() {
        LOG.trace("Downloaded of size {} started", dataSize);
        updateExecutor.execute(() -> downloadStartListeners.forEach(l -> l.accept(dataSize)));
    }

    private synchronized void update(int len) {
        long currentSize = downloaded.addAndGet(len);
        if (lastUpdateSize.get() + updateChunkSize <= currentSize) {
            LOG.trace("Downloaded {} bytes of {}", currentSize, dataSize);
            updateExecutor.execute(() -> {
                lastUpdateSize.set(currentSize);
                final double percentageDone = (((double) currentSize) / ((double) dataSize / 100.0)) / 100.0;
                LOG.trace("Downloaded {} %", percentageDone);
                downloadPercentageListeners.forEach(l -> l.accept(percentageDone));
            });
        }
    }

    public static DownloadInputStreamImpl map(final HttpResponse<InputStream> response, final Executor executor) {
        return map(ConnectionUtils.getContentName(response), response.getContent(), response.getContentSize(), executor);
    }

    public static DownloadInputStreamImpl map(final String name, final InputStream inputStream, final long length, final Executor executor) {
        final DownloadInputStreamImpl downloadInputStream = new DownloadInputStreamImpl(inputStream, length, executor);
        return downloadInputStream;
    }
}
