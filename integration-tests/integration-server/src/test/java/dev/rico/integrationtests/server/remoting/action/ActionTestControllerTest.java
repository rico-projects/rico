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
package dev.rico.integrationtests.server.remoting.action;

import dev.rico.integrationtests.remoting.action.ActionTestBean;
import dev.rico.integrationtests.server.TestConfiguration;
import dev.rico.client.remoting.Param;
import dev.rico.server.remoting.test.ControllerTestException;
import dev.rico.server.remoting.test.ControllerUnderTest;
import dev.rico.server.remoting.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static dev.rico.integrationtests.remoting.action.ActionTestConstants.*;

@SpringBootTest(classes = TestConfiguration.class)
public class ActionTestControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<ActionTestBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(ACTION_CONTROLLER_NAME);
    }

    @AfterMethod
    public void destroy() {
        controller.destroy();
    }

    @Test
    public void callSimpleMethod() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        controller.invoke(PUBLIC_ACTION);
        Assert.assertTrue(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPrivateMethod() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        controller.invoke(PRIVATE_ACTION);
        Assert.assertTrue(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPrivateMethodWithStringParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value = "Hello endpoint!";
        controller.invoke(PRIVATE_WITH_STRING_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertTrue(controller.getModel().getBooleanValue());
        Assert.assertEquals(controller.getModel().getStringValue(), value);
    }

    @Test
    public void callPrivateMethodWithNullParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        controller.invoke(PRIVATE_WITH_STRING_PARAM_ACTION, Param.of(PARAM_NAME, null));
        Assert.assertTrue(controller.getModel().getBooleanValue());
        Assert.assertNull(controller.getModel().getStringValue());
    }

    @Test
    public void callPublicMethodWithBooleanParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value = "Hello endpoint!";
        controller.invoke(PUBLIC_WITH_BOOLEAN_PARAM_ACTION, Param.of(PARAM_NAME, true));
        Assert.assertTrue(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPublicMethodWithNullParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value = "Hello endpoint!";
        controller.invoke(PUBLIC_WITH_BOOLEAN_PARAM_ACTION, Param.of(PARAM_NAME, null));
        Assert.assertNull(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPrivateMethodWithSeveralParams() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        final String value1 = "Hello endpoint!";
        final String value2 = "I want to test you!";
        final int value3 = 356;
        controller.invoke(PRIVATE_WITH_SEVERAL_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertTrue(controller.getModel().getBooleanValue());
        Assert.assertEquals(controller.getModel().getStringValue(), value1 + value2 + value3);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void callMethodWithException() {
        controller.invoke(WITH_EXCEPTION_ACTION);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void callMethodWithWrongParamaters() {
        controller.invoke(PRIVATE_WITH_STRING_PARAM_ACTION);
    }

    /** Start Integer Type Related Action Test */
    @Test
    public void callPublicMethodWithIntegerParam() {
        Assert.assertNull(controller.getModel().getIntegerValue());
        final int value = 10;
        controller.invoke(PUBLIC_WITH_INTEGER_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getIntegerValue().intValue(), value);
    }

    @Test
    public void callPrivateMethodWithIntegerParam() {
        Assert.assertNull(controller.getModel().getIntegerValue());
        final int value = 10;
        controller.invoke(PUBLIC_WITH_INTEGER_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getIntegerValue().intValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralIntegerParams() {
        Assert.assertNull(controller.getModel().getIntegerValue());
        final int value1 = 1;
        final int value2 = 2;
        final int value3 = 3;
        controller.invoke(PUBLIC_WITH_SEVERAL_INTEGER_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getIntegerValue().intValue(), value1 + value2 + value3);
    }

    @Test
    public void callPrivateMethodWithSeveralIntegerParams() {
        Assert.assertNull(controller.getModel().getIntegerValue());
        final int value1 = 1;
        final int value2 = 2;
        controller.invoke(PRIVATE_WITH_SEVERAL_INTEGER_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getIntegerValue().intValue(), value1 + value2);
    }
    /** End Integer Type Related Action Test */


    /** Start Long Type Related Action Test */
    @Test
    public void callPublicMethodWithLongParam() {
        Assert.assertNull(controller.getModel().getLongValue());
        final long value = 10L;
        controller.invoke(PUBLIC_WITH_LONG_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getLongValue().longValue(), value);
    }

    @Test
    public void callPrivateMethodWithLongParam() {
        Assert.assertNull(controller.getModel().getLongValue());
        final long value = 10L;
        controller.invoke(PUBLIC_WITH_LONG_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getLongValue().longValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralLongParams() {
        Assert.assertNull(controller.getModel().getLongValue());
        final long value1 = 1L;
        final long value2 = 2L;
        final long value3 = 3L;
        controller.invoke(PUBLIC_WITH_SEVERAL_LONG_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getLongValue().longValue(), value1 + value2 + value3);
    }

    @Test
    public void callPrivateMethodWithSeveralLongParams() {
        Assert.assertNull(controller.getModel().getLongValue());
        final long value1 = 1L;
        final long value2 = 2L;
        controller.invoke(PRIVATE_WITH_SEVERAL_LONG_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getLongValue().longValue(), value1 + value2);
    }
    /** End Long Type Related Action Test */


    /** Start Float Type Related Action Test */
    @Test
    public void callPublicMethodWithFloatParam() {
        Assert.assertNull(controller.getModel().getFloatValue());
        final float value = 10F;
        controller.invoke(PUBLIC_WITH_FLOAT_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), value);
    }

    @Test
    public void callPrivateMethodWithFloatParam() {
        Assert.assertNull(controller.getModel().getFloatValue());
        final float value = 10F;
        controller.invoke(PUBLIC_WITH_FLOAT_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralFloatParams() {
        Assert.assertNull(controller.getModel().getFloatValue());
        final float value1 = 1F;
        final float value2 = 2F;
        final float value3 = 3F;
        controller.invoke(PUBLIC_WITH_SEVERAL_FLOAT_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), value1 + value2 + value3);
    }

    @Test
    public void callPrivateMethodWithSeveralFloatParams() {
        Assert.assertNull(controller.getModel().getFloatValue());
        final float value1 = 1F;
        final float value2 = 2F;
        controller.invoke(PRIVATE_WITH_SEVERAL_FLOAT_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), value1 + value2);
    }
    /** End Float Type Related Action Test */


    /** Start Double Type Related Action Test */
    @Test
    public void callPublicMethodWithDoubleParam() {
        Assert.assertNull(controller.getModel().getDoubleValue());
        final double value = 10.0;
        controller.invoke(PUBLIC_WITH_DOUBLE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), value);
    }

    @Test
    public void callPrivateMethodWithDoubleParam() {
        Assert.assertNull(controller.getModel().getDoubleValue());
        final double value = 10.0;
        controller.invoke(PUBLIC_WITH_DOUBLE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralDoubleParams() {
        Assert.assertNull(controller.getModel().getDoubleValue());
        final double value1 = 1.0;
        final double value2 = 2.0;
        final double value3 = 3.0;
        controller.invoke(PUBLIC_WITH_SEVERAL_DOUBLE_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), value1 + value2 + value3);
    }

    @Test
    public void callPrivateMethodWithSeveralDoubleParams() {
        Assert.assertNull(controller.getModel().getDoubleValue());
        final double value1 = 1.0;
        final double value2 = 2.0;
        controller.invoke(PRIVATE_WITH_SEVERAL_DOUBLE_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), value1 + value2);
    }
    /** End Double Type Related Action Test */


    /** Start BigDecimal Type Related Action Test */
    @Test
    public void callPublicMethodWithBigDecimalParam() {
        Assert.assertNull(controller.getModel().getBigDecimalValue());
        final BigDecimal value = new BigDecimal(10);
        controller.invoke(PUBLIC_WITH_BIGDECIMAL_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getBigDecimalValue(), value);
    }

    @Test
    public void callPrivateMethodWithBigDecimalParam() {
        Assert.assertNull(controller.getModel().getBigDecimalValue());
        final BigDecimal value = new BigDecimal(10);
        controller.invoke(PUBLIC_WITH_BIGDECIMAL_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getBigDecimalValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralBigDecimalParams() {
        Assert.assertNull(controller.getModel().getBigDecimalValue());
        final BigDecimal value1 = new BigDecimal(10);
        final BigDecimal value2 = new BigDecimal(20);
        final BigDecimal value3 = new BigDecimal(30);
        controller.invoke(PUBLIC_WITH_SEVERAL_BIGDECIMAL_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getBigDecimalValue(), value1.add(value2).add(value3));
    }

    @Test
    public void callPrivateMethodWithSeveralBigDecimalParams() {
        Assert.assertNull(controller.getModel().getBigDecimalValue());
        final BigDecimal value1 = new BigDecimal(10);
        final BigDecimal value2 = new BigDecimal(20);
        controller.invoke(PRIVATE_WITH_SEVERAL_BIGDECIMAL_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getBigDecimalValue(), value1.add(value2));
    }
    /** End BigDecimal Type Related Action Test */


    /** Start BigInteger Type Related Action Test */
    @Test
    public void callPublicMethodWithBigIntegerParam() {
        Assert.assertNull(controller.getModel().getBigIntegerValue());
        final BigInteger value = BigInteger.TEN;
        controller.invoke(PUBLIC_WITH_BIGINTEGER_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), value);
    }

    @Test
    public void callPrivateMethodWithBigIntegerParam() {
        Assert.assertNull(controller.getModel().getBigIntegerValue());
        final BigInteger value = BigInteger.TEN;
        controller.invoke(PUBLIC_WITH_BIGINTEGER_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralBigIntegerParams() {
        Assert.assertNull(controller.getModel().getBigIntegerValue());
        final BigInteger value1 = BigInteger.TEN;
        final BigInteger value2 = BigInteger.TEN;
        final BigInteger value3 = BigInteger.TEN;
        controller.invoke(PUBLIC_WITH_SEVERAL_BIGINTEGER_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), value1.add(value2).add(value3));
    }

    @Test
    public void callPrivateMethodWithSeveralBigIntegerParams() {
        Assert.assertNull(controller.getModel().getBigIntegerValue());
        final BigInteger value1 = BigInteger.TEN;
        final BigInteger value2 = BigInteger.TEN;
        controller.invoke(PRIVATE_WITH_SEVERAL_BIGINTEGER_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), value1.add(value2));
    }
    /** End BigInteger Type Related Action Test */


    /** Start Byte Type Related Action Test */
    @Test
    public void callPublicMethodWithByteParam() {
        Assert.assertNull(controller.getModel().getByteValue());
        final Byte value = 10;
        controller.invoke(PUBLIC_WITH_BYTE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getByteValue(), value);
    }

    @Test
    public void callPrivateMethodWithByteParam() {
        Assert.assertNull(controller.getModel().getByteValue());
        final Byte value = 10;
        controller.invoke(PUBLIC_WITH_BYTE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getByteValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralByteParams() {
        Assert.assertNull(controller.getModel().getByteValue());
        final Byte value1 = 10;
        final Byte value2 = 20;
        final Byte value3 = 30;
        controller.invoke(PUBLIC_WITH_SEVERAL_BYTE_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getByteValue().byteValue(), (byte)(value1 + value2 + value3));
    }

    @Test
    public void callPrivateMethodWithSeveralByteParams() {
        Assert.assertNull(controller.getModel().getByteValue());
        final Byte value1 = 10;
        final Byte value2 = 20;
        controller.invoke(PRIVATE_WITH_SEVERAL_BYTE_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getByteValue().byteValue(), (byte)(value1 + value2));
    }
    /** End Byte Type Related Action Test */

    /** Start Calendar Type Related Action Test */
    @Test
    public void callPublicMethodWithCalendarParam() {
        Assert.assertNull(controller.getModel().getCalendarValue());
        final Calendar value = Calendar.getInstance();
        controller.invoke(PUBLIC_WITH_CALENDER_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), value.getTime());
    }

    @Test
    public void callPrivateMethodWithCalendarParam() {
        Assert.assertNull(controller.getModel().getCalendarValue());
        final Calendar value = Calendar.getInstance();
        controller.invoke(PUBLIC_WITH_CALENDER_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), value.getTime());
    }

    /** End Calendar Type Related Action Test */


    /** Start Date Type Related Action Test */
    @Test
    public void callPublicMethodWithDateParam() {
        Assert.assertNull(controller.getModel().getDateValue());
        final Date value = new Date();
        controller.invoke(PUBLIC_WITH_DATE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getDateValue(), value);
    }

    @Test
    public void callPrivateMethodWithDateParam() {
        Assert.assertNull(controller.getModel().getDateValue());
        final Date value = new Date();
        controller.invoke(PUBLIC_WITH_DATE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getDateValue(), value);
    }

    /** End Date Type Related Action Test */

    /** Start Short Type Related Action Test */
    @Test
    public void callPublicMethodWithShortParam() {
        Assert.assertNull(controller.getModel().getShortValue());
        final short value = 10;
        controller.invoke(PUBLIC_WITH_SHORT_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getShortValue().shortValue(), value);
    }

    @Test
    public void callPrivateMethodWithShortParam() {
        Assert.assertNull(controller.getModel().getShortValue());
        final short value = 10;
        controller.invoke(PUBLIC_WITH_SHORT_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getShortValue().shortValue(), value);
    }

    @Test
    public void callPublicMethodWithSeveralShortParams() {
        Assert.assertNull(controller.getModel().getShortValue());
        final short value1 = 10;
        final short value2 = 10;
        final short value3 = 10;
        controller.invoke(PUBLIC_WITH_SEVERAL_SHORT_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2), Param.of(PARAM_NAME_3, value3));
        Assert.assertEquals(controller.getModel().getShortValue().shortValue(), (short)(value1 + value2 + value3));
    }

    @Test
    public void callPrivateMethodWithSeveralShortParams() {
        Assert.assertNull(controller.getModel().getShortValue());
        final short value1 = 10;
        final short value2 = 10;
        controller.invoke(PRIVATE_WITH_SEVERAL_SHORT_PARAMS_ACTION, Param.of(PARAM_NAME_1, value1), Param.of(PARAM_NAME_2, value2));
        Assert.assertEquals(controller.getModel().getShortValue().shortValue(), (short)(value1 + value2));
    }
    /** End Short Type Related Action Test */


    /** Start UUID Type Related Action Test */
    @Test
    public void callPublicMethodWithUUIDParam() {
        Assert.assertNull(controller.getModel().getUuidValue());
        final UUID value = UUID.randomUUID();
        controller.invoke(PUBLIC_WITH_UUID_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getUuidValue(), value);
    }

    @Test
    public void callPrivateMethodWithUUIDParam() {
        Assert.assertNull(controller.getModel().getUuidValue());
        final UUID value = UUID.randomUUID();
        controller.invoke(PUBLIC_WITH_UUID_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getUuidValue(), value);
    }
    /** End UUID Type Related Action Test */



    /** Start ElementType Type Related Action Test */
    @Test
    public void callPublicMethodWithElementTypeParam() {
        Assert.assertNull(controller.getModel().getEnumValue());
        final ElementType value = ElementType.FIELD;
        controller.invoke(PUBLIC_WITH_ELEMENT_TYPE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getEnumValue(), value);
    }

    @Test
    public void callPrivateMethodWithElementTypeParam() {
        Assert.assertNull(controller.getModel().getEnumValue());
        final ElementType value = ElementType.METHOD;
        controller.invoke(PUBLIC_WITH_ELEMENT_TYPE_PARAM_ACTION, Param.of(PARAM_NAME, value));
        Assert.assertEquals(controller.getModel().getEnumValue().compareTo(value), 0);
    }

    /** End ElementType Type Related Action Test */

    @Test(expectedExceptions = ControllerTestException.class)
    public void checkExceptionHandlerForRuntimeException() {
        try {
            controller.invoke(WITH_EXCEPTION_ACTION);
        } finally {
            System.out.println(controller.getModel().getErrors());
        }
    }

}

