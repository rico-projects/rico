package dev.rico.integrationtests.server.timing;

import dev.rico.integrationtests.timing.TimingConstants;
import dev.rico.server.javaee.timing.Timing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/interceptor-timing")
public class InterceptorTimingEndpoint {

    @GET
    @Path("/1")
    @Timing
    public void testTiming1() throws InterruptedException {
        Thread.sleep(100);
    }

    @GET
    @Path("/2")
    @Timing(TimingConstants.METRICS_NAME)
    public void testTiming2() throws InterruptedException {
        Thread.sleep(100);
    }

    @GET
    @Path("/3")
    @Timing(value = TimingConstants.METRICS_NAME, description = TimingConstants.METRICS_DESCRIPTION)
    public void testTiming3() throws InterruptedException {
        Thread.sleep(100);
    }
}
