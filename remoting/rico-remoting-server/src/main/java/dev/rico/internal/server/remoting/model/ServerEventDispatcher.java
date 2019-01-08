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
package dev.rico.internal.server.remoting.model;

import dev.rico.internal.remoting.EventDispatcherImpl;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerEventDispatcher extends EventDispatcherImpl {

    public ServerEventDispatcher(ModelStore modelStore) {
        super(modelStore);
    }

    @Override
    public String getLocalSystemIdentifier() {
        return LegacyConstants.SOURCE_SYSTEM_SERVER;
    }
}
