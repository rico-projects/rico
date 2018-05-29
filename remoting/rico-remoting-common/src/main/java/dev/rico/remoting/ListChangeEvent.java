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

import java.util.List;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * An event that defines the change of an {@link ObservableList}.
 *
 * @param <E> generic type of the list.
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface ListChangeEvent<E> {

    /**
     * Returns the list that has triggered this event
     *
     * @return the list
     */
    ObservableList<E> getSource();

    /**
     * Returns a list of changes of this event
     *
     * @return the list
     */
    List<Change<E>> getChanges();

    /**
     * Defines one change in an {@link ObservableList}.
     *
     * @param <S> generic type of the list.
     */
    interface Change<S> {

        int getFrom();

        int getTo();

        /**
         * Returns a list that contains all elements that were removed from the list.
         *
         * @return the list
         */
        List<S> getRemovedElements();

        /**
         * Returns true if elements were added to the list
         *
         * @return true if elements were added to the list
         */
        boolean isAdded();

        /**
         * Returns true if elements were removed from the list
         *
         * @return true if elements were removed from the list
         */
        boolean isRemoved();

        /**
         * Returns true if elements were replaced in the list
         *
         * @return true if elements were replaced in the list
         */
        boolean isReplaced();
    }
}
