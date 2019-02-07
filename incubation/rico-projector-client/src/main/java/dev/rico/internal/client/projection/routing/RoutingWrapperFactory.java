package dev.rico.internal.client.projection.routing;

import dev.rico.client.remoting.ControllerProxy;
import dev.rico.internal.client.projection.projection.ProjectionFactory;
import dev.rico.internal.client.projection.projection.Projector;
import dev.rico.internal.core.Assert;
import dev.rico.internal.projection.routing.Route;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class RoutingWrapperFactory implements ProjectionFactory<Route> {

    private final Routing routing;

    public RoutingWrapperFactory(final Routing routing) {
        this.routing = Assert.requireNonNull(routing, "routing");
    }

    @Override
    public Parent createProjection(final Projector projector, final ControllerProxy controllerProxy, final Route projectable) {
        final StackPane pane = new StackPane();
        routing.addRoutingLayer(projectable, pane, controllerProxy);
        return pane;
    }
}
