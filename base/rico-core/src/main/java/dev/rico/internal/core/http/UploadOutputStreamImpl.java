package dev.rico.internal.core.http;

import dev.rico.core.functional.Subscription;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class UploadOutputStreamImpl extends OutputStream {

    private static final Logger LOG = LoggerFactory.getLogger(UploadOutputStreamImpl.class);

    private final OutputStream wrappedStream;

    private final AtomicBoolean firstWrite;

    private final Executor updateExecutor;

    private final long size;

    private final AtomicLong uploaded;

    private final long updateChunkSize;

    private final AtomicLong lastUpdateSize;

    private final List<Consumer<Double>> uploadPercentageListeners;

    private final List<Consumer<Long>> uploadStartListeners;

    private final List<Consumer<Long>> uploadDoneListeners;

    public UploadOutputStreamImpl(final OutputStream wrappedStream, final long size, final Executor updateExecutor) {
        this.wrappedStream = Assert.requireNonNull(wrappedStream, "wrappedStream");
        this.size = size;
        this.updateExecutor = Assert.requireNonNull(updateExecutor, "updateExecutor");
        this.firstWrite = new AtomicBoolean(true);
        this.uploadPercentageListeners = new CopyOnWriteArrayList<>();
        this.uploadStartListeners = new CopyOnWriteArrayList<>();
        this.uploadDoneListeners = new CopyOnWriteArrayList<>();
        this.updateChunkSize = size / 100;
        this.uploaded = new AtomicLong(0);
        this.lastUpdateSize = new AtomicLong(0);
    }

    public Subscription addUploadStartListener(final Consumer<Long> listener) {
        Assert.requireNonNull(listener, "listener");
        uploadStartListeners.add(listener);
        return () -> uploadStartListeners.remove(listener);
    }

    public Subscription addUploadPercentageListener(final Consumer<Double> listener) {
        Assert.requireNonNull(listener, "listener");
        uploadPercentageListeners.add(listener);
        return () -> uploadPercentageListeners.remove(listener);
    }

    public Subscription addUploadDoneListener(final Consumer<Long> listener) {
        Assert.requireNonNull(listener, "listener");
        uploadDoneListeners.add(listener);
        return () -> uploadDoneListeners.remove(listener);
    }

    @Override
    public void write(final int b) throws IOException {
        if (firstWrite.get()) {
            onStart();
            firstWrite.set(false);
        }
        wrappedStream.write(b);
        update(1);
    }

    @Override
    public void close() throws IOException {
        super.close();
        onDone();
    }

    private void onStart() {
        LOG.trace("Upload of size {} started", size);
        updateExecutor.execute(() -> uploadStartListeners.forEach(l -> l.accept(size)));
    }

    private synchronized void update(int len) {
        long currentSize = uploaded.addAndGet(len);
        if (lastUpdateSize.get() + updateChunkSize <= currentSize) {
            LOG.trace("Uploaded {} bytes of {}", currentSize, size);
            updateExecutor.execute(() -> {
                lastUpdateSize.set(currentSize);
                final double percentageDone = (((double) currentSize) / ((double) size / 100.0)) / 100.0;
                LOG.trace("Uploaded {} %", percentageDone);
                uploadPercentageListeners.forEach(l -> l.accept(percentageDone));
            });
        }
        if (currentSize == size) {
            onDone();
        }
    }

    private void onDone() {
        LOG.trace("Uploade of size {} done", size);
        updateExecutor.execute(() -> uploadDoneListeners.forEach(l -> l.accept(size)));
    }

}
