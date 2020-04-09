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

import dev.rico.remoting.client.javafx.BidirectionalConverter;
import dev.rico.core.functional.Binding;
import dev.rico.remoting.client.javafx.binding.NumericJavaFXBidirectionaBinder;
import dev.rico.remoting.Property;
import javafx.beans.value.ChangeListener;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractNumericJavaFXBidirectionalBinder<S extends Number> extends DefaultJavaFXBinder<Number> implements NumericJavaFXBidirectionaBinder<S> {


    private final javafx.beans.property.Property<Number> javaFxProperty;

    public AbstractNumericJavaFXBidirectionalBinder(final javafx.beans.property.Property<Number> javaFxProperty) {
        super(javaFxProperty);
        this.javaFxProperty = javaFxProperty;
    }

    @Override
    public <T> Binding bidirectionalTo(final Property<T> property, final BidirectionalConverter<T, Number> converter) {
        final Binding unidirectionalBinding = to(property, converter);
        final ChangeListener<Number> listener = (obs, oldVal, newVal) -> property.set(converter.convertBack(newVal));
        javaFxProperty.addListener(listener);
        return () -> {
            javaFxProperty.removeListener(listener);
            unidirectionalBinding.unbind();
        };
    }

    @Override
    public <T> Binding bidirectionalToNumeric(final Property<T> property, final BidirectionalConverter<T, S> converter) {
        final Binding unidirectionalBinding = to(property, converter);

        final ChangeListener<Number> listener = (obs, oldVal, newVal) -> property.set(converter.convertBack(convertNumber(newVal)));
        javaFxProperty.addListener(listener);
        return () -> {
            javaFxProperty.removeListener(listener);
            unidirectionalBinding.unbind();
        };
    }
}
