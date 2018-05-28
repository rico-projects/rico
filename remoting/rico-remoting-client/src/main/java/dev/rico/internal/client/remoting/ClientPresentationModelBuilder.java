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
package dev.rico.internal.client.remoting;

import dev.rico.internal.remoting.AbstractPresentationModelBuilder;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.client.remoting.legacy.ClientAttribute;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientPresentationModelBuilder extends AbstractPresentationModelBuilder<ClientPresentationModel> {

    private final List<ClientAttribute> attributes = new ArrayList<>();
    private final ClientModelStore modelStore;

    public ClientPresentationModelBuilder(final ClientModelStore modelStore) {
        this.modelStore = Assert.requireNonNull(modelStore, "modelStore");
        attributes.add(new ClientAttribute(LegacyConstants.SOURCE_SYSTEM, LegacyConstants.SOURCE_SYSTEM_CLIENT));
    }

    @Override
    public ClientPresentationModelBuilder withAttribute(final String name) {
        attributes.add(new ClientAttribute(name, null));
        return this;
    }

    @Override
    public ClientPresentationModelBuilder withAttribute(final String name, final Object value) {
        attributes.add(new ClientAttribute(name, value));
        return this;
    }

    @Override
    public ClientPresentationModelBuilder withAttribute(final String name, Object value, final String qualifier) {
        attributes.add(new ClientAttribute(name, value, qualifier));
        return this;
    }

    @Override
    public ClientPresentationModel create() {
        return modelStore.createModel(getId(), getType(), attributes.toArray(new ClientAttribute[attributes.size()]));
    }

}
