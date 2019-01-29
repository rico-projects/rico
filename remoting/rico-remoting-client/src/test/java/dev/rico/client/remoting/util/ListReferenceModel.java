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

import dev.rico.remoting.ObservableList;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class ListReferenceModel {

    public enum DataType {LIST_VALUE_1, LIST_VALUE_2}

    private ObservableList<SimpleTestModel> objectList;

    private ObservableList<String> primitiveList;

    public ObservableList<SimpleTestModel> getObjectList() {
        return objectList;
    }

    public ObservableList<String> getPrimitiveList() {
        return primitiveList;
    }
}
