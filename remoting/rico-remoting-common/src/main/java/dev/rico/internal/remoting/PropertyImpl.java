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
package dev.rico.internal.remoting;

import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * An implementation of {@link Property} that is used for all remoting Beans generated from class definitions.
 *
 * @param <T> The type of the wrapped property.
 */
@API(since = "0.x", status = INTERNAL)
public class PropertyImpl<T> extends AbstractProperty<T> {

    private T internalValue;

    @Override
    public void set(final T value) {
        final T oldValue = internalValue;
        internalValue = value;
        firePropertyChanged(oldValue, internalValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return internalValue;
    }


    public void internalSet(T value) {
        final T oldValue = internalValue;
        internalValue = value;
        firePropertyChanged(oldValue, internalValue);
    }

    public void setWithOutSendCommand(Object newValue) {
    }
}
