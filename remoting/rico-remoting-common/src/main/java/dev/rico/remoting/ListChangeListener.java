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
package dev.rico.remoting;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Listener that can be used to observe changes of an {@link ObservableList}.
 *
 * @param <E> type of elements in the list
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface ListChangeListener<E> {

    /**
     * This method will be called whenever an {@link ObservableList} has changed.
     *
     * @param evt defines the change.
     */
    void listChanged(ListChangeEvent<? extends E> evt);

}
