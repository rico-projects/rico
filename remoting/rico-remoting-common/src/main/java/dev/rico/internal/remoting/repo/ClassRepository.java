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

import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.Property;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A {@code ClassRepository} manages {@link ClassInfo} objects for all registered remote Beans. A {@code ClassInfo}
 * object keeps information on class level about the properties and ObservableLists of a RemotingBean.
 */
@API(since = "0.x", status = INTERNAL)
public class ClassRepository {

 private static final Logger LOG = LoggerFactory.getLogger(ClassRepository.class);

    private final Converters converters;

    private final Map<Class<?>, ClassInfo> classToClassInfoMap = new HashMap<>();
    private final Map<String, ClassInfo> modelTypeToClassInfoMap = new HashMap<>();

    public ClassRepository(final Converters converters) {
        this.converters = Assert.requireNonNull(converters, "converters");
    }

    public ClassInfo getClassInfo(final String modelType) {
        return modelTypeToClassInfoMap.get(modelType);
    }

    public ClassInfo getOrCreateClassInfo(final Class<?> beanClass) {
        final ClassInfo existingClassInfo = classToClassInfoMap.get(beanClass);
        if (existingClassInfo != null) {
            return existingClassInfo;
        }
        final ClassInfo classInfo = createClassInfoForClass(beanClass);
        Assert.requireNonNull(classInfo, "classInfo");
        classToClassInfoMap.put(beanClass, classInfo);

        return classToClassInfoMap.get(beanClass);
    }

    private ClassInfo createClassInfoForClass(final Class<?> beanClass) {
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
        return new ClassInfo(beanClass, propertyInfos, observableListInfos);
    }

    private enum PropertyType {PROPERTY, OBSERVABLE_LIST}
}
