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
package dev.rico.internal.remoting.client;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.AbstractBeanBuilder;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.ClassRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.ListMapper;
import dev.rico.internal.remoting.PresentationModelBuilderFactory;
import dev.rico.internal.remoting.PropertyImpl;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.info.PropertyInfo;
import dev.rico.internal.remoting.legacy.core.Attribute;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientBeanBuilderImpl extends AbstractBeanBuilder {

    public ClientBeanBuilderImpl(final ClassRepository classRepository, final BeanRepository beanRepository, final ListMapper listMapper, final PresentationModelBuilderFactory builderFactory, final EventDispatcher dispatcher) {
        super(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
    }

    protected ObservableList create(final PropertyInfo observableListInfo, final PresentationModel model, final ListMapper listMapper) {
        Assert.requireNonNull(model, "model");
        Assert.requireNonNull(listMapper, "listMapper");
        return new ObservableArrayList() {
            @Override
            protected void notifyInternalListeners(ListChangeEvent event) {
                listMapper.processEvent(observableListInfo, model.getId(), event);
            }
        };
    }

    @SuppressWarnings("deprecation")
    protected Property create(final Attribute attribute, final PropertyInfo propertyInfo) {
        return new PropertyImpl<>(attribute, propertyInfo);
    }
}
