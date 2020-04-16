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

import dev.rico.integrationtests.remoting.action.ActionErrorBean;
import dev.rico.integrationtests.remoting.action.ActionTestBean;
import dev.rico.remoting.BeanManager;
import dev.rico.remoting.server.Param;
import dev.rico.remoting.server.RemotingAction;
import dev.rico.remoting.server.RemotingController;
import dev.rico.remoting.server.RemotingModel;
import dev.rico.remoting.server.error.ActionExceptionEvent;
import dev.rico.remoting.server.error.ActionExceptionHandler;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static dev.rico.integrationtests.remoting.action.ActionTestConstants.*;

@RemotingController(ACTION_CONTROLLER_NAME)
public class ActionTestController {

    @RemotingModel
    private ActionTestBean model;

    @Inject
    private BeanManager beanManager;

    @RemotingAction(RESET_MODEL_ACTION)
    public void resetModel() {
        model.setBooleanValue(null);
        model.setStringValue(null);
    }

    @RemotingAction(PUBLIC_ACTION)
    public void publicAction() {
        model.setBooleanValue(true);
    }

    @RemotingAction(PRIVATE_ACTION)
    private void privateAction() {
        model.setBooleanValue(true);
    }

    @RemotingAction(PUBLIC_WITH_BOOLEAN_PARAM_ACTION)
    public void withStringParam(@Param(PARAM_NAME) Boolean value) {
        model.setBooleanValue(value);
    }

    @RemotingAction(PRIVATE_WITH_STRING_PARAM_ACTION)
    private void withStringParam(@Param(PARAM_NAME) String value) {
        model.setBooleanValue(true);
        model.setStringValue(value);
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_PARAMS_ACTION)
    private void withSeveralParams(@Param(PARAM_NAME_1) String value1, @Param(PARAM_NAME_2) String value2, @Param(PARAM_NAME_3) int value3) {
        model.setBooleanValue(true);
        model.setStringValue(value1 + value2 + value3);
    }

    @RemotingAction(WITH_EXCEPTION_ACTION)
    private void withException() {
        throw new RuntimeException("huhu");
    }


    /** Start Integer Type Related Action */
    @RemotingAction(PUBLIC_WITH_INTEGER_PARAM_ACTION)
    public void withPublicIntegerParam(@Param(PARAM_NAME) int value) {
        model.setIntegerValue(value);
    }

    @RemotingAction(PRIVATE_WITH_INTEGER_PARAM_ACTION)
    private void withPrivateIntegerParam(@Param(PARAM_NAME) int value) {
        model.setIntegerValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_INTEGER_PARAMS_ACTION)
    public void withSeveralPublicIntegerParams(@Param(PARAM_NAME_1) int value1, @Param(PARAM_NAME_2) int value2, @Param(PARAM_NAME_3) int value3) {
        model.setIntegerValue(value1 + value2 + value3);
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_INTEGER_PARAMS_ACTION)
    private void withSeveralPrivateIntegerParams(@Param(PARAM_NAME_1) int value1, @Param(PARAM_NAME_2) int value2) {
        model.setIntegerValue(value1 + value2);
    }
    /** End Integer Type Related Action */


    /** Start Long Type Related Action */
    @RemotingAction(PUBLIC_WITH_LONG_PARAM_ACTION)
    public void withPublicLongParam(@Param(PARAM_NAME) long value) {
        model.setLongValue(value);
    }

    @RemotingAction(PRIVATE_WITH_LONG_PARAM_ACTION)
    private void withPrivateLongParam(@Param(PARAM_NAME) long value) {
        model.setLongValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_LONG_PARAMS_ACTION)
    public void withSeveralPublicLongParams(@Param(PARAM_NAME_1) long value1, @Param(PARAM_NAME_2) long value2, @Param(PARAM_NAME_3) long value3) {
        model.setLongValue(value1 + value2 + value3);
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_LONG_PARAMS_ACTION)
    private void withSeveralPrivateLongParams(@Param(PARAM_NAME_1) long value1, @Param(PARAM_NAME_2) long value2) {
        model.setLongValue(value1 + value2);
    }
    /** End Long Type Related Action */


    /** Start Float Type Related Action */
    @RemotingAction(PUBLIC_WITH_FLOAT_PARAM_ACTION)
    public void withPublicFloatParam(@Param(PARAM_NAME) float value) {
        model.setFloatValue(value);
    }

