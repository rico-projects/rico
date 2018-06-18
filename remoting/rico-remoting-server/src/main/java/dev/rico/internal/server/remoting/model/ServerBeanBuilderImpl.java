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
package dev.rico.internal.server.remoting.model;

import dev.rico.internal.remoting.*;
import dev.rico.internal.remoting.repo.BeanRepository;
import dev.rico.internal.remoting.repo.ClassRepository;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ListChangeListener;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.ValueChangeEvent;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.repo.PropertyInfo;
import dev.rico.remoting.Property;
import dev.rico.internal.core.Assert;
import dev.rico.internal.server.remoting.gc.GarbageCollector;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerBeanBuilderImpl extends AbstractBeanBuilder {

    private final GarbageCollector garbageCollector;

    public ServerBeanBuilderImpl(final ClassRepository classRepository, final BeanRepository beanRepository, final GarbageCollector garbageCollector) {
        super(classRepository, beanRepository);
        this.garbageCollector = Assert.requireNonNull(garbageCollector, "garbageCollector");
    }

    public <T> T createRootModel(Class<T> beanClass) {
        T bean = super.create(beanClass);
        garbageCollector.onBeanCreated(bean, true);
        return bean;
    }

    @Override
    public <T> T create(Class<T> beanClass) {
        T bean = super.create(beanClass);
        garbageCollector.onBeanCreated(bean, false);
        return bean;
    }

    @Override
    protected String getIdPrefix() {
        return "SERVER";
    }

    @Override
    protected ObservableArrayList createList(String beanId, PropertyInfo observableListInfo) {
        final ObservableArrayList<?> list = super.createList(beanId, observableListInfo);
        list.onInternalChanged(e -> {
            e.getChanges().forEach(c -> {
                if(c.isAdded()) {
                    list.subList(c.getFrom(), c.getTo()).forEach(i -> garbageCollector.onAddedToList(list, i));
                }
                if(c.isRemoved()) {
                    c.getRemovedElements().forEach(i -> garbageCollector.onRemovedFromList(list, i));
                }
                if(c.isReplaced()) {
                    //??? TODO
                }
            });
        });
        return list;
    }

    @Override
    protected PropertyImpl createProperty(String beanId, PropertyInfo propertyInfo) {
        final PropertyImpl property = super.createProperty(beanId, propertyInfo);
        property.onInternalChanged(e -> garbageCollector.onPropertyValueChanged(e.getSource(), e.getOldValue(), e.getNewValue()));
        return property;
    }
}

