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
package dev.rico.internal.client.remoting.legacy;

import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.client.remoting.legacy.communication.OnFinishedHandler;
import dev.rico.internal.remoting.legacy.communication.EmptyCommand;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * The legacy facade on the client side.
 */
@Deprecated
@API(since = "0.x", status = DEPRECATED)
public class ClientDolphin {

    private ClientModelStore clientModelStore;

    private AbstractClientConnector clientConnector;

    public AbstractClientConnector getClientConnector() {
        return clientConnector;
    }

    /**
     * both java- and groovy-friendly convenience method to withContent an empty command, which will have no
     * presentation models nor data in the callback
     */
    @Deprecated
    public void sync(final Runnable runnable) {
        clientConnector.send(new EmptyCommand(), new OnFinishedHandler() {
            public void onFinished() {
                runnable.run();
            }

        });
    }

    /**
     * @deprecated Model store should be final and defined by constructor
     * @param clientModelStore
     */
    @Deprecated
    public void setClientModelStore(final ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    /**
     * @deprecated client connector should be final and defined by constructor
     * @param clientConnector
     */
    @Deprecated
    public void setClientConnector(final AbstractClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

}
