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
package dev.rico.client.remoting.binding;

import dev.rico.core.functional.Binding;
import dev.rico.client.remoting.BidirectionalConverter;
import dev.rico.client.remoting.Converter;
import dev.rico.client.remoting.FXBinder;
import javafx.beans.value.ObservableValue;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * This class can be used to create a unidirectional or bidirectional binding between a JavaFX property and a
 * remoting property. Normally a developer don't need to create new instances of this class since it's part of a
 * fluent API. To create bindings see {@link FXBinder}
 * @param <S> datatype of the JavaFX property
 */
@API(since = "0.x", status = MAINTAINED)
public interface Binder<S> {

    /**
     * Create a unidirectional binding between the given JavaFX property and the remoting property.
     * @param observableValue the javaFX property
     * @return the binding
     */
    default Binding to(final ObservableValue<? extends S> observableValue) {
        if (observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        return to(observableValue, n -> n);
    }

    /**
     * Create a unidirectional binding between the given JavaFX property and the remoting property.
     * @param observableValue the javaFX property
     * @param converter a converter
     * @param <T> type of the converted data
     * @return the binding
     */
    <T> Binding to(final ObservableValue<T> observableValue, final Converter<? super T, ? extends S> converter);

    /**
     * Create a bidirectional binding between the given JavaFX property and the remoting property.
     * @param property  the javaFX property
     * @return the binding
     */
    default Binding bidirectionalTo(final javafx.beans.property.Property<S> property) {
        if (property == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        return bidirectionalTo(property, new BidirectionalConverter<S, S>() {
            @Override
            public S convertBack(S value) {
                return value;
            }

            @Override
            public S convert(S value) {
                return value;
            }
        });
    }

    /**
     * Create a bidirectional binding between the given JavaFX property and the remoting property.
     * @param property the javaFX property
     * @param converter the converter
     * @param <T> type of the converted data
     * @return the binding
     */
    <T> Binding bidirectionalTo(final javafx.beans.property.Property<T> property, final BidirectionalConverter<T, S> converter);
}
