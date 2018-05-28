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
package dev.rico.internal.server.data.event;

import dev.rico.internal.server.data.event.PersistenceContextImpl;
import dev.rico.server.data.AbstractEntity;

/**
 * Listener for JPA events that can be registered as a lister to the {@link PersistenceContextImpl}
 * TODO: Refactore to Lambda support
 */
public interface PersistenceListener {

    /**
     * Will be fired if an entity was persited
     * @param entity the persisted entity
     */
    void onEntityPersisted(AbstractEntity entity);

    /**
     * Will be fired if an entity was removed
     * @param entity the removed entity
     */
    void onEntityRemoved(AbstractEntity entity);

    /**
     * Will be fired if an entity was updated
     * @param entity the updated entity
     */
    void onEntityUpdated(AbstractEntity entity);

}
