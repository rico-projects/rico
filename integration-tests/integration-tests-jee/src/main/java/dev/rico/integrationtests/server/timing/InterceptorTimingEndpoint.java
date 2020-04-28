package dev.rico.integrationtests.server.timing;

import dev.rico.integrationtests.timing.TimingConstants;
import dev.rico.internal.server.javaee.timing.TimingInterceptor;
import dev.rico.server.javaee.timing.Timing;

import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static dev.rico.integrationtests.server.HealthEndpoint.RESPONSE_200_OK;

@Path("/interceptor-timing")
@Interceptors(TimingInterceptor.class)
public class InterceptorTimingEndpoint {

    @GET
    @Path("/1")
    @Timing
    public Response testTiming1() throws InterruptedException {
        Thread.sleep(100);
        return RESPONSE_200_OK;
    }

    @GET
    @Path("/2")
    @Timing(TimingConstants.METRICS_NAME)
    public Response testTiming2() throws InterruptedException {
        Thread.sleep(100);
        return RESPONSE_200_OK;
    }

    @GET
    @Path("/3")
    @Timing(value = TimingConstants.METRICS_NAME, description = TimingConstants.METRICS_DESCRIPTION)
    public Response testTiming3() throws InterruptedException {
        Thread.sleep(100);
        return RESPONSE_200_OK;
    }
}
