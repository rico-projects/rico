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
package dev.rico.server.data;

import dev.rico.internal.core.Assert;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public class EntityService<E extends AbstractEntity> implements DataService<Long, E> {

    private final Class<E> entityType;

    private final EntityManager entityManager;

    public EntityService(final Class<E> entityType, final EntityManager entityManager) {
        this.entityType = Assert.requireNonNull(entityType, "entityType");
        this.entityManager = Assert.requireNonNull(entityManager, "entityManager");
    }

    @Override
    public E createNewInstance() {
        try {
            return entityType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("TODO");
        }
    }

    @Override
    public List<E> findAll() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<E> cq = cb.createQuery(entityType);
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public E findById(Long id) {
        Assert.requireNonNull(id, "id");
        return entityManager.find(entityType, id);
    }

    @Override
    public Optional<E> byId(final Long id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public Class<E> getDataClass() {
        return entityType;
    }

    @Override
    public E reset(final E entity) {
        Assert.requireNonNull(entity , "entity");
        final Long id = entity.getId();

        if(id != null) {
            entityManager.detach(entity);
            return findById(id);
        } else {
            return createNewInstance();
        }
    }

    @Override
    public E save(final E entity) {
        Assert.requireNonNull(entity , "entity");
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public void delete(final E entity) {
        Assert.requireNonNull(entity , "entity");
        entityManager.remove(entity);
    }

}
