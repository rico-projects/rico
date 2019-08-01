package dev.rico.core.functional;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CheckedFunctionTest {

    @Test
    public void testSuccess() {
        // when
        final Result<String> result = Optional.of("Hello")
                .map(Result.of(s -> s + " World"))
                .orElse(Result.sucess("WRONG"));

        // then
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertEquals(result.getResult(), "Hello World");
        try {
            result.getException();
            Assert.fail("A success should not have an error");
        } catch (final Exception e) {
            Assert.assertEquals(e.getClass(), IllegalStateException.class);
        }
    }

    @Test
    public void testFail() {
        // when
        final Result<String> result = Optional.of("Hello")
                .map(Result.<String, String>of(s -> throwException()))
                .orElse(Result.sucess("WRONG"));

        // then
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccessful());
        Assert.assertNotNull(result.getException());
        Assert.assertEquals(result.getException().getClass(), RuntimeException.class);
        Assert.assertEquals(result.getException().getMessage(), "ERROR");
        try {
            result.getResult();
            Assert.fail("A fail should not have an result");
        } catch (final Exception e) {
            Assert.assertEquals(e.getClass(), IllegalStateException.class);
        }
    }

    @Test
    public void testInStream() {
        // when
        List<Result<String>> results = IntStream.range(0, 10)
                .mapToObj(i -> i)
                .map(Result.of(i -> i.intValue() % 2 == 0 ? "" : throwException()))
                .collect(Collectors.toList());

        // then
        Assert.assertNotNull(results);
        Assert.assertEquals(results.size(), 10);
        Assert.assertEquals(results.stream().filter(r -> r.isSuccessful()).count(), 5);
    }

    private <A> A throwException() {
        throw new RuntimeException("ERROR");
    }
}
