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

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public interface Action extends WithTitle, WithDescription, WithIcon, WithLayoutMetadata, Projectable {

    Property<Boolean> disabledProperty();

    Property<Boolean> blockUiProperty();

    Property<Boolean> blockOnActionProperty();

    default boolean isDisabled() {
        Boolean val = disabledProperty().get();
        if(val == null) {
            return false;
        }
        return val.booleanValue();
    }

    default boolean isBlockUi() {
        Boolean val = blockUiProperty().get();
        if(val == null) {
            return false;
        }
        return val.booleanValue();
    }

    default boolean isBlockOnAction() {
        Boolean val = blockOnActionProperty().get();
        if(val == null) {
            return false;
        }
        return val.booleanValue();
    }

    default void setDisabled(boolean disabled) {
        disabledProperty().set(disabled);
    }

    default void setBlockUi(boolean blockUi) {
        blockUiProperty().set(blockUi);
    }

    default void setBlockOnAction(boolean blockOnAction) {
        blockOnActionProperty().set(blockOnAction);
    }

}
