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
import dev.rico.client.remoting.FXBinder;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * This class can be used to create a unidirectional or bidirectional binding between a JavaFX property and a
 * remoting property. Normally a developer don't need to create new instances of this class since it's part of a
 * fluent API. To create bindings see {@link FXBinder}
 * @param <S> value type for the properties
 */
@API(since = "0.x", status = MAINTAINED)
public interface JavaFXBidirectionalBinder<S> extends JavaFXBinder<S> {

    /**
     * Bind the given JavaFX property bidirectional to the remoting property
     * @param remotingProperty the remoting property
     * @return the binding
     */
    default Binding bidirectionalTo(final Property<S> remotingProperty) {
        return bidirectionalTo(remotingProperty, new BidirectionalConverter<S, S>() {
            @Override
            public S convertBack(final S value) {
                return value;
            }

            @Override
            public S convert(final S value) {
                return value;
            }
        });
    }

    /**
     * Bind the given JavaFX property bidirectional to the remoting property
     * @param property the remoting property
     * @param converter a converter.
     * @param <T> converted type
     * @return the binding.
     */
    <T> Binding bidirectionalTo(final Property<T> property, final BidirectionalConverter<T, S> converter);

}
