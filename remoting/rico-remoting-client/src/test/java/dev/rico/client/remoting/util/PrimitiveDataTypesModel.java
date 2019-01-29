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
package dev.rico.client.remoting.util;

import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;

@RemotingBean
public class PrimitiveDataTypesModel {

    private Property<String> textProperty;

    private Property<Integer> integerProperty;

    private Property<Double> doubleProperty;

    private Property<Float> floatProperty;

    private Property<Long> longProperty;

    private Property<Boolean> booleanProperty;

    private Property<Byte> byteProperty;

    private Property<Short> shortProperty;

    public Property<String> getTextProperty() {
        return textProperty;
    }

    public Property<Integer> getIntegerProperty() {
        return integerProperty;
    }

    public Property<Double> getDoubleProperty() {
        return doubleProperty;
    }

    public Property<Float> getFloatProperty() {
        return floatProperty;
    }

    public Property<Long> getLongProperty() {
        return longProperty;
    }

    public Property<Boolean> getBooleanProperty() {
        return booleanProperty;
    }

    public Property<Byte> getByteProperty() {
        return byteProperty;
    }

    public Property<Short> getShortProperty() {
        return shortProperty;
    }
}
