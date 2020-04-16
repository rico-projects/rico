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
package dev.rico.internal.remoting.server.legacy.action;

import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.server.legacy.DTO;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import dev.rico.internal.remoting.server.legacy.ServerModelStore;
import org.apiguardian.api.API;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Common superclass for all actions that need access to
 * the ServerModelStore, e.g. to work with the server model store.
 */
@API(since = "0.x", status = INTERNAL)
public abstract class AbstractServerAction implements ServerAction {

    private ServerModelStore serverModelStore;

    private List<Command> response;

    public void presentationModel(final String id, final String presentationModelType, final DTO dto) {
        ServerModelStore.presentationModelCommand(response, id, presentationModelType, dto);
    }

    public void changeValue(final ServerAttribute attribute, final String value) {
        ServerModelStore.changeValueCommand(response, attribute, value);
    }

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }

    @Deprecated
    public void setServerModelStore(final ServerModelStore serverModelStore) {
        this.serverModelStore = serverModelStore;
    }

    public List<Command> getResponse() {
        return response;
    }

    public void setResponse(final List<Command> response) {
        this.response = response;
    }

}
