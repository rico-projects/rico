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
package dev.rico.internal.client.projection.projection;

import dev.rico.internal.projection.action.ClientAction;
import dev.rico.internal.projection.action.ServerAction;
import dev.rico.internal.projection.base.Projectable;
import dev.rico.internal.client.projection.base.ClientActionSupport;
import dev.rico.internal.projection.form.Form;
import dev.rico.internal.projection.graph.GraphDataBean;
import dev.rico.internal.projection.lazy.concrete.MediaLazyListBean;
import dev.rico.internal.projection.base.View;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Projector {

    private final Map<Class, ProjectionFactory> projectionMapping = new HashMap<>();

    private final ClientContext clientContext;

    private final ClientActionSupport clientActionSupport;

    public Projector(final ClientContext clientContext, final ClientActionSupport clientActionSupport) {
        this.clientContext = clientContext;
        this.clientActionSupport = clientActionSupport;

        register(Form.class, new FormFactory());
        register(ClientAction.class, new ClientActionFactory());
        register(ServerAction.class, new ServerActionFactory());
        register(GraphDataBean.class, new GraphFactory());
        register(View.class, new ViewFactory());
        register(MediaLazyListBean.class, new LazyListFactory());
    }

    public <T> void register(final Class<T> projectableClass, final ProjectionFactory<T> factory) {
        projectionMapping.put(projectableClass, factory);
    }

    public Parent create(final Object projectable, final ControllerProxy controllerProxy) {
        ProjectionFactory factory = projectionMapping.get(projectable.getClass());
        if(factory != null) {
            return factory.createProjection(this, clientContext, controllerProxy, projectable);
        }
        if(factory == null) {
            for(Class cls : projectionMapping.keySet()) {
                if(cls.isAssignableFrom(projectable.getClass())) {
                    return projectionMapping.get(cls).createProjection(this, clientContext, controllerProxy, projectable);
                }
            }
        }
        throw new RuntimeException("TODO");
    }

    public CompletableFuture<Void> openInWindow(final String controllerName) {
        return openInWindow(controllerName, null);
    }

    public CompletableFuture<Void> openInWindow(final String controllerName, final Consumer<Scene> sceneInitializer) {
        return clientContext.createController(controllerName).handle((c, e) -> {
            if(e != null) {
                //TODO
            }
            Platform.runLater(() -> {
                Parent rootPane = create((Projectable) c.getModel(), c);
                Stage stage = new Stage();
                Scene scene = new Scene(rootPane);
                if(sceneInitializer != null) {
                    sceneInitializer.accept(scene);
                }
                stage.setScene(scene);
                stage.setWidth(800);
                stage.setHeight(600);
                stage.setOnHidden(ev -> c.destroy());
                stage.show();
            });
            return null;
        });
    }

    public CompletableFuture<Parent> create(final String controllerName) {
        return clientContext.createController(controllerName).handle((c, e) -> {
            if(e != null) {
                //TODO
            }
           return create((Projectable) c.getModel(), c);
        });
    }

    public ClientContext getClientContext() {
        return clientContext;
    }

    public ClientActionSupport getClientActionSupport() {
        return clientActionSupport;
    }
}
