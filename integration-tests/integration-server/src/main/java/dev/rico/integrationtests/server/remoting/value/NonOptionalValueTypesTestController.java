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
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.RemotingValue;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;

import static dev.rico.integrationtests.remoting.value.ValueTestConstants.BIG_DECIMAL_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.BIG_INTEGER_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.BOOLEAN_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.BYTE_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.CHARACTER_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.DOUBLE_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.FLOAT_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.INTEGER_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.LONG_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_BOOLEAN_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_BYTE_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_CHARACTER_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_DOUBLE_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_FLOAT_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_INTEGER_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_LONG_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.PRIMITIVE_SHORT_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.SHORT_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.STRING_VALUE;

@RemotingController(ValueTestConstants.NON_OPTIONAL_VALUE_TYPES_CONTROLLER)
public class NonOptionalValueTypesTestController {

    @RemotingValue(value = BIG_DECIMAL_VALUE, optional = false)
    private BigDecimal bigDecimalValue;

    @RemotingValue(value = BIG_INTEGER_VALUE, optional = false)
    private BigInteger bigIntegerValue;

    @RemotingValue(value = PRIMITIVE_BOOLEAN_VALUE, optional = false)
    private boolean primitiveBooleanValue;

    @RemotingValue(value = BOOLEAN_VALUE, optional = false)
    private Boolean booleanValue;

    @RemotingValue(value = PRIMITIVE_BYTE_VALUE, optional = false)
    private byte primitiveByteValue;

    @RemotingValue(value = BYTE_VALUE, optional = false)
    private Byte byteValue;

    @RemotingValue(value = PRIMITIVE_CHARACTER_VALUE, optional = false)
    private char primitiveCharacterValue;

    @RemotingValue(value = CHARACTER_VALUE, optional = false)
    private Character characterValue;

    @RemotingValue(value = PRIMITIVE_DOUBLE_VALUE, optional = false)
    private double primitiveDoubleValue;

    @RemotingValue(value = DOUBLE_VALUE, optional = false)
    private Double doubleValue;

    @RemotingValue(value = PRIMITIVE_FLOAT_VALUE, optional = false)
    private float primitiveFloatValue;

    @RemotingValue(value = FLOAT_VALUE, optional = false)
    private Float floatValue;

    @RemotingValue(value = PRIMITIVE_INTEGER_VALUE, optional = false)
    private int primitiveIntegerValue;

    @RemotingValue(value = INTEGER_VALUE, optional = false)
    private Integer integerValue;

    @RemotingValue(value = PRIMITIVE_LONG_VALUE, optional = false)
    private long primitiveLongValue;

    @RemotingValue(value = LONG_VALUE, optional = false)
    private Long longValue;

    @RemotingValue(value = PRIMITIVE_SHORT_VALUE, optional = false)
    private short primitiveShortValue;

    @RemotingValue(value = SHORT_VALUE, optional = false)
    private Short shortValue;

    @RemotingValue(value = STRING_VALUE, optional = false)
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
