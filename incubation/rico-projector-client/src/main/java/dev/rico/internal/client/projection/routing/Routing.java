/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.client.projection.routing;

import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerFactory;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.internal.client.projection.projection.Projector;
import dev.rico.internal.client.projection.projection.ViewFactory;
import dev.rico.internal.core.Assert;
import dev.rico.internal.projection.base.View;
import dev.rico.internal.projection.routing.Route;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Routing {

    private final Projector projector;

    private final ViewFactory viewFactory;

    private Map<Route, ControllerProxy> controllerMapping;

    public Routing(final ViewFactory viewFactory, final Projector projector) {
        this.projector = Assert.requireNonNull(projector, "projector");
        this.viewFactory = Assert.requireNonNull(viewFactory, "viewFactory");
        controllerMapping = new WeakHashMap<>();
    }

    public void addRoutingLayer(final Route route, final Pane viewWrapper, final ControllerFactory controllerFactory) {
        Assert.requireNonNull(route, "route");
        Assert.requireNonNull(viewWrapper, "viewWrapper");

        final Consumer<Parent> handler = view -> {
            viewWrapper.getChildren().clear();
            viewWrapper.getChildren().add(view);
        };
        route.locationProperty().onChanged(e -> handleRouting(route, handler, controllerFactory));
        if (route.getLocation() != null) {
            handleRouting(route, handler, controllerFactory);
        }
    }

    private void handleRouting(final Route route, final Consumer<Parent> handler, final ControllerFactory controllerFactory) {
        Assert.requireNonNull(handler, "handler");
        Assert.requireNonNull(route, "route");
        Assert.requireNonNull(controllerFactory, "controllerFactory");

        final String controllerName = route.getLocation();
        Assert.requireNonNull(controllerName, "controllerName");

        final Parent loadingScreen = createLoadingScreen(route);
        handler.accept(loadingScreen);

        final CompletableFuture<Void> destroyFuture = Optional.ofNullable(controllerMapping.get(route))
                .map(p -> p.destroy())
                .orElse(CompletableFuture.completedFuture(null));

        destroyFuture.thenAccept((v) -> {
            final ClientContext clientContext;
            controllerFactory.createController(controllerName).thenAccept(controllerProxy -> {
                final Parent newView = viewFactory.createProjection(projector, controllerProxy, (View) controllerProxy.getModel());
                handler.accept(newView);
                controllerMapping.put(route, controllerProxy);
            }).exceptionally(exception -> {
                throw new RuntimeException("Error in Routing", exception);
            });
        }).exceptionally(exception -> {
            throw new RuntimeException("Error in Routing", exception);
        });
    }

    protected Parent createLoadingScreen(final Route route) {
        return new Label("LOADING...");
    }

}
