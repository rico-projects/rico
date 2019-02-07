package dev.rico.internal.server.projection.routing;

import dev.rico.internal.core.Assert;
import dev.rico.server.remoting.event.RemotingEventBus;

public class Routing {

    private final RemotingEventBus eventBus;

    public Routing(final RemotingEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void route(final String anchor, final String location) {
        Assert.requireNonNull(anchor, "anchor");
        eventBus.publish(RoutingConstants.ROUTING_TOPIC, new RoutingEvent(anchor, location));
    }
}
