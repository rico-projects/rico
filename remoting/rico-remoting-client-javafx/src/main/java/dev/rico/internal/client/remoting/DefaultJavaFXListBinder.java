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
package dev.rico.internal.client.remoting;

import dev.rico.core.functional.Binding;
import dev.rico.client.remoting.binding.JavaFXListBinder;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ObservableList;
import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import javafx.collections.ListChangeListener;
import org.apiguardian.api.API;

import java.util.IdentityHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DefaultJavaFXListBinder<S> implements JavaFXListBinder<S> {

    private javafx.collections.ObservableList<S> list;

    private static IdentityHashMap<javafx.collections.ObservableList, javafx.collections.ObservableList> boundLists = new IdentityHashMap<>();

    public DefaultJavaFXListBinder(final javafx.collections.ObservableList<S> list) {
        Assert.requireNonNull(list, "list");
        this.list = list;
    }

    @Override
    public <T> Binding to(final ObservableList<T> remotingList, final Function<? super T, ? extends S> converter) {
        Assert.requireNonNull(remotingList, "remotingList");
        Assert.requireNonNull(converter, "converter");
        if(boundLists.containsKey(list)) {
            throw new UnsupportedOperationException("A JavaFX list can only be bound to one remoting model list!");
        }

        boundLists.put(list, list);
        final InternalListChangeListener<T> listChangeListener = new InternalListChangeListener<>(converter);
        final Subscription subscription = remotingList.onChanged(listChangeListener);

        list.setAll(remotingList.stream().map(converter).collect(Collectors.toList()));

        ListChangeListener<S> readOnlyListener = c -> {
            if (!listChangeListener.onChange) {
                throw new UnsupportedOperationException("A JavaFX list that is bound to a remoting model list can only be modified by the binding!");
            }
        };
        list.addListener(readOnlyListener);


        return () -> {
            subscription.unsubscribe();
            list.removeListener(readOnlyListener);
            boundLists.remove(list);
        };
    }

    @Override
    public <T> Binding bidirectionalTo(final ObservableList<T> remotingList, final Function<? super T, ? extends S> converter, final Function<? super S, ? extends T> backConverter) {
        Assert.requireNonNull(remotingList, "remotingList");
        Assert.requireNonNull(converter, "converter");
        Assert.requireNonNull(backConverter, "backConverter");
        if(boundLists.containsKey(list)) {
            throw new IllegalStateException("A JavaFX list can only be bound to one remoting model list!");
        }

        final InternalBidirectionalListChangeListener<T> listChangeListener = new InternalBidirectionalListChangeListener<>(remotingList, converter, backConverter);
        final Subscription subscription = remotingList.onChanged(listChangeListener);

        list.setAll(remotingList.stream().map(converter).collect(Collectors.toList()));

        list.addListener(listChangeListener);

        return () -> {
            subscription.unsubscribe();
            list.removeListener(listChangeListener);
        };
    }

    private class InternalListChangeListener<T> implements dev.rico.remoting.ListChangeListener<T> {

        private final Function<? super T, ? extends S> converter;

        protected boolean onChange;

        private InternalListChangeListener(final Function<? super T, ? extends S> converter) {
            this.converter = converter;
            onChange = false;
        }

        @Override
        public void listChanged(final ListChangeEvent<? extends T> e) {
            if (onChange) {
                return;
            }

            onChange = true;
            try {
                for (final ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    final int index = c.getFrom();
                    if (c.isRemoved() || c.isReplaced()) {
                        list.subList(index, index + c.getRemovedElements().size()).clear();
                    }
                    if (c.isAdded() || c.isReplaced()) {
                        list.addAll(index,
                                e.getSource()
                                        .subList(index, c.getTo()).stream()
                                        .map(converter)
                                        .collect(Collectors.toList()));
                    }
                }
            } finally {
                onChange = false;
            }
        }
    }

    private class InternalBidirectionalListChangeListener<T> extends InternalListChangeListener<T> implements ListChangeListener<S> {

        private final ObservableList<T> remotingList;
        private final Function<? super S, ? extends T> backConverter;

        private InternalBidirectionalListChangeListener(ObservableList<T> remotingList,
                                                        Function<? super T, ? extends S> converter,
                                                        Function<? super S, ? extends T> backConverter) {
            super(converter);
            this.remotingList = remotingList;
            this.backConverter = backConverter;
        }

        @Override
        public void onChanged(Change<? extends S> change) {
            if (onChange) {
                return;
            }

            onChange = true;
            try {
                while (change.next()) {
                    // TODO: Replace with subList() operation once implemented
                    final int index = change.getFrom();
                    if (change.wasRemoved() || change.wasReplaced()) {
                        for (int i = 0; i < change.getRemovedSize(); i++) {
                            remotingList.remove(index);
                        }
                    }
                    if (change.wasAdded() || change.wasReplaced()) {
                        remotingList.addAll(index,
                                change.getAddedSubList().stream()
                                        .map(backConverter)
                                        .collect(Collectors.toList()));
                    }
                }
            } finally {
                onChange = false;
            }
        }
    }
}
