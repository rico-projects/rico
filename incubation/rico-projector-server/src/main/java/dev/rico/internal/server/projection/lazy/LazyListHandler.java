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
package dev.rico.internal.server.projection.lazy;

import dev.rico.internal.projection.lazy.LazyList;
import dev.rico.internal.projection.lazy.LazyListElement;
import dev.rico.core.functional.Subscription;

import java.util.function.IntFunction;

public class LazyListHandler<E extends LazyListElement> {

    private LazyList<E> listModel;

    private Subscription neededContentSubscription;

    private IntFunction<E> elementFactory;

    public LazyListHandler(LazyList<E> listModel) {
        setListModel(listModel);
    }

    public LazyList<E> getListModel() {
        return listModel;
    }

    public void setListModel(LazyList<E> listModel) {
        this.listModel = listModel;

        if(neededContentSubscription != null) {
            neededContentSubscription.unsubscribe();
        }

        neededContentSubscription = listModel.getNeededContent().onChanged(e -> {
            e.getChanges().forEach(c -> {
                if(c.isAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        E elementBean = elementFactory.apply(listModel.getNeededContent().get(i));
                        elementBean.indexProperty().set(listModel.getNeededContent().get(i));
                        listModel.getLoadedContent().add(elementBean);
                    }
                }
            });
        });
    }

    public IntFunction<E> getElementFactory() {
        return elementFactory;
    }

    public void setElementFactory(IntFunction<E> elementFactory) {
        this.elementFactory = elementFactory;
    }

    public void setListLength(int listLength) {
        listModel.listLengthProperty().set(listLength);
    }

    public int getListLength() {
        return listModel.listLengthProperty().get();
    }
}
