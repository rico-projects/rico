package dev.rico.internal.server.projection.routing;

import dev.rico.server.remoting.event.Topic;

public interface RoutingConstants {

    Topic<RoutingEvent> ROUTING_TOPIC = Topic.create("Routing");
}
