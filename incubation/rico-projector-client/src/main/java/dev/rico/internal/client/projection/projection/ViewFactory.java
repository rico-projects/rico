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
package dev.rico.internal.client.projection.projection;

import dev.rico.internal.projection.base.WithLayoutMetadata;
import dev.rico.internal.projection.metadata.MetadataUtilities;
import dev.rico.internal.projection.base.View;
import dev.rico.internal.projection.view.ViewMetadata;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViewFactory implements ProjectionFactory<View> {

    @Override
    public Parent createProjection(Projector projector, ClientContext clientContext, ControllerProxy controllerProxy, View projectable) {
        final StackPane mainPane = new StackPane();

        MetadataUtilities.addListenerToMetadata(projectable, () -> updateContentWrapper(mainPane, projectable));
        Pane internalPane = updateContentWrapper(mainPane, projectable);

        projectable.getContent().onChanged(e -> recreateContent(internalPane, projectable, projector, controllerProxy));
        recreateContent(internalPane, projectable, projector, controllerProxy);
        return mainPane;
    }

    private Pane updateContentWrapper(StackPane mainPane, View projectable) {
        List<Node> children = new ArrayList<>();
        if (!mainPane.getChildren().isEmpty()) {
            Pane internalPane = (Pane) mainPane.getChildren().get(0);
            children.addAll(internalPane.getChildren());
            mainPane.getChildren().clear();
        }
        Pane internalPane = Optional.of(projectable).map(p -> {
            if(ViewMetadata.isOrientationVertical(p)) {
                return new HBox();
            } else {
                return new VBox();
            }
        }).orElse(new HBox());
        internalPane.getChildren().addAll(children);
        mainPane.getChildren().add(internalPane);
        return internalPane;
    }

    private void recreateContent(Pane pane, View projectable, Projector projector, ControllerProxy controllerProxy) {
        pane.getChildren().clear();
        projectable.getContent().forEach(c -> {
            Node content = projector.create(c, controllerProxy);
            if (c instanceof WithLayoutMetadata) {
                WithLayoutMetadata metadata = (WithLayoutMetadata) c;
                MetadataUtilities.addListenerToMetadata(metadata, () -> updateContentByMetadata(metadata, content));
                updateContentByMetadata(metadata, content);
            }
            pane.getChildren().add(content);
        });
    }

    private void updateContentByMetadata(WithLayoutMetadata bean, Node content) {
        Priority priority = MetadataUtilities.getMetadata(ViewMetadata.JAVAFX_LAYOUT_CONTENT_GROW, bean).
                map(m -> Priority.valueOf(Optional.ofNullable(m.getValue()).orElse("").toString())).
                orElse(Priority.SOMETIMES);
        HBox.setHgrow(content, priority);
        VBox.setVgrow(content, priority);

        double margin = MetadataUtilities.getMetadata(ViewMetadata.JAVAFX_LAYOUT_MARGIN, bean).
                map(m -> Double.parseDouble(Optional.ofNullable(m.getValue()).orElse("0.0").toString())).
                orElse(0.0);
        HBox.setMargin(content, new Insets(margin));
        VBox.setMargin(content, new Insets(margin));
    }


}
