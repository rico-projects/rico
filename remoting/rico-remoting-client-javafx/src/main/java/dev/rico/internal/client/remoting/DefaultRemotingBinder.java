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
package dev.rico.internal.client.remoting;

import dev.rico.client.remoting.BidirectionalConverter;
import dev.rico.core.functional.Binding;
import dev.rico.client.remoting.Converter;
import dev.rico.client.remoting.binding.Binder;
import dev.rico.core.functional.Subscription;
import dev.rico.remoting.Property;
import dev.rico.internal.core.Assert;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DefaultRemotingBinder<S> implements Binder<S> {

    private final Property<S> property;

    public DefaultRemotingBinder(final Property<S> property) {
        this.property = Assert.requireNonNull(property, "property");
    }

    @Override
    public <T> Binding to(final ObservableValue<T> observableValue, final Converter<? super T, ? extends S> converter) {
        if (observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        if (converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        final ChangeListener<T> listener = (obs, oldVal, newVal) -> property.set(converter.convert(newVal));
        observableValue.addListener(listener);
        property.set(converter.convert(observableValue.getValue()));
        return () -> observableValue.removeListener(listener);
    }


    @Override
    public <T> Binding bidirectionalTo(final javafx.beans.property.Property<T> javaFxProperty, final BidirectionalConverter<T, S> converter) {
        if (javaFxProperty == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        if (converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        final Binding unidirectionalBinding = to(javaFxProperty, converter);
        final Subscription subscription = property.onChanged(e -> javaFxProperty.setValue(converter.convertBack(property.get())));
        return () -> {
            unidirectionalBinding.unbind();
            subscription.unsubscribe();
        };
    }
}
