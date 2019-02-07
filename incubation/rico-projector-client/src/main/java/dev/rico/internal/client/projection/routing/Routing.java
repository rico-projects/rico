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

import dev.rico.client.remoting.ControllerProxy;
import dev.rico.internal.client.projection.projection.Projector;
import dev.rico.internal.core.Assert;
import dev.rico.internal.projection.routing.Route;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Routing {

    private final Projector projector;

    public Routing(final Projector projector) {
        this.projector = Assert.requireNonNull(projector, "projector");
    }

    public void addRoutingLayer(String routingControllerName, final ControllerProxy currentProxy) {
                
    }

    public Parent createLoadingScreen(final Route route) {
        return new Label("LOADING...");
    }

    private void handleRouting(final Route route, final Consumer<Parent> handler, final ControllerProxy currentProxy) {
        Assert.requireNonNull(handler, "handler");
        Assert.requireNonNull(route, "route");

        final String controllerName = route.getLocation();
        Assert.requireNonNull(controllerName, "controllerName");

        final Parent loadingScreen = createLoadingScreen(route);
        handler.accept(loadingScreen);

        Optional.ofNullable(currentProxy).map(p -> p.destroy()).orElse(CompletableFuture.completedFuture(null)).thenAccept((v) -> {
            final CompletableFuture<Parent> future = projector.create(controllerName);
            future.whenComplete((parent, exception) -> {
                if(exception != null) {
                    throw new RuntimeException("Error in routing", exception);
                }
                Assert.requireNonNull(parent, "parent");
                handler.accept(parent);
            });
        });
    }

}
