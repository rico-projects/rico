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
package dev.rico.internal.server.data.mapping;

import dev.rico.internal.core.Assert;
import dev.rico.server.data.DataService;
import dev.rico.server.data.DataWithId;
import dev.rico.server.data.mapping.BeanMapper;
import dev.rico.server.remoting.BeanManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class BeanMapperImpl implements BeanMapper {

    private final WeakHashMap<Object, Map<Class<?>, Serializable>> beanToIdMapper = new WeakHashMap<>();

    private final Map<Class, Map<Class<?>, BeanConverter>> converters = new HashMap<>();

    private final Map<Class, DataService> services = new HashMap<>();

    private final BeanManager beanManager;

    public BeanMapperImpl(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public synchronized <ID extends Serializable, B, E extends DataWithId<ID>> void addConverter(final Class<B> beanClass, final Class<E> entityClass, BeanConverter<ID, B, E> converter) {
        Assert.requireNonNull(beanClass, "beanClass");
        Assert.requireNonNull(entityClass, "entityClass");
        Assert.requireNonNull(converter, "converter");
        final Map<Class<?>, BeanConverter> map = converters.computeIfAbsent(entityClass, e -> new HashMap<>());
        map.put(beanClass, converter);
    }

    private synchronized <ID extends Serializable, B, E extends DataWithId<ID>> BeanConverter<ID, B, E> getConverter(final Class<E> entityClass, final Class<B> beanClass) {
        return Optional.ofNullable(converters.get(entityClass))
                .map(m -> m.get(beanClass))
                .orElseThrow(() -> new ConversionException("Can not find converter for bean type " + beanClass + " and entity type " + entityClass));
    }

    private synchronized <ID extends Serializable, B, E extends DataWithId<ID>> ID getEntityIdForBean(final B bean, final Class<E> entityClass) {
        return (ID) Optional.ofNullable(beanToIdMapper.get(bean))
                .map(m -> m.get(entityClass))
                .orElse(null);
    }

    private synchronized <ID extends Serializable, B, E extends DataWithId<ID>> void addMapping(final B bean, final Class<E> entityClass, final ID id) {
        beanToIdMapper.computeIfAbsent(bean, b -> new HashMap<>())
                .put(entityClass, id);
    }

    @Override
    public <ID extends Serializable, B, E extends DataWithId<ID>> B updateBean(final E entity, final B bean, final Class<B> beanClass) {
        final Class<E> entityClass = (Class<E>) entity.getClass();
        final ID id = entity.getId();
        final BeanConverter<ID, B, E> converter = getConverter(entityClass, beanClass);
        addMapping(bean, entityClass, id);
        return converter.enrichtBeanByEntity(bean, entity);
    }

    @Override
    public <ID extends Serializable, B, E extends DataWithId<ID>> E toEntity(final B bean, final Class<E> entityClass) {
        final Class<B> beanClass = (Class<B>) bean.getClass();
        final DataService<ID, E> service = services.get(entityClass);
        final BeanConverter<ID, B, E> converter = getConverter(entityClass, beanClass);
        final E entity = Optional.ofNullable(getEntityIdForBean(bean, entityClass))
                .map(id -> service.findById(id))
                .orElse(service.createNewInstance());
        return converter.enrichtEntityByBean(entity, bean);
    }

    @Override
    public <ID extends Serializable, B, E extends DataWithId<ID>> B toBean(final E entity, final Class<B> beanClass) {
        throw new RuntimeException("Not yet implemented!");
    }
}
