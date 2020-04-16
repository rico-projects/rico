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
package dev.rico.internal.client.projection.chat;

import dev.rico.core.functional.Binding;
import dev.rico.internal.projection.chat.ChatMessage;
import dev.rico.remoting.client.javafx.FXBinder;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;

import java.util.Optional;

public class ChatCell<T extends ChatMessage> extends ListCell<T> {

    private ChatBubble bubble;

    private Binding sendByMeBinding;

    private Binding messageBinding;

    public ChatCell() {
        bubble = new ChatBubble();
        setGraphic(bubble);
        setText("");
        getStyleClass().add("chat-cell");

        listViewProperty().addListener(e -> {
            bubble.minWidthProperty().unbind();
            if (listViewProperty().get() != null) {
                bubble.minWidthProperty().bind(listViewProperty().get().widthProperty().subtract(32));
                bubble.prefWidthProperty().bind(listViewProperty().get().widthProperty().subtract(32));
                bubble.maxWidthProperty().bind(listViewProperty().get().widthProperty().subtract(32));
            }
        });

    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        Optional.ofNullable(messageBinding).ifPresent(b -> b.unbind());
        Optional.ofNullable(sendByMeBinding).ifPresent(b -> b.unbind());

        if (item == null || empty) {
            bubble.setVisible(false);
        } else {
            bubble.setVisible(true);
            messageBinding = FXBinder.bind(bubble.messageProperty()).to(item.messageProperty());
            sendByMeBinding = FXBinder.bind(bubble.sendByMeProperty()).to(item.sendByMeProperty());
        }
    }
}
