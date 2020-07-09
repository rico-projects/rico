package dev.rico.integrationtests.server.timing;

import dev.rico.integrationtests.timing.TimingConstants;
import dev.rico.server.timing.ServerTimer;
import dev.rico.server.timing.ServerTiming;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static dev.rico.integrationtests.server.HealthEndpoint.RESPONSE_200_OK;

@Path("/simple-timing")
public class SimpleTimingEndpoint {

    @Inject
    private ServerTiming timing;

    @GET
    @Path("/1")
    public Response testTiming1() throws InterruptedException {
        final ServerTimer serverTimer = timing.start(null);
        return recordAndGetResponse(serverTimer);
    }

    @GET
    @Path("/2")
    public Response testTiming2() throws InterruptedException {
        final ServerTimer serverTimer = timing.start(TimingConstants.METRICS_NAME);
        return recordAndGetResponse(serverTimer);
    }

    @GET
    @Path("/3")
    public Response testTiming3() throws InterruptedException {
        final ServerTimer serverTimer = timing.start(TimingConstants.METRICS_NAME, TimingConstants.METRICS_DESCRIPTION);
        return recordAndGetResponse(serverTimer);
    }

    private Response recordAndGetResponse(ServerTimer serverTimer) throws InterruptedException {
        try (serverTimer) {
            Thread.sleep(100);
            return RESPONSE_200_OK;
        }
    }

    @GET
    @Path("/4")
    public Response testTiming4() throws Exception {
        timing.record(TimingConstants.METRICS_NAME, TimingConstants.METRICS_DESCRIPTION, () -> Thread.sleep(100));
        return RESPONSE_200_OK;
    }


}
