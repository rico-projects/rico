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
package dev.rico.client.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.Property;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * A util class that can be used to createList JavaFX properties and lists as wrapper around remoting properties and lists.
 */
@API(since = "0.x", status = MAINTAINED)
public class FXWrapper {


    /**
     * private constructor
     */
    private FXWrapper() {
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.DoubleProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static DoubleProperty wrapDoubleProperty(final Property<Double> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        final DoubleProperty property = new SimpleDoubleProperty();
        FXBinder.bind(property).bidirectionalToNumeric(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.FloatProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static FloatProperty wrapFloatProperty(final Property<Float> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        final FloatProperty property = new SimpleFloatProperty();
        FXBinder.bind(property).bidirectionalToNumeric(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.IntegerProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static IntegerProperty wrapIntProperty(final Property<Integer> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        final IntegerProperty property = new SimpleIntegerProperty();
        FXBinder.bind(property).bidirectionalToNumeric(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.LongProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static LongProperty wrapLongProperty(final Property<Long> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        final LongProperty property = new SimpleLongProperty();
        FXBinder.bind(property).bidirectionalToNumeric(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.BooleanProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static BooleanProperty wrapBooleanProperty(final Property<Boolean> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        final BooleanProperty property = new SimpleBooleanProperty();
        FXBinder.bind(property).bidirectionalTo(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.StringProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static StringProperty wrapStringProperty(final Property<String> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        StringProperty property = new SimpleStringProperty();
        FXBinder.bind(property).bidirectionalTo(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.ObjectProperty} as a wrapper for a remoting property
     *
     * @param remotingProperty the remoting property
     * @return the JavaFX property
     */
    public static <T> ObjectProperty<T> wrapObjectProperty(final Property<T> remotingProperty) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        FXBinder.bind(property).bidirectionalTo(remotingProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.collections.ObservableList} wrapper for a remoting list
     *
     * @param remotingList the remoting list
     * @param <T>         type of the list content
     * @return the JavaFX list
     */
    public static <T> ObservableList<T> wrapList(dev.rico.remoting.ObservableList<T> remotingList) {
        Assert.requireNonNull(remotingList, "remotingList");
        final ObservableList<T> list = FXCollections.observableArrayList(remotingList);

        list.addListener((ListChangeListener<T>) c -> {
            if (listenToFx) {
                listenToRemoting = false;
                while (c.next()) {
                    if (c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
                        for (T removed : c.getRemoved()) {
                            remotingList.remove(removed);
                        }
                        for (T added : c.getAddedSubList()) {
                            remotingList.add(list.indexOf(added), added);
                        }
                    }
                }
                listenToRemoting = true;
            }
        });

        remotingList.onChanged(e -> {
            if (listenToRemoting) {
                listenToFx = false;
                for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    if (c.isAdded()) {
                        for (int i = c.getFrom(); i < c.getTo(); i++) {
                            list.add(i, remotingList.get(i));
                        }
                    } else if (c.isRemoved()) {
                        final int index = c.getFrom();
                        list.remove(index, index + c.getRemovedElements().size());
                    }
                }
                listenToFx = true;
            }
        });

        return list;
    }

    //TODO: HACK
    private static boolean listenToFx = true;

    //TODO: HACK
    private static boolean listenToRemoting = true;
}
