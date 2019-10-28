package dev.rico.core.functional;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckedConsumerTest {

    @Test
    public void testFunctionallity() {

        //given:
        final Stream<String> data = Stream.of("Test1", null, "", "Test4");

        //when:
        final List<Result> results = data.map(Result.ofConsumer(d -> saveDataMockMethod(d))).collect(Collectors.toList());

        //than:
        Assert.assertEquals(results.size(), 4);
        Assert.assertTrue(results.get(0).isSuccessful());
        Assert.assertTrue(results.get(1).isFailed());
        Assert.assertTrue(results.get(2).isFailed());
        Assert.assertTrue(results.get(3).isSuccessful());
    }

    private void saveDataMockMethod(final String data) throws Exception {
        if(data == null || data.length() == 0) {
            throw new IllegalStateException("Data must be > 0");
        }
    }

}
