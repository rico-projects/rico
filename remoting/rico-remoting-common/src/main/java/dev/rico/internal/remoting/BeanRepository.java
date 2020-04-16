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
package dev.rico.internal.remoting;

import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.core.functional.Subscription;
import dev.rico.remoting.converter.BeanRepo;
import org.apiguardian.api.API;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@SuppressWarnings("deprecation")
public interface BeanRepository extends BeanRepo {

    <T> Subscription addOnAddedListener(final Class<T> beanClass, final BeanAddedListener<? super T> listener);

    Subscription addOnAddedListener(final BeanAddedListener<Object> listener);

    <T> Subscription addOnRemovedListener(final Class<T> beanClass, final BeanRemovedListener<? super T> listener);

    Subscription addOnRemovedListener(final BeanRemovedListener<Object> listener);

    boolean isManaged(Object bean);

    <T> void delete(T bean);

    <T> List<T> findAll(Class<T> beanClass);

    void registerBean(Object bean, PresentationModel model, UpdateSource source);
}
