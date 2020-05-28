package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.TaskChain;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * ...
 */
public class TaskChainTest {

    private Executor backgroundExecutor;
    private Executor uiExecutor;

    private List<Executor> usedExecutors;
    private TaskChain emptyChain;

    @BeforeMethod
    public void setUp() {
        backgroundExecutor = new TestingExecutor();
        uiExecutor = new TestingExecutor();
        usedExecutors = new ArrayList<>();
        emptyChain = new TaskChainImpl(backgroundExecutor, uiExecutor);
    }

    @Test
    public void emptyChainWorks() throws Exception {
        // given
        final CompletableFuture<Void> chain = emptyChain.run();

        // then
        chain.get(100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void initialExecutorIsBackground() throws Exception {
        // given
        final AtomicBoolean hasBeenExecuted = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> hasBeenExecuted.set(true))
                .run();

        // when
        chain.get(100, TimeUnit.MILLISECONDS);

        // then
        assertEquals(usedExecutors, asList(backgroundExecutor));
        assertTrue(hasBeenExecuted.get());
    }

    @Test
    public void switchingFromAndToUiWorks() throws Exception {
        // given
        final AtomicBoolean backgroundHasBeenExecuted = new AtomicBoolean(false);
        final AtomicBoolean uiHasBeenExecuted = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> backgroundHasBeenExecuted.set(true))
                .ui()
                .execute(() -> uiHasBeenExecuted.set(true))
                .execute(() -> {})
                .background()
                .execute(() -> {})
                .execute(() -> {})
                .run();

        // when
        chain.get(100, TimeUnit.MILLISECONDS);

        // then
        assertEquals(usedExecutors, asList(backgroundExecutor, uiExecutor, uiExecutor, backgroundExecutor, backgroundExecutor));
        assertTrue(backgroundHasBeenExecuted.get());
        assertTrue(uiHasBeenExecuted.get());
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingBackgroundExecutor() {
        new TaskChainImpl(null, uiExecutor);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingUiExecutor() {
        new TaskChainImpl(backgroundExecutor, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingRunnable() {
        emptyChain.execute(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingSupplier() {
        emptyChain.supply(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingExceptionHandler() {
        emptyChain.onException(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingFinally() {
        emptyChain.thenFinally(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingMapper() {
        emptyChain.supply(() -> 0).map(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingConsumer() {
        emptyChain.supply(() -> 0).consume(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingExceptionHandlerWithOutput() {
        emptyChain.supply(() -> 0).onException(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeForMissingFinallyWithOutput() {
        emptyChain.supply(() -> 0).thenFinally(null);
    }


    private class TestingExecutor implements Executor {

        private final Executor executor = Executors.newSingleThreadExecutor();

        @Override
        public void execute(Runnable command) {
            usedExecutors.add(this);
            executor.execute(command);
        }
    }
}
