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
package dev.rico.internal.client.projection.table;

import dev.rico.internal.core.ReflectionHelper;
import dev.rico.internal.projection.table.PropertyColumn;
import dev.rico.remoting.Property;
import dev.rico.remoting.client.javafx.FXBinder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PropertyTableColumn<S, T> extends AbstractTableColumn<S, T> {

    public PropertyTableColumn(PropertyColumn column) {
        super(column);

        setCellValueFactory(e -> {
            try {
                Property<T> property = getPropertyForRowItem(e.getValue(), column.getPropertyName());
                ObjectProperty<T> javaFXProperty = new SimpleObjectProperty<T>();
                FXBinder.bind(javaFXProperty).bidirectionalTo(property);
                return javaFXProperty;
                //TODO: No Unbind here. This will cause memory leak!
            } catch (IllegalAccessException e1) {
                throw new RuntimeException("TODO", e1);
            }
        });

    }

    private Property<T> getPropertyForRowItem(S rowItem, String propertyName) throws IllegalAccessException {
        return (Property<T>) ReflectionHelper.getInheritedDeclaredField(rowItem.getClass(), propertyName).get(rowItem);
    }

}
