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
package dev.rico.internal.server.crud;

import dev.rico.internal.core.Assert;
import dev.rico.server.data.DataWithId;

import java.io.Serializable;

public class CrudEvent<ID extends Serializable, E extends DataWithId<ID>> implements Serializable {

    private Class<E> entityClass;

    private ID id;

    private CrudEventType eventType;

    public CrudEvent() {
    }

    public CrudEvent(final Class<E> entityClass, final ID id, final CrudEventType eventType) {
        this();
        setEntityClass(entityClass);
        setId(id);
        setEventType(eventType);
    }

    public CrudEventType getEventType() {
        return eventType;
    }

    public void setEventType(final CrudEventType eventType) {
        this.eventType = Assert.requireNonNull(eventType, "eventType");
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(final Class<E> entityClass) {
        this.entityClass = Assert.requireNonNull(entityClass, "entityClass");
    }

    public ID getId() {
        return id;
    }

    public void setId(final ID id) {
        this.id = Assert.requireNonNull(id, "id");
    }

    public boolean matches(final DataWithId entity) {
        Assert.requireNonNull(entity, "entity");
        if(!entity.getClass().equals(entityClass)) {
            return false;
        }
        final Serializable entityId = entity.getId();
        if(entityId == null || !id.equals(entityId)) {
            return false;
        }
        return true;
    }
}
