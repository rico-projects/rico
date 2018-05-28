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
package dev.rico.server.data;


import dev.rico.internal.server.data.event.PersistenceContextImpl;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.Version;

@MappedSuperclass
public class AbstractEntity implements DataWithId<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    @Override
    public final Long getId() {
        return id;
    }

    @Override
    public final void setId(final Long id) {
        this.id = id;
    }

    /**
     * Returns the version
     *
     * @return the version
     */
    public final Long getVersion() {
        return version;
    }

    /**
     * Set the version
     *
     * @param version the version
     */
    public final void setVersion(Long version) {
        this.version = version;
    }

    @PostPersist
    public void onPersist() {
        PersistenceContextImpl.getInstance().firePersisted(this);
    }

    @PostRemove
    public void onRemove() {
        PersistenceContextImpl.getInstance().fireRemoved(this);
    }

    @PostUpdate
    public void onUpdate() {
        PersistenceContextImpl.getInstance().fireUpdated(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
