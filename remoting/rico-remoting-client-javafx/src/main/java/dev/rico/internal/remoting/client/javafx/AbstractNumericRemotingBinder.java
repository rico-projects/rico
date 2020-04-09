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
package dev.rico.internal.remoting.client.javafx;

import dev.rico.core.functional.Binding;
import dev.rico.core.functional.Subscription;
import dev.rico.remoting.Property;
import dev.rico.remoting.client.javafx.BidirectionalConverter;
import dev.rico.remoting.client.javafx.binding.NumericRemotingBinder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractNumericRemotingBinder<T extends Number> extends DefaultRemotingBinder<T> implements NumericRemotingBinder<T> {

    private final Property<T> property;

    public AbstractNumericRemotingBinder(final Property<T> property) {
        super(property);
        this.property = property;
    }

    protected abstract boolean equals(Number n, T t);

    protected abstract BidirectionalConverter<Number, T> getConverter();

    @Override
    public Binding toNumeric(final ObservableValue<Number> observableValue) {
        if (observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        final ChangeListener<Number> listener = (obs, oldVal, newVal) -> {
            if (!equals(newVal, property.get())) {
                property.set(getConverter().convert(newVal));
            }
        };
        observableValue.addListener(listener);
        if (!equals(observableValue.getValue(), property.get())) {
            property.set(getConverter().convert(observableValue.getValue()));
        }
        return () -> observableValue.removeListener(listener);
    }

    @Override
    public Binding bidirectionalToNumeric(final javafx.beans.property.Property<Number> javaFxProperty) {
        if (javaFxProperty == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        final Binding unidirectionalBinding = toNumeric(javaFxProperty);
        final Subscription subscription = property.onChanged(e -> {
            if (!equals(javaFxProperty.getValue(), property.get())) {
                javaFxProperty.setValue(getConverter().convertBack(property.get()));
            }
        });
        return () -> {
            unidirectionalBinding.unbind();
            subscription.unsubscribe();
        };
    }
}
