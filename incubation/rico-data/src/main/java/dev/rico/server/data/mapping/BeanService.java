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
package dev.rico.server.data.mapping;

import dev.rico.internal.core.Assert;
import dev.rico.server.data.DataService;
import dev.rico.server.data.DataWithId;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanService<ID extends Serializable, B extends DataWithId<ID>, E extends DataWithId<ID>> implements DataService<ID, B> {

    private final DataService<ID, E> dataService;

    private final BeanMapper mapper;

    private final Class<B> beanClass;

    public BeanService(final DataService<ID, E> dataService, final BeanMapper mapper, final Class<B> beanClass) {
        this.dataService = Assert.requireNonNull(dataService, "dataService");
        this.mapper = Assert.requireNonNull(mapper, "mapper");
        this.beanClass = Assert.requireNonNull(beanClass, "beanClass");
    }

    @Override
    public B createNewInstance() {
        final E entity = dataService.createNewInstance();
        return mapper.toBean(entity, beanClass);
    }

    @Override
    public List<B> findAll() {
        final List<E> entities = dataService.findAll();
        return entities.stream()
                .map(e -> toBean(e))
                .collect(Collectors.toList());
    }

    @Override
    public B findById(final ID id) {
        return byId(id).orElse(null);
    }

    @Override
    public Optional<B> byId(final ID id) {
        Assert.requireNonNull(id, "id");
        return dataService.byId(id).map(e -> toBean(e));
    }

    @Override
    public Class<B> getDataClass() {
        return beanClass;
    }

    @Override
    public B reset(B bean) {
        Assert.requireNonNull(bean, "bean");
        final E entity = toEntity(bean);
        final E resetedEntity = dataService.reset(entity);
        return updateBean(bean, resetedEntity);
    }

    @Override
    public B save(final B bean) {
        Assert.requireNonNull(bean, "bean");
        final E entity = toEntity(bean);
        final E savedEntity = dataService.save(entity);
        return updateBean(bean, savedEntity);
    }

    @Override
    public void delete(final B bean) {
        Assert.requireNonNull(bean, "bean");
        final E entity = toEntity(bean);
        dataService.delete(entity);
    }

    private B toBean(final E entity) {
        return mapper.toBean(entity, getDataClass());
    }

    private E toEntity(final B bean) {
        return mapper.toEntity(bean, dataService.getDataClass());
    }

    private B updateBean(final B bean, final E entity) {
        return mapper.updateBean(entity, bean, getDataClass());
    }
}