    @RemotingAction(PRIVATE_WITH_FLOAT_PARAM_ACTION)
    private void withPrivateFloatParam(@Param(PARAM_NAME) float value) {
        model.setFloatValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_FLOAT_PARAMS_ACTION)
    public void withSeveralPublicFloatParams(@Param(PARAM_NAME_1) float value1, @Param(PARAM_NAME_2) float value2, @Param(PARAM_NAME_3) float value3) {
        model.setFloatValue(value1 + value2 + value3);
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_FLOAT_PARAMS_ACTION)
    private void withSeveralPrivateFloatParams(@Param(PARAM_NAME_1) float value1, @Param(PARAM_NAME_2) float value2) {
        model.setFloatValue(value1 + value2);
    }
    /** End Float Type Related Action */


    /** Start Double Type Related Action */
    @RemotingAction(PUBLIC_WITH_DOUBLE_PARAM_ACTION)
    public void withPublicDoubleParam(@Param(PARAM_NAME) double value) {
        model.setDoubleValue(value);
    }

    @RemotingAction(PRIVATE_WITH_DOUBLE_PARAM_ACTION)
    private void withPrivateDoubleParam(@Param(PARAM_NAME) double value) {
        model.setDoubleValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_DOUBLE_PARAMS_ACTION)
    public void withSeveralPublicDoubleParams(@Param(PARAM_NAME_1) double value1, @Param(PARAM_NAME_2) double value2, @Param(PARAM_NAME_3) double value3) {
        model.setDoubleValue(value1 + value2 + value3);
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_DOUBLE_PARAMS_ACTION)
    private void withSeveralPrivateDoubleParams(@Param(PARAM_NAME_1) double value1, @Param(PARAM_NAME_2) double value2) {
        model.setDoubleValue(value1 + value2);
    }
    /** End Double Type Related Action */

    /** Start BigDecimal Type Related Action */
    @RemotingAction(PUBLIC_WITH_BIGDECIMAL_PARAM_ACTION)
    public void withPublicBigDecimalParam(@Param(PARAM_NAME) BigDecimal value) {
        model.setBigDecimalValue(value);
    }

    @RemotingAction(PRIVATE_WITH_BIGDECIMAL_PARAM_ACTION)
    private void withPrivateBigDecimalParam(@Param(PARAM_NAME) BigDecimal value) {
        model.setBigDecimalValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_BIGDECIMAL_PARAMS_ACTION)
    public void withSeveralPublicBigDecimalParams(@Param(PARAM_NAME_1) BigDecimal value1, @Param(PARAM_NAME_2) BigDecimal value2, @Param(PARAM_NAME_3) BigDecimal value3) {
        model.setBigDecimalValue(value1.add(value2).add(value3));
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_BIGDECIMAL_PARAMS_ACTION)
    private void withSeveralPrivateBigDecimalParams(@Param(PARAM_NAME_1) BigDecimal value1, @Param(PARAM_NAME_2) BigDecimal value2) {
        model.setBigDecimalValue(value1.add(value2));
    }
    /** End BigDecimal Type Related Action */


    /** Start BigInteger Type Related Action */
    @RemotingAction(PUBLIC_WITH_BIGINTEGER_PARAM_ACTION)
    public void withPublicBigIntegerParam(@Param(PARAM_NAME) BigInteger value) {
        model.setBigIntegerValue(value);
    }

    @RemotingAction(PRIVATE_WITH_BIGINTEGER_PARAM_ACTION)
    private void withPrivateBigIntegerParam(@Param(PARAM_NAME) BigInteger value) {
        model.setBigIntegerValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_BIGINTEGER_PARAMS_ACTION)
    public void withSeveralPublicBigIntegerParams(@Param(PARAM_NAME_1) BigInteger value1, @Param(PARAM_NAME_2) BigInteger value2, @Param(PARAM_NAME_3) BigInteger value3) {
        model.setBigIntegerValue(value1.add(value2).add(value3));
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_BIGINTEGER_PARAMS_ACTION)
    private void withSeveralPrivateBigIntegerParams(@Param(PARAM_NAME_1) BigInteger value1, @Param(PARAM_NAME_2) BigInteger value2) {
        model.setBigIntegerValue(value1.add(value2));
    }
    /** End BigInteger Type Related Action */


    /** Start Byte Type Related Action */
    @RemotingAction(PUBLIC_WITH_BYTE_PARAM_ACTION)
    public void withPublicByteParam(@Param(PARAM_NAME) byte value) {
        model.setByteValue(value);
    }

    @RemotingAction(PRIVATE_WITH_BYTE_PARAM_ACTION)
    private void withPrivateByteParam(@Param(PARAM_NAME) byte value) {
        model.setByteValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_BYTE_PARAMS_ACTION)
    public void withSeveralPublicByteParams(@Param(PARAM_NAME_1) byte value1, @Param(PARAM_NAME_2) byte value2, @Param(PARAM_NAME_3) byte value3) {
        model.setByteValue((byte)(value1 + value2 + value3));
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_BYTE_PARAMS_ACTION)
    private void withSeveralPrivateByteParams(@Param(PARAM_NAME_1) byte value1, @Param(PARAM_NAME_2) byte value2) {
        model.setByteValue((byte)(value1 + value2));
    }
    /** End Byte Type Related Action */


