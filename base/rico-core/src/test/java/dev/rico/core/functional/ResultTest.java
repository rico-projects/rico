package dev.rico.core.functional;

import org.testng.annotations.Test;

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
}
