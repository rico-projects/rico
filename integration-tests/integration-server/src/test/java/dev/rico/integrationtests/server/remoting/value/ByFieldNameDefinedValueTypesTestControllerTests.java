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
import dev.rico.integrationtests.server.TestConfiguration;
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

import static dev.rico.integrationtests.remoting.value.ValueTestConstants.BY_FIELD_NAME_DEFINED_VALUE_TYPES_CONTROLLER;

@SpringBootTest(classes = TestConfiguration.class)
public class ByFieldNameDefinedValueTypesTestControllerTests extends SpringTestNGControllerTest {

    @Test
    public void testCreationWithAllParameters() {
        //given:
        final Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("bigDecimalValue", BigDecimal.valueOf(100L));
        parameters.put("bigIntegerValue", BigInteger.valueOf(100L));
        parameters.put("primitiveBooleanValue", true);
        parameters.put("booleanValue", true);
        parameters.put("primitiveByteValue", (byte) 1);
        parameters.put("byteValue", (byte) 1);
        parameters.put("primitiveCharacterValue", 'a');
        parameters.put("characterValue", 'a');
        parameters.put("primitiveDoubleValue", 0.1d);
        parameters.put("doubleValue", 0.1d);
        parameters.put("primitiveFloatValue", 0.1f);
        parameters.put("floatValue", 0.1f);
        parameters.put("primitiveIntegerValue", 1);
        parameters.put("integerValue", 1);
        parameters.put("primitiveLongValue", 100L);
        parameters.put("longValue", 100L);
        parameters.put("primitiveShortValue", (short) 1);
        parameters.put("shortValue", (short) 1);
        parameters.put("stringValue", "Hello");

        //when:
        final ControllerUnderTest<AllValueTypesTestControllerModel> controller = createController(BY_FIELD_NAME_DEFINED_VALUE_TYPES_CONTROLLER, parameters);
        final AllValueTypesTestControllerModel model = controller.getModel();

        //then:
        Assert.assertEquals(model.bigDecimalValue().get(), BigDecimal.valueOf(100L));
        Assert.assertEquals(model.bigIntegerValue().get(), BigInteger.valueOf(100L));
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
        Assert.assertEquals(model.primitiveLongValue().get().longValue(), 100L);
        Assert.assertEquals(model.longValue().get().longValue(), 100L);
        Assert.assertEquals(model.primitiveShortValue().get().shortValue(), (short) 1);
        Assert.assertEquals(model.shortValue().get().shortValue(), (short) 1);
        Assert.assertEquals(model.stringValue().get(), "Hello");
    }
}
