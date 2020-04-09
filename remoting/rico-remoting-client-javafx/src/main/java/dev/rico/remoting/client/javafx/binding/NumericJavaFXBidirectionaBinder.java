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
package dev.rico.remoting.client.javafx.binding;

import dev.rico.core.functional.Binding;
import dev.rico.remoting.Property;
import dev.rico.remoting.client.javafx.BidirectionalConverter;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(since = "0.x", status = MAINTAINED)
public interface NumericJavaFXBidirectionaBinder<S extends Number> extends JavaFXBidirectionalBinder<Number> {

    default Binding bidirectionalToNumeric(final Property<S> remotingProperty) {
        return bidirectionalTo(remotingProperty, new BidirectionalConverter<S, Number>() {
            @Override
            public S convertBack(final Number value) {
                return convertNumber(value);
            }

            @Override
            public Number convert(final S value) {
                return value;
            }
        });
    }

    S convertNumber(Number value);

    <T> Binding bidirectionalToNumeric(final Property<T> property, final BidirectionalConverter<T, S> converter);
}
