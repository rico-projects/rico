package dev.rico.core.functional;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
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
        // given
        final RuntimeException exception = new RuntimeException();

        // when
        final Result<String> result = Result.fail(exception);

        // then
        assertFalse(result.isSuccessful());
        assertTrue(result.isFailed());
        assertNotNull(result.getException());
        assertSame(result.getException(), exception);

        result.getResult(); // should throw an exception
    }

    @Test
    public void testResultOfRunnable() {
        // when:
        final Result<Void> result = Result.of(() -> { });

        // then:
        assertTrue(result.isSuccessful());
    }

    @Test
    public void testResultOfRunnableThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = Result.of((CheckedRunnable) () -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testResultOfSupplier() {
        // when:
        final Result<String> result = Result.of(() -> "test");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
    }

    @Test
    public void testResultOfSupplierThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = Result.of(() -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testResultOfFunction() {
        // when:
        final ResultWithInput<String, String> result = Result.of("bar", v -> "test");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfFunctionThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final ResultWithInput<String, String> result = Result.of("bar", v -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfFunctionalFunction() {
        // given:
        final CheckedFunction<String, String> function = (String v) -> "test";

        // when:
        final ResultWithInput<String, String> result = Result.of(function).apply("bar");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfFunctionalFunctionThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();
        final CheckedFunction<String, String> function = (String v) -> { throw exception; };

        // when:
        final ResultWithInput<String, String> result = Result.of(function).apply("bar");

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfConsumer() {
        // when:
        final ResultWithInput<String, Void> result = Result.ofConsumer("bar", v -> {});

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final ResultWithInput<String, Void> result = Result.ofConsumer("bar", v -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfFunctionalConsumer() {
        // given:
        final CheckedConsumer<String> consumer = v -> { };

        // when:
        final ResultWithInput<String, Void> result = Result.ofConsumer(consumer).apply("bar");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testResultOfFunctionalConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();
        final CheckedConsumer<String> consumer = v -> { throw exception; };

        // when:
        final ResultWithInput<String, Void> result = Result.ofConsumer(consumer).apply("bar");

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
        assertEquals(result.getInput(), "bar");
    }

    @Test
    public void testSuccessfulResultMap() {
        // when:
        final Result<String> result = successfulResult().map(s -> s + "-" + s);

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test-test");
    }

    @Test
    public void testFailedResultMap() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult(exception).map(s -> s + "-" + s);

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultMapThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = successfulResult().map(s -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testFailedResultMapThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult(exception).map(s -> { throw new IOException(); });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultRecover() {
        // when:
        final Result<String> result = successfulResult("test").recover(e -> "bar");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
    }

    @Test
    public void testFailedResultRecover() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult(exception).recover(e -> "bar");

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "bar");
    }

    @Test
    public void testSuccessfulResultRecoverThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = successfulResult().recover(s -> { throw exception; });

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
    }

    @Test
    public void testFailedResultRecoverThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult().recover(s -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessConsumer() {
        // given:
        final AtomicReference<String> value = new AtomicReference<>("");

        // when:
        final Result<Void> result = successfulResult().onSuccess(value::set);

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(value.get(), "test");
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
        assertSame(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = successfulResult().onSuccess(s -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testFailedResultOnSuccessConsumerThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = failedResult(exception).onSuccess(s -> { throw new IOException(); });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessRunnable() {
        // given:
        final AtomicReference<String> value = new AtomicReference<>("");

        // when:
        final Result<Void> result = successfulResult().onSuccess(() -> value.set("bar"));

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(value.get(), "bar");
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
        assertSame(result.getException(), exception);
    }

    @Test
    public void testSuccessfulResultOnSuccessRunnableThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = successfulResult().onSuccess(() -> { throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
    }

    @Test
    public void testFailedResultOnSuccessRunnableThrowingException() {
        // given:
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<Void> result = failedResult(exception).onSuccess(() -> { throw new IOException(); });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
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
        assertSame(value.get(), exception);
    }

    @Test
    public void testSuccessfulResultOrElse() {
        // when:
        final String result = successfulResult("bar").orElse("test");

        // then:
        assertEquals(result, "bar");
    }

    @Test
    public void testFailedResultOrElse() {
        // when:
        final String result = failedResult().orElse("bar");

        // then:
        assertEquals(result, "bar");
    }

    @Test
    public void testSuccessfulResultOnFailureWithInput() {
        // given:
        final AtomicReference<String> input = new AtomicReference<>(null);
        final AtomicReference<Exception> value = new AtomicReference<>(null);

        // when:
        successfulResult().onFailure((i, e) -> { input.set(i); value.set(e); });

        // then:
        assertNull(value.get());
        assertNull(input.get());
    }

    @Test
    public void testFailedResultOnFailureWithInput() {
        // given:
        final AtomicReference<String> input = new AtomicReference<>(null);
        final AtomicReference<Exception> value = new AtomicReference<>(null);
        final RuntimeException exception = new RuntimeException();

        // when:
        failedResult(exception).onFailure((i, e) -> { input.set(i); value.set(e); });

        // then:
        assertSame(value.get(), exception);
        assertEquals(input.get(), "test");
    }

    @Test
    public void testSuccessfulResultRecoverWithInput() {
        // given:
        final AtomicReference<String> input = new AtomicReference<>(null);

        // when:
        final Result<String> result = successfulResult("test").recover((i, e) -> { input.set(i); return "bar"; });

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
        assertNull(input.get());
    }

    @Test
    public void testFailedResultRecoverWithInput() {
        // given:
        final AtomicReference<String> input = new AtomicReference<>(null);
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult(exception).recover((i, e) -> { input.set(i); return "bar"; });

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "bar");
        assertEquals(input.get(), "test");
    }

    @Test
    public void testSuccessfulResultRecoverWithInputThrowingException() {
        // given:
        final AtomicReference<String> input = new AtomicReference<>(null);
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = successfulResult().recover((i, e) -> { input.set(i); throw exception; });

        // then:
        assertTrue(result.isSuccessful());
        assertEquals(result.getResult(), "test");
        assertNull(input.get());
    }

    @Test
    public void testFailedResultRecoverWithInputThrowingException() {
        // given:
        final AtomicReference<String> input = new AtomicReference<>(null);
        final RuntimeException exception = new RuntimeException();

        // when:
        final Result<String> result = failedResult().recover((i, e) -> { input.set(i); throw exception; });

        // then:
        assertTrue(result.isFailed());
        assertSame(result.getException(), exception);
        assertEquals(input.get(), "test");
    }

    private ResultWithInput<String, String> successfulResult() {
        return successfulResult("test");
    }

    private ResultWithInput<String, String> successfulResult(String value) {
        return Result.of(value, String::toString);
    }

    private ResultWithInput<String, String> failedResult() {
        return failedResult(new RuntimeException());
    }

    private ResultWithInput<String, String> failedResult(Exception e) {
        return Result.of("test", s -> { throw e; });
    }
}
