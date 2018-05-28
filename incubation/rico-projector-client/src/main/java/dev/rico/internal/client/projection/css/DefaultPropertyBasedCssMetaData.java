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
package dev.rico.internal.client.projection.css;

import javafx.beans.property.Property;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;

/**
 * A CssMetaData class that is bound to a specific property that is part of a Styleable instance.
 * @param <S> Type of the Styleable instance
 * @param <V> Value type of the property
 */
public class DefaultPropertyBasedCssMetaData<S extends Styleable, V> extends AbstractPropertyBasedCssMetaData<S, V> {

    /**
     * Default Constructor
     *
     * @param property name of the CSS property
     * @param converter the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param propertyName Name of the property field in the Styleable class
     * @param defaultValue The default value of the corresponding StyleableProperty
     */
    public DefaultPropertyBasedCssMetaData(String property, StyleConverter<?, V> converter, String propertyName, V defaultValue) {
        super(property, converter, propertyName, defaultValue);
    }

    protected <T extends Property<V> & StyleableProperty<V>> T getProperty(S styleable) {
        try {
            return (T) styleable.getClass().getMethod(getPropertyName() + "Property").invoke(styleable);
        } catch (Exception e) {
            throw new RuntimeException("Can't get StyleableProperty", e);
        }
    }
}