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
package dev.rico.internal.projection.form;

import dev.rico.internal.projection.base.WithDescription;
import dev.rico.internal.projection.base.WithIcon;
import dev.rico.internal.projection.base.WithLayoutMetadata;
import dev.rico.internal.projection.base.WithTitle;
import dev.rico.internal.projection.message.Message;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public interface FormField<T> extends WithTitle, WithDescription, WithIcon, WithLayoutMetadata {

    Property<Boolean> mandatoryProperty();

    Property<Boolean> disabledProperty();

    Property<Boolean> editableProperty();

    Property<Class> contentTypeProperty();

    Property<T> valueProperty();

    ObservableList<Message> getMessages();

    default boolean isMandatory() {
        Boolean mandatory = mandatoryProperty().get();
        if(mandatory == null) {
            return false;
        }
        return mandatory.booleanValue();
    }

    default boolean isDisabled() {
        Boolean disabled = disabledProperty().get();
        if(disabled == null) {
            return false;
        }
        return disabled.booleanValue();
    }

    default boolean isEditable() {
        Boolean val = editableProperty().get();
        if(val == null) {
            return false;
        }
        return val.booleanValue();
    }

    default T getValue() {
        return valueProperty().get();
    }

    default Class<T> getContentType() {
        return contentTypeProperty().get();
    }

    default void setMandatory(boolean mandatory) {
        mandatoryProperty().set(mandatory);
    }

    default void setDisabled(boolean disabled) {
        disabledProperty().set(disabled);
    }

    default void setEditable(boolean editable) {
        editableProperty().set(editable);
    }

    default void setValue(T value) {
        valueProperty().set(value);
    }

    default void setContentType(Class<T> contentType) {
        contentTypeProperty().set(contentType);
    }

}
