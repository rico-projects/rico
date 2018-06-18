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
package dev.rico.internal.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.repo.BeanRepository;
import dev.rico.internal.remoting.repo.ClassInfo;
import dev.rico.internal.remoting.repo.ClassRepository;
import dev.rico.internal.remoting.repo.PropertyInfo;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import java.util.UUID;

import static org.apiguardian.api.API.Status.INTERNAL;


@API(since = "0.x", status = INTERNAL)
public class BeanBuilder {

    private final ClassRepository classRepository;

    private final BeanRepository beanRepository;

    public BeanBuilder(final ClassRepository classRepository, final BeanRepository beanRepository) {
        this.classRepository = Assert.requireNonNull(classRepository, "classRepository");
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
    }

    public <T> T createInstanceForClass(final Class<T> beanClass, final String id, final UpdateSource source) {
        Assert.requireNonNull(beanClass, "beanClass");
        final ClassInfo classInfo = classRepository.getOrCreateClassInfo(beanClass);
        try {
            final T bean = beanClass.newInstance();

            setupProperties(classInfo, bean, id);
            setupObservableLists(classInfo, bean, id);

            beanRepository.registerBean(id, bean, source);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Cannot createList bean of type " + beanClass, e);
        }
    }

    private void setupProperties(final ClassInfo classInfo, final Object bean, final String beanId) {
        Assert.requireNonNull(classInfo, "classInfo");
        classInfo.forEachProperty(propertyInfo -> {
            try {
                Assert.requireNonNull(propertyInfo, "propertyInfo");
                final Property property = createProperty(beanId, propertyInfo);
                propertyInfo.setPriviliged(bean, property);
            } catch (Exception e) {
                throw new RuntimeException("Can not createList property " + propertyInfo.getAttributeName(), e);
            }
        });
    }

    private void setupObservableLists(final ClassInfo classInfo, final Object bean, final String beanId) {
        Assert.requireNonNull(classInfo, "classInfo");
        classInfo.forEachObservableList(observableListInfo -> {
            try {
                Assert.requireNonNull(observableListInfo, "observableListInfo");
                final ObservableList observableList = createList(beanId, observableListInfo);
                observableListInfo.setPriviliged(bean, observableList);
            } catch (Exception e) {
                throw new RuntimeException("Can not createList observable list " + observableListInfo.getAttributeName(), e);
            }
        });
    }

    protected ObservableArrayList createList(final String beanId, final PropertyInfo observableListInfo) {
        ObservableArrayList list = new ObservableArrayList();
        //TODO: Define Listener to create & send sync commands
        return list;
    }

    protected PropertyImpl createProperty(final String beanId, final PropertyInfo propertyInfo) {
        PropertyImpl property = new PropertyImpl<>();
        //TODO: Define Listener to create & send sync commands
        return property;
    }

}
