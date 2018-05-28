/*
 * Copyright 2018 Karakun AG.
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
package dev.rico.internal.client.projection.action;

import dev.rico.internal.projection.base.Action;
import dev.rico.internal.client.projection.projection.Projector;
import dev.rico.remoting.ObservableList;
import dev.rico.client.remoting.ControllerProxy;
import javafx.scene.layout.FlowPane;

public class SimpleActionBar extends FlowPane {

    private final ObservableList<Action> actions;

    private final ControllerProxy controllerProxy;

    private final Projector projector;

    public SimpleActionBar(ControllerProxy controllerProxy, ObservableList<Action> actions, Projector projector) {
        this.actions = actions;
        this.controllerProxy = controllerProxy;
        this.projector = projector;
        getStyleClass().add("action-bar");
        this.actions.onChanged( e -> updateActions());
        updateActions();
    }

    protected void updateActions() {
        getChildren().clear();
        for (Action action : actions) {
            getChildren().add(projector.create(action, controllerProxy));
        }
    }

}
