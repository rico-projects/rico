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
import dev.rico.client.remoting.FXBinder;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractActionButton<T extends Action> extends Button {

    private final T action;

    private final BooleanProperty blockUi = new SimpleBooleanProperty(false);

    private final BooleanProperty blockOnAction = new SimpleBooleanProperty(false);

    private final BooleanProperty actionRunning = new SimpleBooleanProperty(false);

    private final BooleanProperty disabled = new SimpleBooleanProperty(false);

    public AbstractActionButton(T action) {
        this.action = action;

        FXBinder.bind(textProperty()).to(action.titleProperty());

        FXBinder.bind(blockUi).to(action.blockUiProperty());
        FXBinder.bind(blockOnAction).to(action.blockOnActionProperty());
        FXBinder.bind(disabled).to(action.disabledProperty());
        disableProperty().bind(disabled.or(blockOnAction.and(actionRunning)));

        action.descriptionProperty().onChanged(event -> {
            if(action.getDescription() != null) {
                setTooltip(new Tooltip(action.getDescription()));
            } else {
                setTooltip(null);
            }
        });
        if(action.getDescription() != null) {
            setTooltip(new Tooltip(action.getDescription()));
        }

        setOnAction(e -> {
            actionRunning.setValue(true);
            callAction().whenComplete((v, ex) -> {
                Platform.runLater(() -> {
                    actionRunning.setValue(false);
                });
            });
        });
    }

    protected abstract CompletableFuture<Void> callAction();

    protected T getAction() {
        return action;
    }

}
