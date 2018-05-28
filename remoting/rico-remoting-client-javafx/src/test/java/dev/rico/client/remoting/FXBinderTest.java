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

import dev.rico.core.functional.Binding;
import dev.rico.remoting.ObservableList;
import dev.rico.internal.client.remoting.DefaultBidirectionalConverter;
import dev.rico.internal.remoting.MockedProperty;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.remoting.Property;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableStringValue;
import javafx.collections.FXCollections;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class FXBinderTest {

    private final static double EPSILON = 1e-10;

    @Test
    public void testJavaFXDoubleUnidirectional() {
        Property<Double> doubleRemotingProperty = new MockedProperty<>();
        Property<Number> numberRemotingProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();

        doubleRemotingProperty.set(47.0);
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).to(doubleRemotingProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        doubleRemotingProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        doubleRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        doubleRemotingProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        numberRemotingProperty.set(12.0);
        binding = FXBinder.bind(doubleJavaFXProperty).to(numberRemotingProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        numberRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        numberRemotingProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleRemotingProperty.set(47.0);
        binding = FXBinder.bind(writableDoubleValue).to(doubleRemotingProperty);
        assertEquals(writableDoubleValue.get(), 47.0, EPSILON);
        doubleRemotingProperty.set(100.0);
        assertEquals(writableDoubleValue.get(), 100.0, EPSILON);
        doubleRemotingProperty.set(null);
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
        binding.unbind();
        doubleRemotingProperty.set(100.0);
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleBidirectional() {
        Property<Double> doubleRemotingProperty = new MockedProperty<>();
        Property<Number> numberRemotingProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();

        doubleRemotingProperty.set(47.0);
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(doubleRemotingProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        doubleRemotingProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        doubleRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        assertEquals(doubleRemotingProperty.get().doubleValue(), 12.0, EPSILON);
        doubleJavaFXProperty.setValue(null);
        assertEquals(doubleRemotingProperty.get().doubleValue(), 0.0, EPSILON);

        binding.unbind();
        doubleRemotingProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        numberRemotingProperty.set(12.0);
        binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalTo(numberRemotingProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        numberRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        assertEquals(numberRemotingProperty.get().doubleValue(), 12.0, EPSILON);
        doubleJavaFXProperty.setValue(null);
        assertEquals(numberRemotingProperty.get().doubleValue(), 0.0, EPSILON);

        binding.unbind();
        numberRemotingProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleUnidirectionalWithConverter() {
        Property<String> stringRemotingProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);

        stringRemotingProperty.set("47.0");
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).to(stringRemotingProperty, stringDoubleConverter);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        stringRemotingProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        stringRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        stringRemotingProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        stringRemotingProperty.set("12.0");
        binding = FXBinder.bind(doubleJavaFXProperty).to(stringRemotingProperty, stringDoubleConverter);
        assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        stringRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        stringRemotingProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        stringRemotingProperty.set("47.0");
        binding = FXBinder.bind(writableDoubleValue).to(stringRemotingProperty, stringDoubleConverter);
        assertEquals(writableDoubleValue.get(), 47.0, EPSILON);
        stringRemotingProperty.set("100.0");
        assertEquals(writableDoubleValue.get(), 100.0, EPSILON);
        stringRemotingProperty.set(null);
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
        binding.unbind();
        stringRemotingProperty.set("100.0");
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleBidirectionalWithConverter() {
        Property<String> stringRemotingProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);
        Converter<Double, String> doubleStringConverter = d -> d == null ? null : d.toString();
        BidirectionalConverter<String, Double> doubleBidirectionalConverter = new DefaultBidirectionalConverter<>(stringDoubleConverter, doubleStringConverter);

        stringRemotingProperty.set("47.0");
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(stringRemotingProperty, doubleBidirectionalConverter);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        stringRemotingProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        stringRemotingProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        assertEquals(stringRemotingProperty.get(), "12.0");
        doubleJavaFXProperty.setValue(null);
        assertEquals(stringRemotingProperty.get(), "0.0");

        binding.unbind();
        stringRemotingProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXBooleanUnidirectional() {
        Property<Boolean> booleanRemotingProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();

        booleanRemotingProperty.set(true);
        assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).to(booleanRemotingProperty);
        assertEquals(booleanJavaFXProperty.get(), true);
        booleanRemotingProperty.set(false);
        assertEquals(booleanJavaFXProperty.get(), false);
        booleanRemotingProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);
        binding.unbind();
        booleanRemotingProperty.set(true);
        assertEquals(booleanJavaFXProperty.get(), false);


        binding = FXBinder.bind(writableBooleanValue).to(booleanRemotingProperty);
        assertEquals(writableBooleanValue.get(), true);
        booleanRemotingProperty.set(false);
        assertEquals(writableBooleanValue.get(), false);
        booleanRemotingProperty.set(null);
        assertEquals(writableBooleanValue.get(), false);
        binding.unbind();
        booleanRemotingProperty.set(true);
        assertEquals(writableBooleanValue.get(), false);
    }

    @Test
    public void testJavaFXBooleanUnidirectionalWithConverter() {
        Property<String> stringRemotingProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);

        stringRemotingProperty.set("Hello");
        assertEquals(booleanJavaFXProperty.get(), false);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).to(stringRemotingProperty, stringBooleanConverter);
        assertEquals(booleanJavaFXProperty.get(), false);
        stringRemotingProperty.set("true");
        assertEquals(booleanJavaFXProperty.get(), true);
        stringRemotingProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);
        binding.unbind();
        stringRemotingProperty.set("true");
        assertEquals(booleanJavaFXProperty.get(), false);

        stringRemotingProperty.set("false");
        binding = FXBinder.bind(writableBooleanValue).to(stringRemotingProperty, stringBooleanConverter);
        assertEquals(writableBooleanValue.get(), false);
        stringRemotingProperty.set("true");
        assertEquals(writableBooleanValue.get(), true);
        stringRemotingProperty.set(null);
        assertEquals(writableBooleanValue.get(), false);
        binding.unbind();
        stringRemotingProperty.set("true");
        assertEquals(writableBooleanValue.get(), false);
    }

    @Test
    public void testJavaFXBooleanBidirectional() {
        Property<Boolean> booleanRemotingProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();

        booleanRemotingProperty.set(true);
        assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(booleanRemotingProperty);
        assertEquals(booleanJavaFXProperty.get(), true);
        booleanRemotingProperty.set(false);
        assertEquals(booleanJavaFXProperty.get(), false);
        booleanRemotingProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);


        booleanJavaFXProperty.set(true);
        assertEquals(booleanRemotingProperty.get().booleanValue(), true);

        booleanJavaFXProperty.setValue(null);
        assertEquals(booleanRemotingProperty.get().booleanValue(), false);

        binding.unbind();
        booleanRemotingProperty.set(true);
        assertEquals(booleanJavaFXProperty.get(), false);
    }

    @Test
    public void testJavaFXBooleanBidirectionalWithConverter() {
        Property<String> stringRemotingProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        Converter<Boolean, String> booleanStringConverter = b -> b == null ? null : b.toString();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);
        BidirectionalConverter<Boolean, String> booleanStringBidirectionalConverter = new DefaultBidirectionalConverter<>(booleanStringConverter, stringBooleanConverter);


        stringRemotingProperty.set("true");
        assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(stringRemotingProperty, booleanStringBidirectionalConverter.invert());
        assertEquals(booleanJavaFXProperty.get(), true);
        stringRemotingProperty.set("false");
        assertEquals(booleanJavaFXProperty.get(), false);
        stringRemotingProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);


        booleanJavaFXProperty.set(true);
        assertEquals(stringRemotingProperty.get(), "true");

        booleanJavaFXProperty.setValue(null);
        assertEquals(stringRemotingProperty.get(), "false");

        binding.unbind();
        stringRemotingProperty.set("true");
        assertEquals(booleanJavaFXProperty.get(), false);
    }

    @Test
    public void testJavaFXStringUnidirectional() {
        Property<String> stringRemotingProperty = new MockedProperty<>();
        StringProperty stringJavaFXProperty = new SimpleStringProperty();
        WritableStringValue writableStringValue = new SimpleStringProperty();

        stringRemotingProperty.set("Hello");
        assertNotEquals(stringJavaFXProperty.get(), "Hello");

        Binding binding = FXBinder.bind(stringJavaFXProperty).to(stringRemotingProperty);
        assertEquals(stringJavaFXProperty.get(), "Hello");
        stringRemotingProperty.set("Hello JavaFX");
        assertEquals(stringJavaFXProperty.get(), "Hello JavaFX");
        stringRemotingProperty.set(null);
        assertEquals(stringJavaFXProperty.get(), null);
        binding.unbind();
        stringRemotingProperty.set("Hello JavaFX");
        assertEquals(stringJavaFXProperty.get(), null);


        binding = FXBinder.bind(writableStringValue).to(stringRemotingProperty);
        assertEquals(writableStringValue.get(), "Hello JavaFX");
        stringRemotingProperty.set("Rico Platform");
        assertEquals(writableStringValue.get(), "Rico Platform");
        stringRemotingProperty.set(null);
        assertEquals(writableStringValue.get(), null);
        binding.unbind();
        stringRemotingProperty.set("Rico Platform");
        assertEquals(writableStringValue.get(), null);
    }

    @Test
    public void testJavaFXStringBidirectional() {
        Property<String> stringRemotingProperty = new MockedProperty<>();
        StringProperty stringJavaFXProperty = new SimpleStringProperty();

        stringRemotingProperty.set("Hello");
        assertNotEquals(stringJavaFXProperty.get(), "Hello");

        Binding binding = FXBinder.bind(stringJavaFXProperty).bidirectionalTo(stringRemotingProperty);
        assertEquals(stringJavaFXProperty.get(), "Hello");
        stringRemotingProperty.set("Hello World");
        assertEquals(stringJavaFXProperty.get(), "Hello World");
        stringRemotingProperty.set(null);
        assertEquals(stringJavaFXProperty.get(), null);


        stringJavaFXProperty.set("Hello from JavaFX");
        assertEquals(stringRemotingProperty.get(), "Hello from JavaFX");

        stringJavaFXProperty.setValue(null);
        assertEquals(stringRemotingProperty.get(), null);

        binding.unbind();
        stringRemotingProperty.set("Hello Rico");
        assertEquals(stringJavaFXProperty.get(), null);
    }

    @Test
    public void testJavaFXStringBidirectionalWithConverter() {
        Property<Double> doubleRemotingProperty = new MockedProperty<>();
        StringProperty stringJavaFXProperty = new SimpleStringProperty();
        Converter<String, Double> doubleStringConverter = s -> s == null ? null : Double.parseDouble(s);
        Converter<Double, String> stringDoubleConverter = d -> d == null ? null : d.toString();
        BidirectionalConverter<Double, String> doubleStringBidirectionalConverter = new DefaultBidirectionalConverter<>(stringDoubleConverter, doubleStringConverter);


        doubleRemotingProperty.set(0.1);
        assertNotEquals(stringJavaFXProperty.get(), "0.1");

        Binding binding = FXBinder.bind(stringJavaFXProperty).bidirectionalTo(doubleRemotingProperty, doubleStringBidirectionalConverter);
        assertEquals(stringJavaFXProperty.get(), "0.1");

        doubleRemotingProperty.set(0.2);
        assertEquals(stringJavaFXProperty.get(), "0.2");

        doubleRemotingProperty.set(null);
        assertEquals(stringJavaFXProperty.get(), null);

        stringJavaFXProperty.set("0.1");
        assertEquals(doubleRemotingProperty.get(), 0.1);

        stringJavaFXProperty.setValue("0.2");
        assertEquals(doubleRemotingProperty.get(), 0.2);

        binding.unbind();
        doubleRemotingProperty.set(0.3);
        assertEquals(stringJavaFXProperty.get(), "0.2");
    }

    @Test
    public void testJavaFXIntegerUnidirectional() {
        Property<Integer> integerRemotingProperty = new MockedProperty<>();
        Property<Number> numberRemotingProperty = new MockedProperty<>();
        IntegerProperty integerJavaFXProperty = new SimpleIntegerProperty();
        WritableIntegerValue writableIntegerValue = new SimpleIntegerProperty();

        integerRemotingProperty.set(47);
        assertNotEquals(integerJavaFXProperty.doubleValue(), 47);

        Binding binding = FXBinder.bind(integerJavaFXProperty).to(integerRemotingProperty);
        assertEquals(integerJavaFXProperty.get(), 47);
        integerRemotingProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 100);
        integerRemotingProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);
        binding.unbind();
        integerRemotingProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);


        numberRemotingProperty.set(12);
        binding = FXBinder.bind(integerJavaFXProperty).to(numberRemotingProperty);
        assertEquals(integerJavaFXProperty.get(), 12);
        numberRemotingProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);
        binding.unbind();
        numberRemotingProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);

        integerRemotingProperty.set(47);
        binding = FXBinder.bind(writableIntegerValue).to(integerRemotingProperty);
        assertEquals(writableIntegerValue.get(), 47);
        integerRemotingProperty.set(100);
        assertEquals(writableIntegerValue.get(), 100);
        integerRemotingProperty.set(null);
        assertEquals(writableIntegerValue.get(), 0);
        binding.unbind();
        integerRemotingProperty.set(100);
        assertEquals(writableIntegerValue.get(), 0);
    }

    @Test
    public void testJavaFXIntegerBidirectional() {
        Property<Integer> integerRemotingProperty = new MockedProperty<>();
        Property<Number> numberRemotingProperty = new MockedProperty<>();
        IntegerProperty integerJavaFXProperty = new SimpleIntegerProperty();

        integerRemotingProperty.set(47);
        assertNotEquals(integerJavaFXProperty.get(), 47);

        Binding binding = FXBinder.bind(integerJavaFXProperty).bidirectionalToNumeric(integerRemotingProperty);
        assertEquals(integerJavaFXProperty.get(), 47);
        integerRemotingProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 100);
        integerRemotingProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);

        integerJavaFXProperty.set(12);
        assertEquals(integerRemotingProperty.get().intValue(), 12);
        integerJavaFXProperty.setValue(null);
        assertEquals(integerRemotingProperty.get().intValue(), 0);

        binding.unbind();
        integerRemotingProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);


        numberRemotingProperty.set(12);
        binding = FXBinder.bind(integerJavaFXProperty).bidirectionalTo(numberRemotingProperty);
        assertEquals(integerJavaFXProperty.get(), 12);
        numberRemotingProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);

        integerJavaFXProperty.set(12);
        assertEquals(numberRemotingProperty.get().intValue(), 12);
        integerJavaFXProperty.setValue(null);
        assertEquals(numberRemotingProperty.get().intValue(), 0);

        binding.unbind();
        numberRemotingProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);
    }

    @Test
    public void testUnidirectionalChain() {
        Property<String> stringRemotingProperty1 = new MockedProperty<>();
        StringProperty stringJavaFXProperty1 = new SimpleStringProperty();
        Property<String> stringRemotingProperty2 = new MockedProperty<>();
        StringProperty stringJavaFXProperty2 = new SimpleStringProperty();

        Binding binding1 = FXBinder.bind(stringRemotingProperty1).to(stringJavaFXProperty1);
        Binding binding2 = FXBinder.bind(stringJavaFXProperty2).to(stringRemotingProperty1);
        Binding binding3 = FXBinder.bind(stringRemotingProperty2).to(stringJavaFXProperty2);

        stringJavaFXProperty1.setValue("Hello");

        assertEquals(stringRemotingProperty1.get(), "Hello");
        assertEquals(stringRemotingProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        binding2.unbind();

        stringJavaFXProperty1.setValue("Hello World");

        assertEquals(stringRemotingProperty1.get(), "Hello World");
        assertEquals(stringRemotingProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello World");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        binding1.unbind();
        binding3.unbind();
    }

    @Test
    public void testBidirectionalChain() {
        Property<String> stringRemotingProperty1 = new MockedProperty<>();
        StringProperty stringJavaFXProperty1 = new SimpleStringProperty();
        Property<String> stringRemotingProperty2 = new MockedProperty<>();
        StringProperty stringJavaFXProperty2 = new SimpleStringProperty();

        Binding binding1 = FXBinder.bind(stringRemotingProperty1).bidirectionalTo(stringJavaFXProperty1);
        Binding binding2 = FXBinder.bind(stringJavaFXProperty2).bidirectionalTo(stringRemotingProperty1);
        Binding binding3 = FXBinder.bind(stringRemotingProperty2).bidirectionalTo(stringJavaFXProperty2);

        stringJavaFXProperty1.setValue("Hello");
        assertEquals(stringRemotingProperty1.get(), "Hello");
        assertEquals(stringRemotingProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        stringRemotingProperty1.set("Hello World");
        assertEquals(stringRemotingProperty1.get(), "Hello World");
        assertEquals(stringRemotingProperty2.get(), "Hello World");
        assertEquals(stringJavaFXProperty1.get(), "Hello World");
        assertEquals(stringJavaFXProperty2.get(), "Hello World");

        stringJavaFXProperty2.setValue("Hello");
        assertEquals(stringRemotingProperty1.get(), "Hello");
        assertEquals(stringRemotingProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        stringRemotingProperty2.set("Hello World");
        assertEquals(stringRemotingProperty1.get(), "Hello World");
        assertEquals(stringRemotingProperty2.get(), "Hello World");
        assertEquals(stringJavaFXProperty1.get(), "Hello World");
        assertEquals(stringJavaFXProperty2.get(), "Hello World");

        binding2.unbind();

        stringJavaFXProperty1.setValue("Hello");
        assertEquals(stringRemotingProperty1.get(), "Hello");
        assertEquals(stringRemotingProperty2.get(), "Hello World");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello World");

        binding1.unbind();
        binding3.unbind();
    }

    @Test
    public void testListBinding() {
        ObservableList<String> remotingList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        Binding binding = FXBinder.bind(javaFXList).to(remotingList);

        assertEquals(remotingList.size(), 0);
        assertEquals(javaFXList.size(), 0);

        remotingList.add("Hello");

        assertEquals(remotingList.size(), 1);
        assertEquals(javaFXList.size(), 1);
        assertTrue(remotingList.contains("Hello"));
        assertTrue(javaFXList.contains("Hello"));

        remotingList.add("World");
        remotingList.add("Rico");

        assertEquals(remotingList.size(), 3);
        assertEquals(javaFXList.size(), 3);
        assertEquals(remotingList.indexOf("Hello"), 0);
        assertEquals(remotingList.indexOf("World"), 1);
        assertEquals(remotingList.indexOf("Rico"), 2);
        assertEquals(javaFXList.indexOf("Hello"), 0);
        assertEquals(javaFXList.indexOf("World"), 1);
        assertEquals(javaFXList.indexOf("Rico"), 2);

        remotingList.clear();

        assertEquals(remotingList.size(), 0);
        assertEquals(javaFXList.size(), 0);

        remotingList.add("Java");

        assertEquals(remotingList.size(), 1);
        assertEquals(javaFXList.size(), 1);
        assertTrue(remotingList.contains("Java"));
        assertTrue(javaFXList.contains("Java"));


        binding.unbind();

        assertEquals(remotingList.size(), 1);
        assertEquals(javaFXList.size(), 1);
        assertTrue(remotingList.contains("Java"));
        assertTrue(javaFXList.contains("Java"));

        remotingList.add("Duke");

        assertEquals(remotingList.size(), 2);
        assertEquals(javaFXList.size(), 1);
        assertTrue(remotingList.contains("Java"));
        assertTrue(remotingList.contains("Duke"));
        assertTrue(javaFXList.contains("Java"));

        FXBinder.bind(javaFXList).to(remotingList);

        remotingList.clear();
        assertEquals(remotingList.size(), 0);
        assertEquals(javaFXList.size(), 0);

        Runnable check = () -> {
            assertEquals(remotingList.size(), 4);
            assertEquals(javaFXList.size(), 4);
            assertEquals(remotingList.indexOf("A"), 0);
            assertEquals(remotingList.indexOf("B"), 1);
            assertEquals(remotingList.indexOf("C"), 2);
            assertEquals(remotingList.indexOf("D"), 3);
            assertEquals(javaFXList.indexOf("A"), 0);
            assertEquals(javaFXList.indexOf("B"), 1);
            assertEquals(javaFXList.indexOf("C"), 2);
            assertEquals(javaFXList.indexOf("D"), 3);
        };

        //add first
        remotingList.clear();
        remotingList.addAll(Arrays.asList("B", "C", "D"));
        remotingList.add(0, "A");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("C", "D"));
        remotingList.add(0, "A");
        remotingList.add(1, "B");
        check.run();

        //add any
        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "D"));
        remotingList.add(2, "C");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "D"));
        remotingList.add(1, "B");
        remotingList.add(2, "C");
        check.run();

        //add last
        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C"));
        remotingList.add(3, "D");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B"));
        remotingList.add(2, "C");
        remotingList.add(3, "D");
        check.run();

        //removePresentationModel first
        remotingList.clear();
        remotingList.addAll(Arrays.asList("X", "A", "B", "C", "D"));
        remotingList.remove(0);
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("X", "A", "B", "C", "D"));
        remotingList.remove("X");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("X1", "X2", "A", "B", "C", "D"));
        remotingList.remove(0);
        remotingList.remove(0);
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("X1", "X2","A", "B", "C", "D"));
        remotingList.remove("X1");
        remotingList.remove("X2");
        check.run();

        //removePresentationModel any
        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "X", "C", "D"));
        remotingList.remove(2);
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "X", "C", "D"));
        remotingList.remove("X");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "X1", "X2", "C", "D"));
        remotingList.remove(2);
        remotingList.remove(2);
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "X1", "X2", "C", "D"));
        remotingList.remove("X1");
        remotingList.remove("X2");
        check.run();

        //removePresentationModel last
        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C", "D", "X"));
        remotingList.remove(4);
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C", "D", "X"));
        remotingList.remove("X");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C", "D", "X1", "X2"));
        remotingList.remove(5);
        remotingList.remove(4);
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C", "D", "X1", "X2"));
        remotingList.remove("X1");
        remotingList.remove("X2");
        check.run();


        //set first
        remotingList.clear();
        remotingList.addAll(Arrays.asList("X", "B", "C", "D"));
        remotingList.set(0, "A");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("X1", "X2", "C", "D"));
        remotingList.set(0, "A");
        remotingList.set(1, "B");
        check.run();

        //set any
        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "X", "D"));
        remotingList.set(2, "C");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "X1", "X2", "D"));
        remotingList.set(1, "B");
        remotingList.set(2, "C");
        check.run();

        //set last
        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C", "X"));
        remotingList.set(3, "D");
        check.run();

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "X1", "X2"));
        remotingList.set(2, "C");
        remotingList.set(3, "D");
        check.run();






        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C", "X1", "X2", "X3", "D"));
        remotingList.remove("X1");
        remotingList.remove("X2");
        remotingList.remove("X3");
        assertEquals(remotingList.size(), 4);
        assertEquals(javaFXList.size(), 4);
        assertEquals(remotingList.indexOf("A"), 0);
        assertEquals(remotingList.indexOf("B"), 1);
        assertEquals(remotingList.indexOf("C"), 2);
        assertEquals(remotingList.indexOf("D"), 3);
        assertEquals(javaFXList.indexOf("A"), 0);
        assertEquals(javaFXList.indexOf("B"), 1);
        assertEquals(javaFXList.indexOf("C"), 2);
        assertEquals(javaFXList.indexOf("D"), 3);

        remotingList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "F", "D"));
        remotingList.set(2, "C");
        assertEquals(remotingList.size(), 4);
        assertEquals(javaFXList.size(), 4);
        assertEquals(remotingList.indexOf("A"), 0);
        assertEquals(remotingList.indexOf("B"), 1);
        assertEquals(remotingList.indexOf("C"), 2);
        assertEquals(remotingList.indexOf("D"), 3);
        assertEquals(javaFXList.indexOf("A"), 0);
        assertEquals(javaFXList.indexOf("B"), 1);
        assertEquals(javaFXList.indexOf("C"), 2);
        assertEquals(javaFXList.indexOf("D"), 3);
    }

    @Test
    public void severalBindings() {
        ObservableList<String> remotingList1 = new ObservableArrayList<>();
        ObservableList<String> remotingList2 = new ObservableArrayList<>();
        ObservableList<String> remotingList3 = new ObservableArrayList<>();
        ObservableList<String> remotingList4 = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList1 = FXCollections.observableArrayList();
        javafx.collections.ObservableList<String> javaFXList2 = FXCollections.observableArrayList();
        javafx.collections.ObservableList<String> javaFXList3 = FXCollections.observableArrayList();
        javafx.collections.ObservableList<String> javaFXList4 = FXCollections.observableArrayList();

        Binding binding1 = FXBinder.bind(javaFXList1).to(remotingList1);
        Binding binding2 = FXBinder.bind(javaFXList2).to(remotingList2);
        Binding binding3 = FXBinder.bind(javaFXList3).to(remotingList3);
        Binding binding4 = FXBinder.bind(javaFXList4).to(remotingList4);

        binding1.unbind();
        binding2.unbind();

        binding1 = FXBinder.bind(javaFXList1).to(remotingList2);
        binding2 = FXBinder.bind(javaFXList2).to(remotingList1);

        binding3.unbind();
        binding4.unbind();

        binding3 = FXBinder.bind(javaFXList3).to(remotingList4);
        binding4 = FXBinder.bind(javaFXList4).to(remotingList3);

        binding1.unbind();
        binding2.unbind();
        binding3.unbind();
        binding4.unbind();

        binding1 = FXBinder.bind(javaFXList1).to(remotingList4);
        binding2 = FXBinder.bind(javaFXList2).to(remotingList3);
        binding3 = FXBinder.bind(javaFXList3).to(remotingList2);
        binding4 = FXBinder.bind(javaFXList4).to(remotingList1);

        binding1.unbind();
        binding2.unbind();
        binding3.unbind();
        binding4.unbind();
    }


    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testErrorOnMultipleListBinding() {
        ObservableList<String> remotingList = new ObservableArrayList<>();
        ObservableList<String> remotingList2 = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        FXBinder.bind(javaFXList).to(remotingList);
        FXBinder.bind(javaFXList).to(remotingList2);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testModifyBoundList() throws Throwable {
        ObservableList<String> remotingList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        //Sadly the JavaFX collection classes catch exceptions and pass them to the uncaught exception handler
        Thread.UncaughtExceptionHandler defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        List<Throwable> thrownExceptions = new ArrayList<>();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> thrownExceptions.add(e));
        FXBinder.bind(javaFXList).to(remotingList);
        javaFXList.add("BAD");
        Thread.setDefaultUncaughtExceptionHandler(defaultExceptionHandler);

        //Sadly the new element is in the list. This is done by JavaFX and we can't change it
        assertEquals(javaFXList.size(), 1);

        assertEquals(thrownExceptions.size(), 1);
        throw thrownExceptions.get(0);
    }

    @Test
    public void testCorrectUnbind() {
        ObservableList<String> remotingList = new ObservableArrayList<>();
        ObservableList<String> remotingList2 = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        Binding binding = FXBinder.bind(javaFXList).to(remotingList);

        remotingList.add("Foo");

        assertEquals(remotingList.size(), 1);
        assertEquals(remotingList2.size(), 0);
        assertEquals(javaFXList.size(), 1);

        binding.unbind();
        FXBinder.bind(javaFXList).to(remotingList2);

        assertEquals(remotingList.size(), 1);
        assertEquals(remotingList2.size(), 0);
        assertEquals(javaFXList.size(), 0);

        remotingList2.add("Foo");
        remotingList2.add("Bar");

        assertEquals(remotingList.size(), 1);
        assertEquals(remotingList2.size(), 2);
        assertEquals(javaFXList.size(), 2);
    }

    @Test
    public void testConvertedListBinding() {
        ObservableList<Boolean> remotingList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        Binding binding = FXBinder.bind(javaFXList).to(remotingList, value -> value.toString());

        remotingList.add(true);

        assertEquals(remotingList.size(), 1);
        assertEquals(javaFXList.size(), 1);

        assertEquals(javaFXList.get(0), "true");

    }

    @Test
    public void testSeveralBinds() {
        ObservableList<String> remotingList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        for (int i = 0; i < 10; i++) {
            Binding binding = FXBinder.bind(javaFXList).to(remotingList);
            binding.unbind();
        }

        remotingList.addAll(Arrays.asList("A", "B", "C"));
        for (int i = 0; i < 10; i++) {
            Binding binding = FXBinder.bind(javaFXList).to(remotingList);
            binding.unbind();
        }
    }

    @Test
    public void testListBindingWithNonEmptyLists() {
        ObservableList<String> remotingList = new ObservableArrayList<>();

        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        remotingList.addAll(Arrays.asList("A", "B", "C"));
        Binding binding1 = FXBinder.bind(javaFXList).to(remotingList);
        assertEquals(remotingList.size(), 3);
        assertEquals(javaFXList.size(), 3);
        assertTrue(remotingList.contains("A"));
        assertTrue(remotingList.contains("B"));
        assertTrue(remotingList.contains("C"));
        assertTrue(javaFXList.contains("A"));
        assertTrue(javaFXList.contains("B"));
        assertTrue(javaFXList.contains("C"));

        binding1.unbind();

        remotingList.clear();
        javaFXList.clear();
        javaFXList.addAll("A", "B", "C");
        Binding binding2 = FXBinder.bind(javaFXList).to(remotingList);
        assertEquals(remotingList.size(), 0);
        assertEquals(javaFXList.size(), 0);


        binding2.unbind();

        remotingList.clear();
        javaFXList.clear();
        remotingList.addAll(Arrays.asList("A", "B", "C"));
        javaFXList.addAll("D", "E", "F");
        FXBinder.bind(javaFXList).to(remotingList);
        assertEquals(remotingList.size(), 3);
        assertEquals(javaFXList.size(), 3);
        assertTrue(remotingList.contains("A"));
        assertTrue(remotingList.contains("B"));
        assertTrue(remotingList.contains("C"));
        assertTrue(javaFXList.contains("A"));
        assertTrue(javaFXList.contains("B"));
        assertTrue(javaFXList.contains("C"));
    }
}
