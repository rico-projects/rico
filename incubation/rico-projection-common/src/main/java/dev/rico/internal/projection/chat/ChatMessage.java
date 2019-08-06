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
package dev.rico.internal.projection.chat;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

import java.util.Date;

@RemotingBean
public interface ChatMessage {

    Property<Boolean> sendByMeProperty();

    Property<String> messageProperty();

    Property<String> senderNameProperty();

    Property<String> senderAvatarUrlProperty();

    Property<Date> sendTimeProperty();

    default boolean isSendByMe() {
        return sendByMeProperty().get();
    }
}
