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
package dev.rico.internal.projection.graph;

import dev.rico.internal.projection.metadata.KeyValue;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class GraphDataValueBean {

    private Property<String> name;

    private Property<Double> value;

    private ObservableList<KeyValue> layoutMetadata;

    public Property<String> nameProperty() {
        return name;
    }

    public Property<Double> valueProperty() {
        return value;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Double getValue() {
        return value.get();
    }

    public void setValue(Double value) {
        this.value.set(value);
    }

    public ObservableList<KeyValue> getLayoutMetadata() {
        return layoutMetadata;
    }
}
