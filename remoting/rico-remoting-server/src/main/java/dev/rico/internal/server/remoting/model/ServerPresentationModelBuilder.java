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

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.AbstractPresentationModelBuilder;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.server.remoting.legacy.DTO;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.ServerPresentationModel;
import dev.rico.internal.server.remoting.legacy.Slot;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerPresentationModelBuilder extends AbstractPresentationModelBuilder<ServerPresentationModel> {

    private final List<Slot> slots = new ArrayList<>();
    private final ServerModelStore serverModelStore;

    public ServerPresentationModelBuilder(ServerModelStore serverModelStore) {
        Assert.requireNonNull(serverModelStore, "modelStore");
        this.serverModelStore = serverModelStore;
        this.slots.add(new Slot(LegacyConstants.SOURCE_SYSTEM, LegacyConstants.SOURCE_SYSTEM_SERVER));
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name) {
        slots.add(new Slot(name, null));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value) {
        slots.add(new Slot(name, value));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value, String qualifier) {
        slots.add(new Slot(name, value, qualifier));
        return this;
    }

    @Override
    public ServerPresentationModel create() {
        return serverModelStore.presentationModel(getId(), getType(), new DTO(slots));
    }

}
