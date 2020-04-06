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
package dev.rico.internal.projection.metadata;

import dev.rico.internal.projection.base.WithLayoutMetadata;
import dev.rico.internal.projection.metadata.concrete.BooleanKeyValueBean;
import dev.rico.internal.projection.metadata.concrete.DoubleKeyValueBean;
import dev.rico.internal.projection.metadata.concrete.StringKeyValueBean;
import dev.rico.core.functional.Subscription;
import dev.rico.remoting.BeanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MetadataUtilities {

    public static Subscription addListenerToMetadata(WithLayoutMetadata metadata, Runnable onChange) {
        final Map<KeyValue, List<Subscription>> subscriptionMap = new HashMap<>();

        Subscription mainSubscription = metadata.getLayoutMetadata().onChanged(e -> {
            e.getChanges().forEach(c -> {
                if(c.isRemoved()) {
                    c.getRemovedElements().forEach(m -> {
                        List<Subscription> subscriptions = subscriptionMap.computeIfAbsent(m, key -> new ArrayList<>());
                        subscriptions.forEach(s -> s.unsubscribe());
                        subscriptions.clear();
                    });
                }
                if(c.isAdded()) {
                    metadata.getLayoutMetadata().subList(c.getFrom(), c.getTo()).forEach(m -> {
                        List<Subscription> subscriptions = subscriptionMap.computeIfAbsent(m, key -> new ArrayList<>());
                        subscriptions.add(m.keyProperty().onChanged(event -> onChange.run()));
                        subscriptions.add(m.valueProperty().onChanged(event -> onChange.run()));
                    });
                }
            });
        });
        metadata.getLayoutMetadata().forEach(m -> {
            List<Subscription> subscriptions = subscriptionMap.computeIfAbsent(m, key -> new ArrayList<>());
            subscriptions.add(m.keyProperty().onChanged(event -> onChange.run()));
            subscriptions.add(m.valueProperty().onChanged(event -> onChange.run()));
        });

        return () -> {
            mainSubscription.unsubscribe();
            subscriptionMap.forEach((k, v) -> {
                v.forEach(s -> s.unsubscribe());
            });
        };
    }

    public static KeyValue<String> getOrCreateStringBasedMetadata(final String metadataKey, final WithLayoutMetadata bean, final BeanManager beanManager) {
        return getOrCreateMetadata(StringKeyValueBean.class, metadataKey, bean, beanManager);
    }

    public static KeyValue<Boolean> getOrCreateBooleanBasedMetadata(final String metadataKey, final WithLayoutMetadata bean, final BeanManager beanManager) {
        return getOrCreateMetadata(BooleanKeyValueBean.class, metadataKey, bean, beanManager);
    }

    public static KeyValue<Double> getOrCreateDoubleBasedMetadata(final String metadataKey, final WithLayoutMetadata bean, final BeanManager beanManager) {
        return getOrCreateMetadata(DoubleKeyValueBean.class, metadataKey, bean, beanManager);
    }

    private static <T> KeyValue<T> getOrCreateMetadata(Class<? extends KeyValue> beanClass, final String metadataKey, final WithLayoutMetadata bean, final BeanManager beanManager) {
        KeyValue<T> metadata = getMetadata(metadataKey, bean).orElse(null);
        if (metadata == null) {
            metadata = beanManager.create(beanClass);
            metadata.setKey(metadataKey);
            bean.getLayoutMetadata().add(metadata);
        }
        return metadata;
    }

    public static Optional<KeyValue> getMetadata(final String metadataKey, final WithLayoutMetadata bean) {
        return bean.getLayoutMetadata().stream().
                filter(m -> Optional.ofNullable(m.getKey()).orElse("").equals(metadataKey)).
                findAny();
    }
}
