package dev.rico.internal.server.projection.routing;

import dev.rico.internal.core.Assert;
import dev.rico.server.remoting.event.RemotingEventBus;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class Routing {

    private final RemotingEventBus eventBus;

    public Routing(final RemotingEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void route(final String anchor, final String location) {
        route(anchor, location, Collections.emptyMap());
    }

    public void route(final String anchor, final String location, final Map<String, Serializable> parameters) {
        Assert.requireNonNull(anchor, "anchor");
        eventBus.publish(RoutingServerConstants.ROUTING_TOPIC, new RoutingEvent(anchor, location, parameters));
    }
}
