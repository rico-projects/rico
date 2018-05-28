/*
 * Copyright 2018 Karakun AG.
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
import dev.rico.internal.core.Assert;
import dev.rico.remoting.Property;
import dev.rico.client.remoting.Converter;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(since = "0.x", status = MAINTAINED)
public interface JavaFXBinder<S> {

    default Binding to(Property<? extends S> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        return to(remotingProperty, n -> n);
    }

    <T> Binding to(Property<T> remotingProperty, Converter<? super T, ? extends S> converter);

}
