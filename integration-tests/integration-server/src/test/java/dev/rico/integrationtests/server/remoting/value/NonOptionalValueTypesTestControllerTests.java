package dev.rico.integrationtests.server.remoting.value;

import dev.rico.integrationtests.remoting.value.AllValueTypesTestControllerModel;
import dev.rico.integrationtests.remoting.value.ValueTestConstants;
import dev.rico.integrationtests.server.TestConfiguration;
import dev.rico.server.remoting.test.ControllerTestException;
import dev.rico.server.remoting.test.ControllerUnderTest;
import dev.rico.server.remoting.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static dev.rico.integrationtests.remoting.value.ValueTestConstants.NON_OPTIONAL_VALUE_TYPES_CONTROLLER;

@SpringBootTest(classes = TestConfiguration.class)
public class NonOptionalValueTypesTestControllerTests extends SpringTestNGControllerTest {

    @Test
    public void testCreationWithAllParameters() {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();
        parameters.put(ValueTestConstants.BIG_DECIMAL_VALUE, BigDecimal.valueOf(100l));
        parameters.put(ValueTestConstants.BIG_INTEGER_VALUE, BigInteger.valueOf(100l));
        parameters.put(ValueTestConstants.PRIMITIVE_BOOLEAN_VALUE, true);
        parameters.put(ValueTestConstants.BOOLEAN_VALUE, true);
        parameters.put(ValueTestConstants.PRIMITIVE_BYTE_VALUE, (byte) 1);
        parameters.put(ValueTestConstants.BYTE_VALUE, (byte) 1);
        parameters.put(ValueTestConstants.PRIMITIVE_CHARACTER_VALUE, 'a');
        parameters.put(ValueTestConstants.CHARACTER_VALUE, 'a');
        parameters.put(ValueTestConstants.PRIMITIVE_DOUBLE_VALUE, 0.1d);
        parameters.put(ValueTestConstants.DOUBLE_VALUE, 0.1d);
        parameters.put(ValueTestConstants.PRIMITIVE_FLOAT_VALUE, 0.1f);
        parameters.put(ValueTestConstants.FLOAT_VALUE, 0.1f);
        parameters.put(ValueTestConstants.PRIMITIVE_INTEGER_VALUE, 1);
        parameters.put(ValueTestConstants.INTEGER_VALUE, 1);
        parameters.put(ValueTestConstants.PRIMITIVE_LONG_VALUE, 100l);
        parameters.put(ValueTestConstants.LONG_VALUE, 100l);
        parameters.put(ValueTestConstants.PRIMITIVE_SHORT_VALUE, (short) 1);
        parameters.put(ValueTestConstants.SHORT_VALUE, (short) 1);
        parameters.put(ValueTestConstants.STRING_VALUE, "Hello");

        //when:
        final ControllerUnderTest<AllValueTypesTestControllerModel> controller = createController(NON_OPTIONAL_VALUE_TYPES_CONTROLLER, parameters);
        final AllValueTypesTestControllerModel model = controller.getModel();

        //then:
        Assert.assertEquals(model.bigDecimalValue().get(), BigDecimal.valueOf(100l));
        Assert.assertEquals(model.bigIntegerValue().get(), BigInteger.valueOf(100l));
        Assert.assertEquals(model.primitiveBooleanValue().get().booleanValue(), true);
        Assert.assertEquals(model.booleanValue().get().booleanValue(), true);
        Assert.assertEquals(model.primitiveByteValue().get().byteValue(), (byte) 1);
        Assert.assertEquals(model.byteValue().get().byteValue(), (byte) 1);
        Assert.assertEquals(model.primitiveCharacterValue().get().charValue(), 'a');
        Assert.assertEquals(model.characterValue().get().charValue(), 'a');
        Assert.assertEquals(model.primitiveDoubleValue().get().doubleValue(), 0.1d);
        Assert.assertEquals(model.doubleValue().get().doubleValue(), 0.1d);
        Assert.assertEquals(model.primitiveFloatValue().get().floatValue(), 0.1f);
        Assert.assertEquals(model.floatValue().get().floatValue(), 0.1f);
        Assert.assertEquals(model.primitiveIntegerValue().get().intValue(), 1);
        Assert.assertEquals(model.integerValue().get().intValue(), 1);
        Assert.assertEquals(model.primitiveLongValue().get().longValue(), 100l);
        Assert.assertEquals(model.longValue().get().longValue(), 100l);
        Assert.assertEquals(model.primitiveShortValue().get().shortValue(), (short) 1);
        Assert.assertEquals(model.shortValue().get().shortValue(), (short) 1);
        Assert.assertEquals(model.stringValue().get(), "Hello");
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void testCreationWithMissingParameters() {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();
        parameters.put(ValueTestConstants.PRIMITIVE_BOOLEAN_VALUE, true);
        parameters.put(ValueTestConstants.PRIMITIVE_BYTE_VALUE, (byte) 1);
        parameters.put(ValueTestConstants.PRIMITIVE_CHARACTER_VALUE, 'a');
        parameters.put(ValueTestConstants.PRIMITIVE_DOUBLE_VALUE, 0.1d);
        parameters.put(ValueTestConstants.PRIMITIVE_FLOAT_VALUE, 0.1f);
        parameters.put(ValueTestConstants.PRIMITIVE_INTEGER_VALUE, 1);
        parameters.put(ValueTestConstants.PRIMITIVE_LONG_VALUE, 100l);
        parameters.put(ValueTestConstants.PRIMITIVE_SHORT_VALUE, (short) 1);

        //when:
        createController(NON_OPTIONAL_VALUE_TYPES_CONTROLLER, parameters);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void testCreationWithEmptyParameters() {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();

        //when:
        createController(NON_OPTIONAL_VALUE_TYPES_CONTROLLER, parameters);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void testCreationWithWrongTypedParameters() {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();
        parameters.put(ValueTestConstants.PRIMITIVE_BOOLEAN_VALUE, "Hello");

        //when:
        createController(NON_OPTIONAL_VALUE_TYPES_CONTROLLER, parameters);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void testCreationWithoutParameters() {
        //when:
        createController(NON_OPTIONAL_VALUE_TYPES_CONTROLLER);
    }
}

