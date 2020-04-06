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
package dev.rico.server.remoting;

import dev.rico.server.remoting.binding.PropertyBinder;
import dev.rico.server.remoting.event.RemotingEventBus;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Facade to get access to all instances of a remoting context. Each {@link ClientSession} that uses the remoting layer will contain exactly one remoting context.
 *
 * This is a util interface that normally is not needed for application developers since all needed parts of the context can be injected directly in remoting controller classes or other managed beans.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = EXPERIMENTAL)
public interface RemotingContext {

    /**
     * Return the id of the context.
     * @return the id
     */
    String getId();

    /**
     * Return the executor for the context
     * @return the executor
     */
    ClientSessionExecutor createSessionExecutor();

    /**
     * Return the binder for the context
     * @return the binder
     */
    PropertyBinder getBinder();

    /**
     * Returns the bean manager for the context
     * @return the bean manager
     */
    BeanManager getBeanManager();

    /**
     * Returns the event bus for the context
     * @return the event bus
     */
    RemotingEventBus getEventBus();

    /**
     * Returns the client session for the context
     * @return the client session
     */
    ClientSession getClientSession();
}
