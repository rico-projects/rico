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
package dev.rico.internal.remoting.repo;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.converter.Converter;
import org.apiguardian.api.API;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClassInfo {

    private final Class<?> beanClass;

    private final String id;

    private final Map<String, PropertyInfo> propertyInfoMap;

    private final Map<String, PropertyInfo> observableListInfoMap;

    private static final AtomicLong idCounter = new AtomicLong();

    public ClassInfo(final String id, final Class<?> beanClass, final Collection<PropertyInfo> propertyInfos, final Collection<PropertyInfo> observableListInfos) {
        this.beanClass = beanClass;
        this.id = id;

        final Map<String, PropertyInfo> localPropertyInfoMap = new HashMap<>();
        for (final PropertyInfo propertyInfo : propertyInfos) {
            localPropertyInfoMap.put(propertyInfo.getAttributeName(), propertyInfo);
        }
        propertyInfoMap = Collections.unmodifiableMap(localPropertyInfoMap);

        final Map<String, PropertyInfo> localObservableListInfoMap = new HashMap<>();
        for (final PropertyInfo observableListInfo : observableListInfos) {
            localObservableListInfoMap.put(observableListInfo.getAttributeName(), observableListInfo);
        }
        observableListInfoMap = Collections.unmodifiableMap(localObservableListInfoMap);
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getId() {
        return id;
    }

    public PropertyInfo getPropertyInfo(final String attributeName) {
        return propertyInfoMap.get(attributeName);
    }

    public PropertyInfo getObservableListInfo(final String attributeName) {
        return observableListInfoMap.get(attributeName);
    }

    public void forEachProperty(final Consumer<PropertyInfo> propertyInfoConsumer) {
        for (final PropertyInfo propertyInfo : propertyInfoMap.values()) {
            propertyInfoConsumer.accept(propertyInfo);
        }
    }

    public void forEachObservableList(final Consumer<PropertyInfo> propertyInfoConsumer) {
        for (final PropertyInfo observableListInfo : observableListInfoMap.values()) {
            propertyInfoConsumer.accept(observableListInfo);
        }
    }

    public static ClassInfo create(final String id, final Class<?> beanClass, final Converters converters) {
        Assert.requireNonNull(converters, "converters");

        final List<PropertyInfo> propertyInfos = new ArrayList<>();
        final List<PropertyInfo> observableListInfos = new ArrayList<>();

        for (Field field : ReflectionHelper.getInheritedDeclaredFields(beanClass)) {
            PropertyType type = null;
            if (Property.class.isAssignableFrom(field.getType())) {
                type = PropertyType.PROPERTY;
            } else if (ObservableList.class.isAssignableFrom(field.getType())) {
                type = PropertyType.OBSERVABLE_LIST;
            }
            final Class<?> parameterType = ReflectionHelper.getTypeParameter(field);
            if (type != null && parameterType != null) {
                final Converter converter = converters.getConverter(parameterType);
                final PropertyInfo propertyInfo = new PropertyInfo(converter, field);
                if (type == PropertyType.PROPERTY) {
                    propertyInfos.add(propertyInfo);
                } else {
                    observableListInfos.add(propertyInfo);
                }
            }
        }
        return new ClassInfo(id, beanClass, propertyInfos, observableListInfos);
    }

    public static ClassInfo create(final Class<?> beanClass, final Converters converters) {
        return create(idCounter.incrementAndGet() + "", beanClass, converters);
    }
}
