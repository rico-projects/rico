package dev.rico.internal.server.projection.routing;

import dev.rico.internal.core.Assert;
import dev.rico.internal.projection.metadata.KeyValue;
import dev.rico.internal.projection.routing.Route;
import dev.rico.internal.server.remoting.event.ClientSessionEventFilter;
import dev.rico.remoting.BeanManager;
import dev.rico.server.client.ClientSession;
import dev.rico.server.remoting.event.RemotingEventBus;

import java.io.Serializable;
import java.util.Objects;

import static dev.rico.internal.server.projection.routing.RoutingServerConstants.ROUTING_TOPIC;

public abstract class AbstractRoutingController {

    private final ClientSession clientSession;

    private final RemotingEventBus eventBus;

    private final BeanManager beanManager;

    private String anchor;

    public AbstractRoutingController(final ClientSession clientSession, final RemotingEventBus eventBus, final BeanManager beanManager) {
        this.clientSession = Assert.requireNonNull(clientSession, "clientSession");
        this.eventBus = Assert.requireNonNull(eventBus, "eventBus");
        this.beanManager = Assert.requireNonNull(beanManager, "beanManager");
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

            routingEvent.getParameters().entrySet().stream().map(e -> {
                final KeyValue<Serializable> keyValue = beanManager.create(KeyValue.class);
                keyValue.setKey(e.getKey());
                keyValue.setValue(e.getValue());
                return keyValue;
            }).forEach(v -> route.getParameters().add(v));

            route.setLocation(routingEvent.getLocation());
        }
    }
}
