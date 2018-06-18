package dev.rico.internal.core.http;

import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class DownloadInputStream extends InputStream {

    private final static Logger LOG = LoggerFactory.getLogger(DownloadInputStream.class);

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

    public DownloadInputStream(final InputStream inputStream, final long dataSize, final Executor updateExecutor) {
        this.updateExecutor = Assert.requireNonNull(updateExecutor, "updateExecutor");
        this.dataSize = dataSize;
        this.downloaded = new AtomicLong(0);
        this.lastUpdateSize = new AtomicLong(0);
        this.firstRead = new AtomicBoolean(true);
        this.downloadPercentageListeners = new ArrayList<>();
        this.downloadStartListeners = new ArrayList<>();
        this.downloadDoneListeners = new ArrayList<>();
        this.updateChunkSize = dataSize / 100;
        try {
            this.wrappedStream = ConnectionUtils.createMD5HashStream(inputStream);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No HASH_ALGORITHM support");
        }
    }

    public void readTillDone() throws IOException {
        while (read() >= 0) {}
    }

    public String getHash() {
        return ConnectionUtils.toHex(wrappedStream.getMessageDigest().digest());
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

    @Override
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
}
