package dev.rico.internal.server.projection.routing;

import dev.rico.internal.core.Assert;
import dev.rico.internal.projection.routing.Route;
import dev.rico.internal.server.remoting.event.ClientSessionEventFilter;
import dev.rico.server.client.ClientSession;
import dev.rico.server.remoting.event.RemotingEventBus;

import java.util.Objects;

import static dev.rico.internal.server.projection.routing.RoutingConstants.ROUTING_TOPIC;

public abstract class AbstractRoutingController {

    private final ClientSession clientSession;

    private final RemotingEventBus eventBus;

    private String anchor;

    public AbstractRoutingController(final ClientSession clientSession, final RemotingEventBus eventBus) {
        this.clientSession = Assert.requireNonNull(clientSession, "clientSession");
        this.eventBus = Assert.requireNonNull(eventBus, "eventBus");
    }

    public void init(final String anchor, final String initialLocation) {
        Assert.requireNonNull(anchor, "anchor");
        final Route route = getRoute();
        Assert.requireNonNull(route, "route");
        route.setAnchor(anchor);
        route.setLocation(initialLocation);
        eventBus.subscribe(ROUTING_TOPIC, e -> handle(e.getData()), new ClientSessionEventFilter<>(clientSession.getId()));
    }

    protected abstract Route getRoute();

    protected void handle(final RoutingEvent routingEvent) {
        Assert.requireNonNull(routingEvent, "routingEvent");
        if(Objects.equals(anchor, routingEvent.getAnchor())) {
            final Route route = getRoute();
            Assert.requireNonNull(route, "route");
            route.setLocation(routingEvent.getLocation());
        }
    }
}
