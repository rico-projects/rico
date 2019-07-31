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
package dev.rico.internal.server.remoting.legacy;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.PresentationModelBuilder;
import dev.rico.internal.remoting.PresentationModelBuilderFactory;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.ServerPresentationModel;
import dev.rico.internal.server.remoting.legacy.ServerPresentationModelBuilder;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@Deprecated
public class ServerPresentationModelBuilderFactory implements PresentationModelBuilderFactory<ServerPresentationModel> {

    private final ServerModelStore serverModelStore;

    public ServerPresentationModelBuilderFactory(ServerModelStore serverModelStore) {
        Assert.requireNonNull(serverModelStore, "modelStore");
        this.serverModelStore = serverModelStore;
    }

    @Override
    public PresentationModelBuilder<ServerPresentationModel> createBuilder() {
        return new ServerPresentationModelBuilder(serverModelStore);
    }
}
