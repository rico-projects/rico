package dev.rico.integrationtests.server.remoting.value;

import dev.rico.integrationtests.remoting.value.AllValueTypesTestControllerModel;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.RemotingValue;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;

import static dev.rico.integrationtests.remoting.value.ValueTestConstants.ALL_VALUE_TYPES_CONTROLLER;
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

@RemotingController(ALL_VALUE_TYPES_CONTROLLER)
public class AllValueTypesTestController {

    @RemotingValue(BIG_DECIMAL_VALUE)
    private BigDecimal bigDecimalValue;

    @RemotingValue(BIG_INTEGER_VALUE)
    private BigInteger bigIntegerValue;

    @RemotingValue(PRIMITIVE_BOOLEAN_VALUE)
    private boolean primitiveBooleanValue;

    @RemotingValue(BOOLEAN_VALUE)
    private Boolean booleanValue;

    @RemotingValue(PRIMITIVE_BYTE_VALUE)
    private byte primitiveByteValue;

    @RemotingValue(BYTE_VALUE)
    private Byte byteValue;

    @RemotingValue(PRIMITIVE_CHARACTER_VALUE)
    private char primitiveCharacterValue;

    @RemotingValue(CHARACTER_VALUE)
    private Character characterValue;

    @RemotingValue(PRIMITIVE_DOUBLE_VALUE)
    private double primitiveDoubleValue;

    @RemotingValue(DOUBLE_VALUE)
    private Double doubleValue;

    @RemotingValue(PRIMITIVE_FLOAT_VALUE)
    private float primitiveFloatValue;

    @RemotingValue(FLOAT_VALUE)
    private Float floatValue;

    @RemotingValue(PRIMITIVE_INTEGER_VALUE)
    private int primitiveIntegerValue;

    @RemotingValue(INTEGER_VALUE)
    private Integer integerValue;

    @RemotingValue(PRIMITIVE_LONG_VALUE)
    private long primitiveLongValue;

    @RemotingValue(LONG_VALUE)
    private Long longValue;

    @RemotingValue(PRIMITIVE_SHORT_VALUE)
    private short primitiveShortValue;

    @RemotingValue(SHORT_VALUE)
    private Short shortValue;

    @RemotingValue(STRING_VALUE)
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
