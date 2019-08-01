package dev.rico.core.functional;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

public class CheckedSupplierTest {

    @Test
    public void testSuccess() {
        //given
        final Result<String> result = Optional.ofNullable((Result<String>) null).orElseGet(Result.of(() -> "Hello"));

        //than
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertEquals(result.getResult(), "Hello");
        try {
            result.getException();
            Assert.fail("A success should not have an error");
        } catch (final Exception e) {
            Assert.assertEquals(e.getClass(), IllegalStateException.class);
        }
    }

    @Test
    public void testFail() {
        //given
        final Result<String> result = Optional.ofNullable((Result<String>) null).orElseGet(Result.of(() -> throwException()));

        //than
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

    private <A> A throwException() {
        throw new RuntimeException("ERROR");
    }
}
