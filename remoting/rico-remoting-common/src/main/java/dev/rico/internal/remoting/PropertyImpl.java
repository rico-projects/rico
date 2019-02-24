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
package dev.rico.internal.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.info.PropertyInfo;
import dev.rico.internal.remoting.legacy.core.BaseAttribute;
import dev.rico.remoting.Property;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * An implementation of {@link Property} that is used for all remoting Beans generated from class definitions.
 *
 * @param <T> The type of the wrapped property.
 */
@API(since = "0.x", status = INTERNAL)
public class PropertyImpl<T> extends AbstractProperty<T> {

    private final BaseAttribute attribute;

    private final PropertyInfo propertyInfo;

    public PropertyImpl(final BaseAttribute attribute, final PropertyInfo propertyInfo) {
        this.attribute = Assert.requireNonNull(attribute, "attribute");
        this.propertyInfo = Assert.requireNonNull(propertyInfo, "propertyInfo");

        attribute.addPropertyChangeListener(BaseAttribute.VALUE_NAME, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                Assert.requireNonNull(evt, "evt");
                try {
                    final T oldValue = (T) PropertyImpl.this.propertyInfo.convertFromRemoting(evt.getOldValue());
                    final T newValue = (T) PropertyImpl.this.propertyInfo.convertFromRemoting(evt.getNewValue());
                    if (oldValue == null && newValue != null ||
                            oldValue != null && newValue == null ||
                            (oldValue != null && newValue != null && !oldValue.equals(newValue))) {
                       firePropertyChanged(oldValue, newValue);
                    }
                } catch (final Exception e) {
                    throw new MappingException("Error in property change handling for property: " + attribute.getPropertyName() + " in attribute with name: " + propertyInfo.getAttributeName() + " and Id: " + attribute.getId() + " - old value: " + evt.getOldValue() +" new value: " + evt.getNewValue(), e);
                }
            }
        });
    }

    @Override
    public void set(final T value) {
        try {
            attribute.setValue(propertyInfo.convertToRemoting(value));
        } catch (final ValueConverterException e) {
            throw new MappingException("Error in mutating property value!", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T) propertyInfo.convertFromRemoting(attribute.getValue());
        } catch (final ValueConverterException e) {
            throw new MappingException("Error in accessing property value!", e);
        }
    }
}
