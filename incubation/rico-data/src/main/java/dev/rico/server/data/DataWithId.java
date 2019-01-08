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
package dev.rico.server.data;

import java.io.Serializable;

/**
 * Basic interface for all entity types that are defined by an id
 * @param <T> type of the id
 */
public interface DataWithId<T extends Serializable> extends Serializable {

    /**
     * Returns the id
     * @return the id
     */
    T getId();

    /**
     * Sets the id of the entity
     * @param id
     */
    void setId(T id);
}
