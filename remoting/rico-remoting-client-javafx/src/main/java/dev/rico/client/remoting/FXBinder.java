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
package dev.rico.client.remoting;

import dev.rico.internal.client.remoting.DefaultRemotingBinder;
import dev.rico.internal.client.remoting.DefaultJavaFXBidirectionalBinder;
import dev.rico.internal.client.remoting.DefaultJavaFXBinder;
import dev.rico.internal.client.remoting.DefaultJavaFXListBinder;
import dev.rico.internal.client.remoting.DoubleRemotingBinder;
import dev.rico.internal.client.remoting.DoubleJavaFXBidirectionalBinder;
import dev.rico.internal.client.remoting.FloatRemotingBinder;
import dev.rico.internal.client.remoting.FloatJavaFXBidirectionalBinder;
import dev.rico.internal.client.remoting.IntegerRemotingBinder;
import dev.rico.internal.client.remoting.IntegerJavaFXBidirectionalBinder;
import dev.rico.internal.client.remoting.LongRemotingBinder;
import dev.rico.internal.client.remoting.LongJavaFXBidirectionalBinder;
import dev.rico.remoting.Property;
import dev.rico.client.remoting.binding.Binder;
import dev.rico.client.remoting.binding.JavaFXBidirectionalBinder;
import dev.rico.client.remoting.binding.JavaFXBinder;
import dev.rico.client.remoting.binding.JavaFXListBinder;
import dev.rico.client.remoting.binding.NumericRemotingBinder;
import dev.rico.client.remoting.binding.NumericJavaFXBidirectionaBinder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import org.apiguardian.api.API;

import static dev.rico.internal.core.Assert.requireNonNull;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Utility class to create unidirectional and bidirectional bindings between JavaFX and remoting properties.
 */
@API(since = "0.x", status = MAINTAINED)
public final class FXBinder {

    /**
     * private constructor.
     */
    private FXBinder() {
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param list the JavaFX list
     * @param <T> the data type of the list
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static <T> JavaFXListBinder<T> bind(final ObservableList<T> list) {
        requireNonNull(list, "list");
        return new DefaultJavaFXListBinder(list);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Double> bind(final WritableDoubleValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Float> bind(final WritableFloatValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Integer> bind(final WritableIntegerValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Long> bind(final WritableLongValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @returna binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Double> bind(final DoubleProperty property) {
        requireNonNull(property, "property");
        return new DoubleJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Float> bind(final FloatProperty property) {
        requireNonNull(property, "property");
        return new FloatJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Integer> bind(final IntegerProperty property) {
        requireNonNull(property, "property");
        return new IntegerJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Long> bind(final LongProperty property) {
        requireNonNull(property, "property");
        return new LongJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static <T> JavaFXBinder<T> bind(final WritableValue<T> writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static <T> JavaFXBidirectionalBinder<T> bind(final javafx.beans.property.Property<T> property) {
        requireNonNull(property, "property");
        return new DefaultJavaFXBidirectionalBinder<>(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the remoting property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static <T> Binder<T> bind(final Property<T> property) {
        requireNonNull(property, "property");
        return new DefaultRemotingBinder<>(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the remoting property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericRemotingBinder<Double> bindDouble(final Property<Double> property) {
        requireNonNull(property, "property");
        return new DoubleRemotingBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the remoting property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericRemotingBinder<Float> bindFloat(final Property<Float> property) {
        requireNonNull(property, "property");
        return new FloatRemotingBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the remoting property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericRemotingBinder<Integer> bindInteger(final Property<Integer> property) {
        requireNonNull(property, "property");
        return new IntegerRemotingBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the remoting property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericRemotingBinder<Long> bindLong(final Property<Long> property) {
        requireNonNull(property, "property");
        return new LongRemotingBinder(property);
    }
}