    /** Start Calendar Type Related Action */
    @RemotingAction(PUBLIC_WITH_CALENDER_PARAM_ACTION)
    public void withPublicCalendarParam(@Param(PARAM_NAME) Calendar value) {
        model.setCalendarValue(value);
    }

    @RemotingAction(PRIVATE_WITH_CALENDER_PARAM_ACTION)
    private void withPrivateCalendarParam(@Param(PARAM_NAME) Calendar value) {
        model.setCalendarValue(value);
    }

    /** End Calendar Type Related Action */

    /** Start Date Type Related Action */
    @RemotingAction(PUBLIC_WITH_DATE_PARAM_ACTION)
    public void withPublicDateParam(@Param(PARAM_NAME) Date value) {
        model.setDateValue(value);
    }

    @RemotingAction(PRIVATE_WITH_DATE_PARAM_ACTION)
    private void withPrivateDateParam(@Param(PARAM_NAME) Date value) {
        model.setDateValue(value);
    }

    /** End Date Type Related Action */


    /** Start Short Type Related Action */
    @RemotingAction(PUBLIC_WITH_SHORT_PARAM_ACTION)
    public void withPublicShortParam(@Param(PARAM_NAME) short value) {
        model.setShortValue(value);
    }

    @RemotingAction(PRIVATE_WITH_SHORT_PARAM_ACTION)
    private void withPrivateShortParam(@Param(PARAM_NAME) short value) {
        model.setShortValue(value);
    }

    @RemotingAction(PUBLIC_WITH_SEVERAL_SHORT_PARAMS_ACTION)
    public void withSeveralPublicShortParams(@Param(PARAM_NAME_1) short value1, @Param(PARAM_NAME_2) short value2, @Param(PARAM_NAME_3) short value3) {
        model.setShortValue((short)(value1 + value2 + value3));
    }

    @RemotingAction(PRIVATE_WITH_SEVERAL_SHORT_PARAMS_ACTION)
    private void withSeveralPrivateShortParams(@Param(PARAM_NAME_1) short value1, @Param(PARAM_NAME_2) short value2) {
        model.setShortValue((short)(value1 + value2));
    }
    /** End Short Type Related Action */



    /** Start UUID Type Related Action */
    @RemotingAction(PUBLIC_WITH_UUID_PARAM_ACTION)
    public void withPublicUUIDParam(@Param(PARAM_NAME) UUID value) {
        model.setUuidValue(value);
    }

    @RemotingAction(PRIVATE_WITH_UUID_PARAM_ACTION)
    private void withPrivateUUIDParam(@Param(PARAM_NAME) UUID value) {
        model.setUuidValue(value);
    }
    /** End UUID Type Related Action */


    /** Start ElementType Type Related Action */
    @RemotingAction(PUBLIC_WITH_ELEMENT_TYPE_PARAM_ACTION)
    public void withPublicElementTypeParam(@Param(PARAM_NAME) ElementType value) {
        model.setEnumValue(value);
    }

    @RemotingAction(PRIVATE_WITH_ELEMENT_TYPE_PARAM_ACTION)
    private void withPrivateElementTypeParam(@Param(PARAM_NAME) ElementType value) {
        model.setEnumValue(value);
    }
    /** End ElementType Type Related Action */

    private void addErrorBean(final ActionExceptionEvent<?> exceptionEvent) {
        final ActionErrorBean errorBean = beanManager.create(ActionErrorBean.class);
        errorBean.setActionName(exceptionEvent.getActionName());
        errorBean.setControllerName(exceptionEvent.getControllerName());
        errorBean.setExceptionName(exceptionEvent.getException().getClass().getSimpleName());
        model.getErrors().add(errorBean);
    }

    @ActionExceptionHandler
    private void onRuntimeException(final ActionExceptionEvent<RuntimeException> exceptionEvent) {
        addErrorBean(exceptionEvent);
    }

    @ActionExceptionHandler
    private void onIOException(final ActionExceptionEvent<IOException> exceptionEvent) {
        addErrorBean(exceptionEvent);
    }

    @ActionExceptionHandler
    private void onException(final ActionExceptionEvent<Exception> exceptionEvent) {
        addErrorBean(exceptionEvent);
    }

    @ActionExceptionHandler
    private void onAnyException(final ActionExceptionEvent<?> exceptionEvent) {
        addErrorBean(exceptionEvent);
    }

    @ActionExceptionHandler
    private void onAnyRuntimeException(final ActionExceptionEvent<? extends RuntimeException> exceptionEvent) {
        addErrorBean(exceptionEvent);
    }
}
