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
package dev.rico.internal.client.projection.css;

import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;

/**
 * Abstract CssMetaData class that is bound to a specific property that can be accessed by a Styleable instance.
 *
 * @author Hendrik Ebbers
 * @param <S> Type of the Styleable instance
 * @param <V> Value type of the property
 */
public abstract class AbstractPropertyBasedCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {

    private String propertyName;

    /**
     * Default Constructor
     *
     * @param property name of the CSS property
     * @param converter the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param propertyName Name of the property field
     * @param defaultValue The default value of the corresponding StyleableProperty
     */
    public AbstractPropertyBasedCssMetaData(String property, StyleConverter<?, V> converter, String propertyName, V defaultValue) {
        super(property, converter, defaultValue);
        this.propertyName = propertyName;
    }

    protected abstract <T extends Property<V> & StyleableProperty<V>> T getProperty(S styleable);

    /**
     * Returns the field name of the property
     * @return name of the property
     */
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean isSettable(S styleable) {
        Property<V> property = getProperty(styleable);
        return property == null || !property.isBound();
    }

    @Override
    public StyleableProperty<V> getStyleableProperty(S styleable) {
        return (StyleableProperty<V>) getProperty(styleable);
    }

}
