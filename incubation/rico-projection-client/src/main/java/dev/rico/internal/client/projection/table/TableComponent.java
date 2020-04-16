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

import dev.rico.internal.projection.table.PropertyColumn;
import dev.rico.internal.projection.table.Table;
import dev.rico.remoting.Property;
import dev.rico.remoting.client.javafx.FXBinder;
import javafx.scene.control.TableView;

public class TableComponent<S> extends TableView<S> {

    public TableComponent(Property<Table<S>> tableProperty) {
        tableProperty.onChanged(e -> {
            if (e.getNewValue() != null) {
                update(e.getNewValue());
            }
        });
        if (tableProperty.get() != null) {
            update(tableProperty.get());
        }
    }

    public TableComponent(Table<S> table) {
        update(table);
    }

    private void update(Table<S> table) {
        FXBinder.bind(getItems()).to(table.getItems());
        FXBinder.bind(getColumns()).to(table.getColumns(), column -> {
            if (column instanceof PropertyColumn) {
                return new PropertyTableColumn<S, Object>((PropertyColumn) column);
            } else {
                //TODO: Action Column, Error
                return null;
            }
        });
        FXBinder.bind(table.selectedValueProperty()).to(getSelectionModel().selectedItemProperty());
    }
}
