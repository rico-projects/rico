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

import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.core.BasePresentationModel;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import org.apiguardian.api.API;

import java.util.*;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A {@code BeanRepository} keeps a list of all registered remoting Beans and the mapping between remoting IDs and
 * the associated remoting Bean.
 * <p>
 * A new bean needs to be registered with the {@link #registerBean(Object, BasePresentationModel, UpdateSource)} method and can be deleted
 * with the {@link #delete(Object)} method.
 */
@API(since = "0.x", status = INTERNAL)
public class BeanRepositoryImpl implements BeanRepository {

    private final Map<Object, BasePresentationModel> objectPmToRemotingPm = new IdentityHashMap<>();
    private final Map<String, Object> remotingIdToObjectPm = new HashMap<>();
    private final ModelStore modelStore;
    private final Map<Class<?>, List<BeanAddedListener<?>>> beanAddedListenerMap = new HashMap<>();
    private final List<BeanAddedListener<Object>> anyBeanAddedListeners = new ArrayList<>();
    private final Map<Class<?>, List<BeanRemovedListener<?>>> beanRemovedListenerMap = new HashMap<>();

    private List<BeanRemovedListener<Object>> anyBeanRemovedListeners = new ArrayList<>();

    public BeanRepositoryImpl(final ModelStore modelStore, final EventDispatcher dispatcher) {
        this.modelStore = Assert.requireNonNull(modelStore, "modelStore");

        dispatcher.addRemovedHandler((BasePresentationModel model) -> {
            final Object bean = remotingIdToObjectPm.remove(model.getId());
            if (bean != null) {
                objectPmToRemotingPm.remove(bean);
                if(beanRemovedListenerMap.containsKey(bean.getClass())){
                    for (final BeanRemovedListener beanRemovedListener : beanRemovedListenerMap.get(bean.getClass())) {
                        beanRemovedListener.beanDestructed(bean);
                    }
                }
                for (final BeanRemovedListener beanRemovedListener : anyBeanRemovedListeners) {
                    beanRemovedListener.beanDestructed(bean);
                }
            }
        });
    }

    @Override
    public <T> Subscription addOnAddedListener(final Class<T> beanClass, final BeanAddedListener<? super T> listener) {
        RemotingUtils.assertIsRemotingBean(beanClass);
        beanAddedListenerMap.computeIfAbsent(beanClass, s -> new ArrayList()).add(listener);
        return () -> {
            if (beanAddedListenerMap.containsKey(beanClass)) {
                beanAddedListenerMap.get(beanClass).remove(listener);
            }
        };
    }

    @Override
    public Subscription addOnAddedListener(final BeanAddedListener<Object> listener) {
        anyBeanAddedListeners.add(listener);
        return () -> anyBeanAddedListeners.remove(listener);
    }

    @Override
    public <T> Subscription addOnRemovedListener(final Class<T> beanClass, final BeanRemovedListener<? super T> listener) {
        RemotingUtils.assertIsRemotingBean(beanClass);
        beanRemovedListenerMap.computeIfAbsent(beanClass, s -> new ArrayList()).add(listener);
        return () -> {
            if (beanRemovedListenerMap.containsKey(beanClass)) {
                beanRemovedListenerMap.get(beanClass).remove(listener);
            }
        };
    }

    @Override
    public Subscription addOnRemovedListener(final BeanRemovedListener<Object> listener) {
        anyBeanRemovedListeners.add(listener);
        return () -> anyBeanRemovedListeners.remove(listener);
    }

    @Override
    public boolean isManaged(Object bean) {
        RemotingUtils.assertIsRemotingBean(bean);
        return objectPmToRemotingPm.containsKey(bean);
    }

    @Override
    public <T> void delete(T bean) {
        RemotingUtils.assertIsRemotingBean(bean);
        final BasePresentationModel model = objectPmToRemotingPm.remove(bean);
        if (model != null) {
            remotingIdToObjectPm.remove(model.getId());
            modelStore.remove(model);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> beanClass) {
        RemotingUtils.assertIsRemotingBean(beanClass);
        final List<T> result = new ArrayList<>();
        final List<BasePresentationModel> presentationModels = modelStore.findAllPresentationModelsByType(RemotingUtils.getPresentationModelTypeForClass(beanClass));
        for (BasePresentationModel model : presentationModels) {
            result.add((T) remotingIdToObjectPm.get(model.getId()));
        }
        return result;
    }

    @Override
    public Object getBean(String sourceId) {
        if(sourceId == null) {
            return null;
        }
        if(!remotingIdToObjectPm.containsKey(sourceId)) {
            throw new IllegalArgumentException("No bean instance found with id " + sourceId);
        }
        return remotingIdToObjectPm.get(sourceId);
    }

    @Override
    public String getRemotingId(Object bean) {
        if (bean == null) {
            return null;
        }
        RemotingUtils.assertIsRemotingBean(bean);
        try {
            return objectPmToRemotingPm.get(bean).getId();
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Only managed remoting beans can be used.", ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerBean(Object bean, BasePresentationModel model, UpdateSource source) {
        RemotingUtils.assertIsRemotingBean(bean);
        objectPmToRemotingPm.put(bean, model);
        remotingIdToObjectPm.put(model.getId(), bean);

        if (source == UpdateSource.OTHER) {
            if(beanAddedListenerMap.containsKey(bean.getClass())){
                for (final BeanAddedListener beanAddedListener : beanAddedListenerMap.get(bean.getClass())) {
                    beanAddedListener.beanCreated(bean);
                }
            }
            for (final BeanAddedListener beanAddedListener : anyBeanAddedListeners) {
                beanAddedListener.beanCreated(bean);
            }
        }
    }


}
