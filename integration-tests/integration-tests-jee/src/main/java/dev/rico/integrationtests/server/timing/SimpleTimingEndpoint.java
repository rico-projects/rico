package dev.rico.integrationtests.server.timing;

import dev.rico.integrationtests.timing.TimingConstants;
import dev.rico.server.timing.Metric;
import dev.rico.server.timing.ServerTiming;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/simple-timing")
public class SimpleTimingEndpoint {

    @Inject
    private ServerTiming timing;

    @GET
    @Path("/1")
    public void testTiming1() throws InterruptedException {
        final Metric metric = timing.start(null);
        Thread.sleep(100);
        metric.stop();
    }

    @GET
    @Path("/2")
    public void testTiming2() throws InterruptedException {
        final Metric metric = timing.start(TimingConstants.METRICS_NAME);
        Thread.sleep(100);
        metric.stop();
    }

    @GET
    @Path("/3")
    public void testTiming3() throws InterruptedException {
        final Metric metric = timing.start(TimingConstants.METRICS_NAME, TimingConstants.METRICS_DESCRIPTION);
        Thread.sleep(100);
        metric.stop();
    }


}
