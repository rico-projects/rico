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
package dev.rico.internal.remoting.info;

import dev.rico.internal.remoting.RemotingUtils;
import org.apiguardian.api.API;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClassInfo {
    private final Class<?> beanClass;
    private final String modelType;
    private final Map<String, PropertyInfo> propertyInfoMap;
    private final Map<String, PropertyInfo> observableListInfoMap;

    public ClassInfo(final Class<?> beanClass, final Collection<PropertyInfo> propertyInfos, final Collection<PropertyInfo> observableListInfos) {
        this.beanClass = beanClass;
        modelType = RemotingUtils.getPresentationModelTypeForClass(beanClass);

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

    public String getModelType() {
        return modelType;
    }

    public PropertyInfo getPropertyInfo(final String attributeName) {
        return propertyInfoMap.get(attributeName);
    }

    public PropertyInfo getObservableListInfo(final String attributeName) {
        return observableListInfoMap.get(attributeName);
    }

    public void forEachProperty(final PropertyIterator iterator) {
        for (final PropertyInfo propertyInfo : propertyInfoMap.values()) {
            iterator.call(propertyInfo);
        }
    }

    public void forEachObservableList(final PropertyIterator iterator) {
        for (final PropertyInfo observableListInfo : observableListInfoMap.values()) {
            iterator.call(observableListInfo);
        }
    }

    public interface PropertyIterator {
        void call(PropertyInfo propertyInfo);
    }
}
