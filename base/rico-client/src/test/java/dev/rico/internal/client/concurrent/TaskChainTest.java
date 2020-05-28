package dev.rico.internal.client.concurrent;

import dev.rico.client.concurrent.TaskChain;
import dev.rico.client.concurrent.TaskChainWithInput;
import dev.rico.core.functional.CheckedSupplier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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
        chain.get(100, MILLISECONDS);
    }

    @Test
    public void initialExecutorIsBackground() throws Exception {
        // given
        final AtomicBoolean hasBeenExecuted = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> hasBeenExecuted.set(true))
                .run();

        // when
        chain.get(100, MILLISECONDS);

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
                .execute(() -> {
                })
                .background()
                .execute(() -> {
                })
                .execute(() -> {
                })
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertEquals(usedExecutors, asList(backgroundExecutor, uiExecutor, uiExecutor, backgroundExecutor, backgroundExecutor));
        assertTrue(backgroundHasBeenExecuted.get());
        assertTrue(uiHasBeenExecuted.get());
    }

    @Test
    public void reuseChain() throws Exception {
        // given
        final AtomicInteger counter = new AtomicInteger(0);
        final TaskChainWithInput<Integer> chain = emptyChain
                .supply(() -> counter.incrementAndGet());

        // when
        final Integer result1 = chain.run().get(100, MILLISECONDS);
        final Integer result2 = chain.run().get(100, MILLISECONDS);
        final Integer result3 = chain.run().get(100, MILLISECONDS);

        // then
        assertEquals(result1, (Integer) 1);
        assertEquals(result2, (Integer) 2);
        assertEquals(result3, (Integer) 3);
    }

    @Test
    public void getResultOfChain() throws Exception {
        // given
        final CompletableFuture<Integer> chain = emptyChain
                .supply(() -> 42)
                .run();

        // when
        final Integer result = chain.get(100, MILLISECONDS);

        // then
        assertEquals(result, (Integer) 42);
    }

    @Test
    public void consumeSuppliedValue() throws Exception {
        // given
        final AtomicReference<Integer> check = new AtomicReference<>();
        final CompletableFuture<Void> chain = emptyChain
                .supply(() -> 42)
                .ui()
                .consume(check::set)
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertEquals(check.get(), (Integer) 42);
    }

    @Test
    public void getPassResultToOtherThread() throws Exception {
        // given
        final CompletableFuture<Integer> chain = emptyChain
                .supply(() -> 7)
                .ui()
                .map(i -> i * 3)
                .background()
                .map(i -> i * 2)
                .run();

        // when
        final Integer result = chain.get(100, MILLISECONDS);

        // then
        assertEquals(result, (Integer) 42);
    }

    @Test
    public void completeExceptionally() throws Exception {
        // given
        final IllegalArgumentException ex = new IllegalArgumentException("bla");
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw ex;
                })
                .run();

        // when
        try {
            chain.get(100, MILLISECONDS);
            fail("Should have thrown exception");
        }

        // then
        catch (ExecutionException e) {
            assertEquals(e.getCause(), ex);
        }
    }

    @Test
    public void completeExceptionallyWithError() throws Exception {
        // given
        final Exception ex = new Exception("bla");
        final AtomicReference<Throwable> handledError = new AtomicReference<>();
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw ex;
                })
                .onException(handledError::set)
                .run();

        // when
        chain.get(100, MILLISECONDS);

        //then
        assertNotNull(handledError.get());
        assertEquals(handledError.get().getClass(), CompletionException.class);
        assertEquals(handledError.get().getCause(), ex);
    }

    @Test
    public void doesNotCallExceptionHandler() throws Exception {
        // given
        final CompletableFuture<Integer> chain = emptyChain
                .supply(() -> 42)
                .onException(e -> -1)
                .run();

        // when
        Integer result = chain.get(100, MILLISECONDS);

        // then
        assertEquals(result, (Integer) 42);
    }

    @Test
    public void completeExceptionallyWithResult() throws Exception {
        // given
        final IllegalArgumentException ex = new IllegalArgumentException("bla");
        final CompletableFuture<Integer> chain = emptyChain
                .supply((CheckedSupplier<Integer>) () -> {
                    throw ex;
                })
                .onException(e -> 42)
                .run();

        // when
        Integer result = chain.get(100, MILLISECONDS);

        // then
        assertEquals(result, (Integer) 42);
    }

    @Test
    public void handleException() throws Exception {
        // given
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw new RuntimeException("Error");
                })
                .onException(e -> check.set(true))
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertTrue(check.get());
    }

    @Test
    public void handleExceptionDoesNotStopChain() throws Exception {
        // given
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw new RuntimeException("Error");
                })
                .onException(e -> {
                })
                .execute(() -> check.set(true))
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertTrue(check.get());
    }

    @Test
    public void throwExceptionStopsChain() throws Exception {
        // given
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw new RuntimeException("Error");
                })
                .execute(() -> check.set(true))
                .onException(e -> {
                })
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertFalse(check.get());
    }

    @Test
    public void throwExceptionDoesNotStopExecutorSwitch() throws Exception {
        // given
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw new RuntimeException("Error");
                })
                .ui()
                .onException(e -> check.set(true))
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertTrue(check.get());
        assertEquals(usedExecutors, asList(backgroundExecutor, uiExecutor));
    }

    @Test
    public void completeExceptionallyForSupplier() throws Exception {
        // given
        final IllegalArgumentException ex = new IllegalArgumentException("bla");
        final CompletableFuture<?> chain = emptyChain
                .supply(() -> {
                    throw ex;
                })
                .run();

        // when
        try {
            chain.get(100, MILLISECONDS);
            fail("Should have thrown exception");
        }

        // then
        catch (ExecutionException e) {
            assertEquals(e.getCause(), ex);
        }
    }

    @Test
    public void completeFinally() throws Exception {
        // given
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .thenFinally(() -> check.set(true))
                .run();

        // when
        chain.get(100, MILLISECONDS);

        // then
        assertTrue(check.get());
    }

    @Test
    public void completeFinallyWithResult() throws Exception {
        // given
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Integer> chain = emptyChain
                .supply(() -> 42)
                .thenFinally(() -> check.set(true))
                .run();

        // when
        final Integer result = chain.get(100, MILLISECONDS);

        // then
        assertTrue(check.get());
        assertEquals(result, (Integer) 42);
    }

    @Test
    public void completeFinallyAfterException() throws Exception {
        // given
        final IllegalArgumentException ex = new IllegalArgumentException("bla");
        final AtomicBoolean check = new AtomicBoolean(false);
        final CompletableFuture<Void> chain = emptyChain
                .execute(() -> {
                    throw ex;
                })
                .thenFinally(() -> check.set(true))
                .run();

        // when
        try {
            chain.get(100, MILLISECONDS);
            fail("Should have thrown exception");
        }

        // then
        catch (ExecutionException e) {
            assertEquals(e.getCause(), ex);
        }
        assertTrue(check.get());
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
