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
package dev.rico.internal.projection.base;


import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public interface Icon extends Projectable {

    Property<String> iconFamilyProperty();

    Property<String> iconCodeProperty();

    default String getIconFamily() {
        return iconFamilyProperty().get();
    }

    default String getIconCode() {
        return iconCodeProperty().get();
    }

    default void setIconFamily(String iconFamily) {
        iconFamilyProperty().set(iconFamily);
    }

    default void setIconCode(String iconCode) {
        iconCodeProperty().set(iconCode);
    }
}
