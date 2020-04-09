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
package dev.rico.integrationtests.server.remoting.value;

import dev.rico.integrationtests.remoting.value.AllValueTypesTestControllerModel;
import dev.rico.integrationtests.remoting.value.ValueTestConstants;
import dev.rico.remoting.server.RemotingController;
import dev.rico.remoting.server.RemotingModel;
import dev.rico.remoting.server.RemotingValue;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;

@RemotingController(ValueTestConstants.BY_FIELD_NAME_DEFINED_VALUE_TYPES_CONTROLLER)
public class ByFieldNameDefinedValueTypesTestController {

    @RemotingValue
    private BigDecimal bigDecimalValue;

    @RemotingValue
    private BigInteger bigIntegerValue;

    @RemotingValue
    private boolean primitiveBooleanValue;

    @RemotingValue
    private Boolean booleanValue;

    @RemotingValue
    private byte primitiveByteValue;

    @RemotingValue
    private Byte byteValue;

    @RemotingValue
    private char primitiveCharacterValue;

    @RemotingValue
    private Character characterValue;

    @RemotingValue
    private double primitiveDoubleValue;

    @RemotingValue
    private Double doubleValue;

    @RemotingValue
    private float primitiveFloatValue;

    @RemotingValue
    private Float floatValue;

    @RemotingValue
    private int primitiveIntegerValue;

    @RemotingValue
    private Integer integerValue;

    @RemotingValue
    private long primitiveLongValue;

    @RemotingValue
    private Long longValue;

    @RemotingValue
    private short primitiveShortValue;

    @RemotingValue
    private Short shortValue;

    @RemotingValue
    private String stringValue;

    @RemotingModel
    private AllValueTypesTestControllerModel model;

    @PostConstruct
    private void init() {
        model.bigDecimalValue().set(bigDecimalValue);
        model.bigIntegerValue().set(bigIntegerValue);

        model.booleanValue().set(booleanValue);
        model.primitiveBooleanValue().set(primitiveBooleanValue);

        model.byteValue().set(byteValue);
        model.primitiveByteValue().set(primitiveByteValue);

        model.characterValue().set(characterValue);
        model.primitiveCharacterValue().set(primitiveCharacterValue);

        model.doubleValue().set(doubleValue);
        model.primitiveDoubleValue().set(primitiveDoubleValue);

        model.floatValue().set(floatValue);
        model.primitiveFloatValue().set(primitiveFloatValue);

        model.integerValue().set(integerValue);
        model.primitiveIntegerValue().set(primitiveIntegerValue);

        model.longValue().set(longValue);
        model.primitiveLongValue().set(primitiveLongValue);

        model.shortValue().set(shortValue);
        model.primitiveShortValue().set(primitiveShortValue);

        model.stringValue().set(stringValue);
    }
}
