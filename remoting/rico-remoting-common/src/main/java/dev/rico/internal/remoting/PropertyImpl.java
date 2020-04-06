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

import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.remoting.Property;
import dev.rico.remoting.ValueChangeEvent;
import dev.rico.remoting.ValueChangeListener;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * An implementation of {@link Property} that is used for all remoting Beans generated from class definitions.
 *
 * @param <T> The type of the wrapped property.
 */
@API(since = "0.x", status = INTERNAL)
public class PropertyImpl<T> implements Property<T> {

    private T internalValue;

    private final ValueChangeListener<? super T> internalListener;

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

    private boolean internalValueChange = false;

    public PropertyImpl(final ValueChangeListener<? super T> internalListener) {
        this.internalListener = Assert.requireNonNull(internalListener, "internalListener");
    }

    @Override
    public Subscription onChanged(final ValueChangeListener<? super T> listener) {
        listeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                listeners.remove(listener);
            }
        };
    }

    protected void firePropertyChanged(final T oldValue, final T newValue) {
        final ValueChangeEvent<T> event = new ValueChangeEvent<T>() {
            @Override
            public Property<T> getSource() {
                return PropertyImpl.this;
            }

            @Override
            public T getOldValue() {
                return oldValue;
            }

            @Override
            public T getNewValue() {
                return newValue;
            }
        };
        notifyInternalListeners(event);
        notifyExternalListeners(event);
    }

    protected void notifyExternalListeners(ValueChangeEvent<T> event) {
        for(final ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(event);
        }
    }

    protected void notifyInternalListeners(ValueChangeEvent<T> event) {
        if (!internalValueChange) {
            internalListener.valueChanged(event);
        }
    }

    public String toString() {
        return "Remoting " + getClass().getSimpleName() + "[value: " + get() + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(get());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PropertyImpl<?> that = (PropertyImpl<?>) o;
        return Objects.equals(get(), that.get());
    }

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
        internalValueChange = true;
        try {
            set(value);
        } finally {
            internalValueChange = false;
        }
    }
}
