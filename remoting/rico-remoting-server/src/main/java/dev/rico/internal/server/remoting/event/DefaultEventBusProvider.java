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
package dev.rico.internal.server.remoting.event;

import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.server.remoting.event.RemotingEventBus;
import dev.rico.server.remoting.event.spi.EventBusProvider;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DefaultEventBusProvider implements EventBusProvider {

    public static final String DEFAULT_EVENTBUS_NAME = "default";

    @Override
    public String getType() {
        return DEFAULT_EVENTBUS_NAME;
    }

    @Override
    public RemotingEventBus create(RemotingConfiguration configuration) {
        return new DefaultRemotingEventBus();
    }
}
