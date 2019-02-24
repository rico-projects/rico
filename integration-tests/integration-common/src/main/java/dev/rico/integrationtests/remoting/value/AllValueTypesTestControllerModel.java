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
package dev.rico.integrationtests.remoting.value;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

import java.math.BigDecimal;
import java.math.BigInteger;

@RemotingBean
public class AllValueTypesTestControllerModel {

    private Property<BigDecimal> bigDecimalValue;

    private Property<BigInteger> bigIntegerValue;

    private Property<Boolean> primitiveBooleanValue;

    private Property<Boolean> booleanValue;

    private Property<Byte> primitiveByteValue;

    private Property<Byte> byteValue;

    private Property<Character> primitiveCharacterValue;

    private Property<Character> characterValue;

    private Property<Double> primitiveDoubleValue;

    private Property<Double> doubleValue;

    private Property<Float> primitiveFloatValue;

    private Property<Float> floatValue;

    private Property<Integer> primitiveIntegerValue;

    private Property<Integer> integerValue;

    private Property<Long> primitiveLongValue;

    private Property<Long> longValue;

    private Property<Short> primitiveShortValue;

    private Property<Short> shortValue;

    private Property<String> stringValue;

    public Property<BigDecimal> bigDecimalValue() {
        return bigDecimalValue;
    }

    public Property<BigInteger> bigIntegerValue() {
        return bigIntegerValue;
    }

    public Property<Boolean> primitiveBooleanValue() {
        return primitiveBooleanValue;
    }

    public Property<Boolean> booleanValue() {
        return booleanValue;
    }

    public Property<Byte> primitiveByteValue() {
        return primitiveByteValue;
    }

    public Property<Byte> byteValue() {
        return byteValue;
    }

    public Property<Character> primitiveCharacterValue() {
        return primitiveCharacterValue;
    }

    public Property<Character> characterValue() {
        return characterValue;
    }

    public Property<Double> primitiveDoubleValue() {
        return primitiveDoubleValue;
    }

    public Property<Double> doubleValue() {
        return doubleValue;
    }

    public Property<Float> primitiveFloatValue() {
        return primitiveFloatValue;
    }

    public Property<Float> floatValue() {
        return floatValue;
    }

    public Property<Integer> primitiveIntegerValue() {
        return primitiveIntegerValue;
    }

    public Property<Integer> integerValue() {
        return integerValue;
    }

    public Property<Long> primitiveLongValue() {
        return primitiveLongValue;
    }

    public Property<Long> longValue() {
        return longValue;
    }

    public Property<Short> primitiveShortValue() {
        return primitiveShortValue;
    }

    public Property<Short> shortValue() {
        return shortValue;
    }

    public Property<String> stringValue() {
        return stringValue;
    }
}
