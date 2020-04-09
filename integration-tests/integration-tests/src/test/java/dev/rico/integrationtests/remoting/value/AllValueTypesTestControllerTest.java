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

import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.ControllerProxy;
import dev.rico.integrationtests.remoting.AbstractRemotingIntegrationTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static dev.rico.integrationtests.remoting.value.ValueTestConstants.ALL_VALUE_TYPES_CONTROLLER;

public class AllValueTypesTestControllerTest extends AbstractRemotingIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCreationWithAllParameters(final String containerType, final String endpoint) throws ExecutionException, InterruptedException {
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
        final ClientContext context = connect(endpoint);
        final ControllerProxy<AllValueTypesTestControllerModel> controller = createController(context, ALL_VALUE_TYPES_CONTROLLER, parameters);
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

        controller.destroy().get();
        context.disconnect().get();
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCreationWithPrimitiveParameters(final String containerType, final String endpoint) throws ExecutionException, InterruptedException {
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
        final ClientContext context = connect(endpoint);
        final ControllerProxy<AllValueTypesTestControllerModel> controller = createController(context, ALL_VALUE_TYPES_CONTROLLER, parameters);
        final AllValueTypesTestControllerModel model = controller.getModel();

        //then:
        Assert.assertNull(model.bigDecimalValue().get());
        Assert.assertNull(model.bigIntegerValue().get());
        Assert.assertNull(model.booleanValue().get());
        Assert.assertNull(model.byteValue().get());
        Assert.assertNull(model.characterValue().get());
        Assert.assertNull(model.doubleValue().get());
        Assert.assertNull(model.floatValue().get());
        Assert.assertNull(model.integerValue().get());
        Assert.assertNull(model.longValue().get());
        Assert.assertNull(model.shortValue().get());
        Assert.assertNull(model.stringValue().get());
        Assert.assertEquals(model.primitiveBooleanValue().get().booleanValue(), true);
        Assert.assertEquals(model.primitiveByteValue().get().byteValue(), (byte) 1);
        Assert.assertEquals(model.primitiveCharacterValue().get().charValue(), 'a');
        Assert.assertEquals(model.primitiveDoubleValue().get().doubleValue(), 0.1d);
        Assert.assertEquals(model.primitiveFloatValue().get().floatValue(), 0.1f);
        Assert.assertEquals(model.primitiveIntegerValue().get().intValue(), 1);
        Assert.assertEquals(model.primitiveLongValue().get().longValue(), 100l);
        Assert.assertEquals(model.primitiveShortValue().get().shortValue(), (short) 1);

        controller.destroy().get();
        context.disconnect().get();
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCreationWithEmptyParameters(final String containerType, final String endpoint) throws ExecutionException, InterruptedException {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();

        //when:
        final ClientContext context = connect(endpoint);
        final ControllerProxy<AllValueTypesTestControllerModel> controller = createController(context, ALL_VALUE_TYPES_CONTROLLER, parameters);
        final AllValueTypesTestControllerModel model = controller.getModel();

        //then:
        Assert.assertNull(model.bigDecimalValue().get());
        Assert.assertNull(model.bigIntegerValue().get());
        Assert.assertNull(model.booleanValue().get());
        Assert.assertNull(model.byteValue().get());
        Assert.assertNull(model.characterValue().get());
        Assert.assertNull(model.doubleValue().get());
        Assert.assertNull(model.floatValue().get());
        Assert.assertNull(model.integerValue().get());
        Assert.assertNull(model.longValue().get());
        Assert.assertNull(model.shortValue().get());
        Assert.assertNull(model.stringValue().get());

        //DEFAULT VALUES FOR PRIMITIVES
        Assert.assertEquals(model.primitiveBooleanValue().get().booleanValue(), false);
        Assert.assertEquals(model.primitiveByteValue().get().byteValue(), (byte) 0);
        Assert.assertEquals(model.primitiveCharacterValue().get().charValue(), '\u0000');
        Assert.assertEquals(model.primitiveDoubleValue().get().doubleValue(), 0.0d);
        Assert.assertEquals(model.primitiveFloatValue().get().floatValue(), 0.0f);
        Assert.assertEquals(model.primitiveIntegerValue().get().intValue(), 0);
        Assert.assertEquals(model.primitiveLongValue().get().longValue(), 0l);
        Assert.assertEquals(model.primitiveShortValue().get().shortValue(), (short) 0);

        controller.destroy().get();
        context.disconnect().get();
    }

    //Currently we do not handle controller creation errors on the client.
    // Therefore this test can not be handled
    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, enabled = false)
    public void testCreationWithWrongTypedParameters(final String containerType, final String endpoint) throws ExecutionException, InterruptedException {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();
        parameters.put(ValueTestConstants.PRIMITIVE_BOOLEAN_VALUE, "Hello");

        //when:
        final ClientContext context = connect(endpoint);
        createController(context, ALL_VALUE_TYPES_CONTROLLER, parameters);

        context.disconnect().get();
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testCreationWithoutParameters(final String containerType, final String endpoint) throws ExecutionException, InterruptedException {
        //when:
        final ClientContext context = connect(endpoint);
        final ControllerProxy<AllValueTypesTestControllerModel> controller = createController(context, ALL_VALUE_TYPES_CONTROLLER);
        final AllValueTypesTestControllerModel model = controller.getModel();

        //then:
        Assert.assertNull(model.bigDecimalValue().get());
        Assert.assertNull(model.bigIntegerValue().get());
        Assert.assertNull(model.booleanValue().get());
        Assert.assertNull(model.byteValue().get());
        Assert.assertNull(model.characterValue().get());
        Assert.assertNull(model.doubleValue().get());
        Assert.assertNull(model.floatValue().get());
        Assert.assertNull(model.integerValue().get());
        Assert.assertNull(model.longValue().get());
        Assert.assertNull(model.shortValue().get());
        Assert.assertNull(model.stringValue().get());

        //DEFAULT VALUES FOR PRIMITIVES
        Assert.assertEquals(model.primitiveBooleanValue().get().booleanValue(), false);
        Assert.assertEquals(model.primitiveByteValue().get().byteValue(), (byte) 0);
        Assert.assertEquals(model.primitiveCharacterValue().get().charValue(), '\u0000');
        Assert.assertEquals(model.primitiveDoubleValue().get().doubleValue(), 0.0d);
        Assert.assertEquals(model.primitiveFloatValue().get().floatValue(), 0.0f);
        Assert.assertEquals(model.primitiveIntegerValue().get().intValue(), 0);
        Assert.assertEquals(model.primitiveLongValue().get().longValue(), 0l);
        Assert.assertEquals(model.primitiveShortValue().get().shortValue(), (short) 0);

        controller.destroy().get();
        context.disconnect().get();
    }
}
