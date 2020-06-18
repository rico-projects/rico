package dev.rico.core.functional;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class ResultTest {

    @Test
    public void testSuccess() {
        // when
        final Result<String> result = Result.success("Hello");

        // then
        assertTrue(result.isSuccessful());
        assertFalse(result.isFailed());
        assertEquals(result.getResult(), "Hello");
        assertNull(result.getException());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testFail() {
        // when
        final Result<String> result = Result.fail(new RuntimeException("ERROR"));

        // then
        assertFalse(result.isSuccessful());
        assertTrue(result.isFailed());
        assertNotNull(result.getException());
        assertEquals(result.getException().getClass(), RuntimeException.class);
        assertEquals(result.getException().getMessage(), "ERROR");

        result.getResult(); // should throw an exception
    }

    @Test
    public void testResultOfConsumer() {
        // when:
        final Result<Void> result = Result.ofConsumer(v -> {
        }).apply(null);

        // then:
        assertTrue(result.isSuccessful());
    }

    @Test
    public void testResultOfConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = Result.ofConsumer(v -> { throw exception; }).apply(null);

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testResultOfSupplier() {
        // when:
        final Result<String> result = Result.of(() -> "test").get();

        // then:
        assertTrue(result.isSuccessful());
        assertEquals("test", result.getResult());
    }

    @Test
    public void testResultOfSupplierThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<?> result = Result.of(() -> { throw exception; }).get();

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testResultOfFunction() {
        // when:
        final Result<String> result = Result.of(v -> "test").apply(null);

        // then:
        assertTrue(result.isSuccessful());
        assertEquals("test", result.getResult());
    }

    @Test
    public void testResultOfFunctionThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<?> result = Result.of(v -> { throw exception; }).apply(null);

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testResultWithInput() {
        // when:
        final ResultWithInput<?, String> result = Result.withInput(v -> "test").apply("bar");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals("test", result.getResult());
        assertEquals("bar", result.getInput());
    }

    @Test
    public void testResultWithInputThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final ResultWithInput<?, ?> result = Result.withInput(v -> { throw exception; }).apply("bar");

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
        assertEquals("bar", result.getInput());
    }

    @Test
    public void testSuccessfulResultMap() {
        // when:
        final Result<String> result = successfulResult().map(s -> s + "-" + s);

        // then:
        assertTrue(result.isSuccessful());
        assertEquals("test-test", result.getResult());
    }

    @Test
    public void testFailedResultMap() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult(exception).map(s -> s + "-" + s);

        // then:
        assertTrue(result.isFailed());
        assertEquals(exception, result.getException());
    }

    @Test
    public void testSuccessfulResultMapThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = successfulResult().map(s -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testFailedResultMapThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult(exception).map(s -> { throw new IOException(); });

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessConsumer() {
        // given:
        final AtomicReference<String> value = new AtomicReference<>("");

        // when:
        final Result<Void> result = successfulResult().onSuccess(value::set);

        // then:
        assertTrue(result.isSuccessful());
        assertEquals("test", value.get());
    }

    @Test
    public void testFailedResultOnSuccessConsumer() {
        // given:
        final RuntimeException exception = new RuntimeException();
        final AtomicReference<String> value = new AtomicReference<>("");

        // when:
        final Result<Void> result = failedResult(exception).onSuccess(value::set);

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = successfulResult().onSuccess(s -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testFailedResultOnSuccessConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = failedResult(exception).onSuccess(s -> { throw new IOException(); });

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessRunnable() {
        // given:
        final AtomicReference<String> value = new AtomicReference<>("");

        // when:
        final Result<Void> result = successfulResult().onSuccess(() -> value.set("bar"));

        // then:
        assertTrue(result.isSuccessful());
        assertEquals("bar", value.get());
    }

    @Test
    public void testFailedResultOnSuccessRunnable() {
        // given:
        final RuntimeException exception = new RuntimeException();
        final AtomicReference<String> value = new AtomicReference<>("");

        // when:
        final Result<Void> result = failedResult(exception).onSuccess(() -> value.set("bar"));

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessRunnableThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = successfulResult().onSuccess(() -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testFailedResultOnSuccessRunnableThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = failedResult(exception).onSuccess(() -> { throw new IOException(); });

        // then:
        assertTrue(result.isFailed());
        assertEquals(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnFailure() {
        // given:
        final AtomicReference<Exception> value = new AtomicReference<>(null);

        // when:
        successfulResult().onFailure(value::set);

        // then:
        assertNull(value.get());
    }

    @Test
    public void testFailedResultOnFailure() {
        // given:
        final AtomicReference<Exception> value = new AtomicReference<>(null);
        final RuntimeException exception = new RuntimeException();

        // when:
        failedResult(exception).onFailure(value::set);

        // then:
        assertEquals(value.get(), exception);
    }


    private ResultWithInput<String, String> successfulResult() {
        return Result.withInput(String::toString).apply("test");
    }


    private ResultWithInput<String, String> failedResult(Exception e) {
        return Result.<String, String>withInput(s -> {throw e;}).apply("test");
    }
}
