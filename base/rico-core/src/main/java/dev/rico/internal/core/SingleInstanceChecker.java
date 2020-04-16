package dev.rico.internal.core;

import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SingleInstanceChecker {

    private final Path lockFile;

    public SingleInstanceChecker(final Path lockFile) {
        this.lockFile = Assert.requireNonNull(lockFile, "lockFile");
    }

    public void instanceCheck() {
        final List<ShutdownTask> shutdownRunnables = new ArrayList<>();
        shutdownRunnables.add(() -> Files.deleteIfExists(lockFile));
        try {
            Files.createDirectories(lockFile.getParent());

            final RandomAccessFile randomAccessFileForLock = new RandomAccessFile(lockFile.toFile(), "rw");
            shutdownRunnables.add(() -> randomAccessFileForLock.close());

            final FileLock fileLock = randomAccessFileForLock.getChannel().tryLock();
            if(fileLock == null) {
                throw new IllegalStateException("Another application is already running!");
            }
            shutdownRunnables.add(() -> fileLock.close());

        } catch (Exception e) {
            throw new RuntimeException("Instance checker went wrong!", e);
        } finally {
            final Thread shutdownHookThread = new Thread(() -> {
                shutdownRunnables.stream()
                        .collect(Collectors.toCollection(LinkedList::new))
                        .descendingIterator()
                        .forEachRemaining(task -> {
                            try {
                                task.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

            });
            Runtime.getRuntime().addShutdownHook(shutdownHookThread);
        }
    }}
