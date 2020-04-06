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
package dev.rico.internal.projection.notifications;

import dev.rico.internal.projection.message.MessageType;

import java.io.Serializable;

public class NotificationData implements Serializable{

    private String title;

    private String text;

    private MessageType messageType;

    public NotificationData(String title, String text, MessageType messageType) {
        this.title = title;
        this.text = text;
        this.messageType = messageType;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
