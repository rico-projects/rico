package dev.rico.integrationtests.server.remoting.value;

import dev.rico.integrationtests.remoting.value.AllValueTypesTestControllerModel;
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
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.SHORT_VALUE;
import static dev.rico.integrationtests.remoting.value.ValueTestConstants.STRING_VALUE;

public class OptionalValueTypesTestController {

    @RemotingValue(BIG_DECIMAL_VALUE)
    private BigDecimal bigDecimalValue;

    @RemotingValue(BIG_INTEGER_VALUE)
    private BigInteger bigIntegerValue;

    @RemotingValue(BOOLEAN_VALUE)
    private Boolean booleanValue;

    @RemotingValue(BYTE_VALUE)
    private Byte byteValue;

    @RemotingValue(CHARACTER_VALUE)
    private Character characterValue;

    @RemotingValue(DOUBLE_VALUE)
    private Double doubleValue;

    @RemotingValue(FLOAT_VALUE)
    private Float floatValue;

    @RemotingValue(INTEGER_VALUE)
    private Integer integerValue;

    @RemotingValue(LONG_VALUE)
    private Long longValue;

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
        model.byteValue().set(byteValue);
        model.characterValue().set(characterValue);
        model.doubleValue().set(doubleValue);
        model.floatValue().set(floatValue);
        model.integerValue().set(integerValue);
        model.longValue().set(longValue);
        model.shortValue().set(shortValue);
        model.stringValue().set(stringValue);
    }
}
