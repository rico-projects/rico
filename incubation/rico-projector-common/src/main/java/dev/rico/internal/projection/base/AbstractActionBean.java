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
package dev.rico.internal.projection.base;


import dev.rico.internal.projection.metadata.KeyValue;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;

public abstract class AbstractActionBean extends AbstractProjectableBean implements Action {

    private Property<Boolean> disabled;

    private Property<String> description;

    private Property<Icon> icon;

    private ObservableList<KeyValue> layoutMetadata;

    private Property<String> title;

    private Property<Boolean> blockUi;

    private Property<Boolean> blockOnAction;

    @Override
    public Property<Boolean> disabledProperty() {
        return disabled;
    }

    @Override
    public Property<Boolean> blockUiProperty() {
        return blockUi;
    }

    @Override
    public Property<Boolean> blockOnActionProperty() {
        return blockOnAction;
    }

    @Override
    public Property<String> descriptionProperty() {
        return description;
    }

    @Override
    public Property<Icon> iconProperty() {
        return icon;
    }

    @Override
    public ObservableList<KeyValue> getLayoutMetadata() {
        return layoutMetadata;
    }

    @Override
    public Property<String> titleProperty() {
        return title;
    }

}
