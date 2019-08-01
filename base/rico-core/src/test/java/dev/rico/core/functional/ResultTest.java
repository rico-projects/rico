package dev.rico.core.functional;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ResultTest {


    @Test
    public void testSucess() {
        //given
        final Result<String> result = Result.sucess("Hello");

        //than
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
        final Result<String> result = Result.fail(new RuntimeException("ERROR"));

        //than
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

}
