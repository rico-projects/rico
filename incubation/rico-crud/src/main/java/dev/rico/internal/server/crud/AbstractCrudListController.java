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
package dev.rico.internal.server.crud;

import dev.rico.internal.core.Assert;
import dev.rico.server.data.DataService;
import dev.rico.server.data.DataWithId;
import dev.rico.internal.server.data.mapping.BeanConverter;
import dev.rico.internal.server.data.mapping.BeanMapperImpl;
import dev.rico.core.functional.Subscription;
import dev.rico.server.remoting.BeanManager;
import dev.rico.remoting.ObservableList;
import dev.rico.server.remoting.event.RemotingEventBus;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCrudListController<ID extends Serializable, B, E extends DataWithId<ID>> {

    private final DataService<ID, E> dataService;

    private final BeanMapperImpl mapper;

    private final Class<B> modelClass;

    private final Class<E> entityClass;

    private final RemotingEventBus eventBus;

    private List<E> entities;

    private final Subscription eventBusSubscription;
    
    protected AbstractCrudListController(final Class<B> modelClass, final Class<E> entityClass, final BeanManager manager, final DataService<ID, E> dataService, final BeanConverter<ID, B, E> converter, final RemotingEventBus eventBus) {
        this.modelClass = Assert.requireNonNull(modelClass, "modelClass");
        this.entityClass = Assert.requireNonNull(entityClass, "entityClass");
        this.dataService = Assert.requireNonNull(dataService, "dataService");
        this.eventBus = Assert.requireNonNull(eventBus, "eventBus");
        this.entities = Collections.emptyList();
        Assert.requireNonNull(manager, "manager");
        Assert.requireNonNull(converter, "converter");
        this.mapper = new BeanMapperImpl(manager);
        mapper.addConverter(modelClass, entityClass, converter);
        eventBusSubscription = eventBus.subscribe(CrudConstants.CRUD_EVENT_TOPIC, e -> onCrudEvent(e.getData()));
    }

    @PostConstruct
    protected void onDestroy() {
        eventBusSubscription.unsubscribe();
    }

    private void onCrudEvent(final CrudEvent event) {
        //TODO: Would be perfect to get meta information for example the user.
        Assert.requireNonNull(event, "event");
        if(event.getEntityClass().equals(entityClass)) {
            final ID id = (ID) event.getId();
            entities.stream()
                    .filter(e -> id.equals(e.getId()))
                    .findFirst()
                    .ifPresent(e -> onEntityDirty(e, event.getEventType()));
        }
    }

    protected void onEntityDirty(final E entity, final CrudEventType type) {
        
    }

    protected void showAll() {
        setEntities(dataService.findAll());
    }

    protected void addNew() {
        final List<E> newList = new ArrayList<>(getEntities());
        newList.add(dataService.createNewInstance());
        setEntities(newList);
    }

    protected void deleteSelected() {
        delete(getSelectedEntities());
    }

    protected void resetSelected() {
        reset(getSelectedEntities());
    }

    protected void delete(final List<E> entities) {
        entities.stream()
                .filter(e -> e.getId() != null)
                .forEach(e -> dataService.delete(e));
        final List<E> newList = new ArrayList<>(getEntities());
        newList.removeAll(entities);
        setEntities(newList);
    }

    protected void reset(final List<E> entities) {
        List<E> resetedEntities = entities.stream()
                .map(e -> dataService.reset(e))
                .collect(Collectors.toList());
        final List<E> newList = new ArrayList<>(getEntities());
        newList.replaceAll(e -> {
            final int index = entities.indexOf(e);
            if(index >= 0) {
                return resetedEntities.get(index);
            }
            return e;
        });
        setEntities(newList);
    }

    protected void resetAll() {
        List<E> newEntities = entities.stream().map(e -> dataService.reset(e)).collect(Collectors.toList());
        setEntities(newEntities);
    }

    protected void deleteAll() {
        entities.forEach(e -> dataService.delete(e));
        setEntities(Collections.emptyList());
    }

    public List<E> getEntities() {
        return entities;
    }

    public void setEntities(final List<E> entities) {
        this.entities = Collections.unmodifiableList(entities);
        updateModel();
    }

    protected void updateModel() {
        //TODO
    }

    protected abstract ObservableList<B> getModel();

    protected abstract List<B> getSelected();

    protected DataService<ID, E> getDataService() {
        return dataService;
    }

    private List<E> getSelectedEntities() {
        return getSelected().stream()
                .map(b -> mapper.toEntity(b, entityClass))
                .collect(Collectors.toList());
    }

    private String getEntityType() {
        return entityClass.getSimpleName();
    }
}
