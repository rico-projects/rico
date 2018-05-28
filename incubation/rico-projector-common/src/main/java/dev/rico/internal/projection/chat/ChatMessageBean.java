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
package dev.rico.internal.projection.chat;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

import java.util.Date;

@RemotingBean
public class ChatMessageBean<T> implements ChatMessage {

    private Property<Boolean> sendByMe;

    private Property<String> senderName;

    private Property<String> senderAvatarUrl;

    private Property<Date> sendTime;

    private Property<String> message;

    @Override
    public Property<Boolean> sendByMeProperty() {
        return sendByMe;
    }

    @Override
    public Property<String> messageProperty() {
        return message;
    }

    @Override
    public Property<String> senderNameProperty() {
        return senderName;
    }

    @Override
    public Property<String> senderAvatarUrlProperty() {
        return senderAvatarUrl;
    }

    @Override
    public Property<Date> sendTimeProperty() {
        return sendTime;
    }
}
