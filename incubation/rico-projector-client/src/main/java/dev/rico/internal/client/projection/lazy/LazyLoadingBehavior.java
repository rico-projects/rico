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
package dev.rico.internal.client.projection.lazy;

import dev.rico.internal.core.ReflectionHelper;
import dev.rico.internal.projection.lazy.LazyList;
import dev.rico.internal.projection.lazy.LazyListElement;
import dev.rico.core.functional.Subscription;
import dev.rico.remoting.ListChangeListener;
import dev.rico.remoting.ValueChangeListener;
import com.sun.javafx.scene.control.skin.VirtualContainerBase;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;

import java.util.stream.IntStream;

public class LazyLoadingBehavior<T extends LazyListElement> {

    private Control control;

    private VirtualFlow flow;

    private ObjectProperty<LazyList<T>> model;

    private ObjectProperty<ObservableList<T>> items;

    private Subscription listLenghtListenerSubscription;

    private Subscription listContentListenerSubscription;

    public LazyLoadingBehavior(TableView<T> tableView) {
        this(tableView, tableView.itemsProperty(), tableView.getSelectionModel());
    }

    public LazyLoadingBehavior(ListView<T> listView) {
        this(listView, listView.itemsProperty(), listView.getSelectionModel());
    }

    private LazyLoadingBehavior(Control control, ObjectProperty<ObservableList<T>> items, MultipleSelectionModel<T> selectionModel) {
        this.control = control;

        this.items = new SimpleObjectProperty<>();
        this.items.bind(items);

        this.model = new SimpleObjectProperty<>();

        selectionModel.getSelectedItems().addListener((javafx.collections.ListChangeListener<? super T>)  e -> {
            getModel().setSelectedValue(selectionModel.getSelectedItem());
            getModel().getSelectedValues().setAll(selectionModel.getSelectedItems());
        });

        ValueChangeListener<Integer> listLenghtListener = e -> {
            if (getModel() == null || getModel().listLengthProperty().get() == null) {
                this.getItems().clear();
            } else if (getItems().size() < getModel().listLengthProperty().get()) {
                IntStream.range(getItems().size(), getModel().listLengthProperty().get()).forEach(i -> getItems().add(createElementPlaceholder()));
            } else if (getItems().size() > getModel().listLengthProperty().get()) {
                getItems().remove(getModel().listLengthProperty().get(), getItems().size());
            }
        };

        ListChangeListener<T> listContentListener = e -> {
            e.getChanges().forEach(c -> {
                if (c.isRemoved()) {
                    c.getRemovedElements().forEach(elem -> getItems().set(elem.indexProperty().get(), createElementPlaceholder()));
                } else if (c.isAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        getItems().set(e.getSource().get(i).indexProperty().get(), e.getSource().get(i));
                    }
                } else if (c.isReplaced()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        getItems().set(e.getSource().get(i).indexProperty().get(), e.getSource().get(i));
                    }
                }
            });
        };

        model.addListener((obs, oldVal, newVal) -> {
            if (listLenghtListenerSubscription != null) {
                listLenghtListenerSubscription.unsubscribe();
            }
            if (listContentListenerSubscription != null) {
                listContentListenerSubscription.unsubscribe();
            }
            if (newVal != null) {
                listLenghtListenerSubscription = newVal.listLengthProperty().onChanged(listLenghtListener);
                listContentListenerSubscription = newVal.getLoadedContent().onChanged(listContentListener);
            }
            listLenghtListener.valueChanged(null);
        });

        control.skinProperty().addListener(e -> init());
        if(control.getSkin() != null) {
            init();
        }
    }

    private void init() {
        try {
            flow = (VirtualFlow) ReflectionHelper.getPrivileged(VirtualContainerBase.class.getDeclaredField("flow"), control.getSkin());

            ScrollBar hBar = (ScrollBar) ReflectionHelper.getPrivileged(VirtualFlow.class.getDeclaredField("hbar"), flow);
            ScrollBar vBar = (ScrollBar) ReflectionHelper.getPrivileged(VirtualFlow.class.getDeclaredField("vbar"), flow);

            hBar.valueProperty().addListener(e -> update());
            hBar.minProperty().addListener(e -> update());
            hBar.maxProperty().addListener(e -> update());
            hBar.setOnScroll(e -> update());
            hBar.setOnScrollFinished(e -> update());

            vBar.valueProperty().addListener(e -> update());
            vBar.minProperty().addListener(e -> update());
            vBar.maxProperty().addListener(e -> update());
            vBar.setOnScroll(e -> update());
            vBar.setOnScrollFinished(e -> update());

            control.widthProperty().addListener(e -> update());
            control.heightProperty().addListener(e -> update());

            update();
        } catch (Exception e) {
            throw new RuntimeException("Can't createList behavior", e);
        }
    }

    private void update() {
        try {
            IndexedCell firstCell = flow.getFirstVisibleCell();
            IndexedCell lastCell = flow.getLastVisibleCell();

            if (firstCell != null && lastCell != null) {
                int firstIndex = firstCell.getIndex();
                int lastIndex = lastCell.getIndex();

                IntStream.rangeClosed(firstIndex, lastIndex).forEach(i -> {
                    if (getItems().get(i) == null && !getModel().getNeededContent().contains(i)) {
                        getModel().getNeededContent().add(i);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Lazy loading error", e);
        }
    }

    protected T createElementPlaceholder() {
        return null;
    }

    public LazyList<T> getModel() {
        return model.get();
    }

    public ObjectProperty<LazyList<T>> modelProperty() {
        return model;
    }

    public void setModel(LazyList<T> model) {
        this.model.set(model);
    }

    public ObservableList<T> getItems() {
        return items.get();
    }
}
