package dev.rico.integrationtests.timing;

import dev.rico.client.Client;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.HttpHeader;
import dev.rico.core.http.HttpResponse;
import dev.rico.integrationtests.AbstractIntegrationTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Objects;

import static dev.rico.integrationtests.timing.TimingConstants.METRICS_DESCRIPTION;
import static dev.rico.integrationtests.timing.TimingConstants.METRICS_NAME;

public class SimpleTimingTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCall1(final String containerType, final String endpoint) throws Exception {

        //given
        final String url = endpoint + "/rest/simple-timing/1";

        //when
        final HttpClient client = Client.getService(HttpClient.class);
        final HttpResponse<Void> response = client.get(url).withoutContent().withoutResult().execute().get();

        //then
        final HttpHeader timingHeader = response.getHeaders().stream()
                .filter(h -> Objects.equals(h.getName(), "Server-Timing"))
                .findAny().orElse(null);
        Assert.assertNotNull(timingHeader);
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCall2(final String containerType, final String endpoint) throws Exception {

        //given
        final String url = endpoint + "/rest/simple-timing/2";

        //when
        final HttpClient client = Client.getService(HttpClient.class);
        final HttpResponse<Void> response = client.get(url).withoutContent().withoutResult().execute().get();

        //then
        final HttpHeader timingHeader = response.getHeaders().stream()
                .filter(h -> Objects.equals(h.getName(), "Server-Timing"))
                .findAny().orElse(null);
        Assert.assertNotNull(timingHeader);
        final String content = timingHeader.getContent();
        Assert.assertNotNull(content);
        Assert.assertTrue(content.contains(METRICS_NAME));
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCall3(String containerType, String endpoint) throws Exception {

        //given
        final String url = endpoint + "/rest/simple-timing/3";

        //when
        final HttpClient client = Client.getService(HttpClient.class);
        final HttpResponse<Void> response = client.get(url).withoutContent().withoutResult().execute().get();

        //then
        final HttpHeader timingHeader = response.getHeaders().stream()
                .filter(h -> Objects.equals(h.getName(), "Server-Timing"))
                .findAny().orElse(null);
        Assert.assertNotNull(timingHeader);
        final String content = timingHeader.getContent();
        Assert.assertNotNull(content);
        Assert.assertTrue(content.contains(METRICS_NAME));
        Assert.assertTrue(content.contains(METRICS_DESCRIPTION));
    }
}
