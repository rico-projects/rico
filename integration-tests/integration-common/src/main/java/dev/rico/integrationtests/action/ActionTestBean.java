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
package dev.rico.integrationtests.action;

import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RemotingBean
public class ActionTestBean {

    private ObservableList<ActionErrorBean> errors;

    private Property<String> stringValue;

    private Property<Boolean> booleanValue;

    private Property<Integer> integerValue;

    private Property<Long> longValue;

    private Property<Float> floatValue;

    private Property<Double> doubleValue;

    private Property<BigDecimal> bigDecimalValue;

    private Property<BigInteger> bigIntegerValue;

    private Property<Byte> byteValue;

    private Property<Calendar> calendarValue;

    private Property<Date> dateValue;

    private Property<Short> shortValue;

    private Property<UUID> uuidValue;

    private Property<ElementType> enumValue;

    public ObservableList<ActionErrorBean> getErrors() {
        return errors;
    }

    public String getStringValue() {
        return stringValue.get();
    }

    public void setStringValue(String stringValue) {
        this.stringValue.set(stringValue);
    }

    public Boolean getBooleanValue() {
        return booleanValue.get();
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue.set(booleanValue);
    }

    public Integer getIntegerValue() {
        return integerValue.get();
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue.set(integerValue);
    }

    public Long getLongValue() {
        return longValue.get();
    }

    public void setLongValue(Long longValue) {
        this.longValue.set(longValue);
    }

    public Float getFloatValue() {
        return floatValue.get();
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue.set(floatValue);
    }

    public Double getDoubleValue() {
        return doubleValue.get();
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue.set(doubleValue);
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue.get();
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue.set(bigDecimalValue);
    }

    public BigInteger getBigIntegerValue() {
        return bigIntegerValue.get();
    }

    public void setBigIntegerValue(BigInteger bigIntegerValue) {
        this.bigIntegerValue.set(bigIntegerValue);
    }

    public Byte getByteValue() {
        return byteValue.get();
    }

    public void setByteValue(Byte byteValue) {
        this.byteValue.set(byteValue);
    }

    public Calendar getCalendarValue() {
        return calendarValue.get();
    }

    public void setCalendarValue(Calendar calendarValue) {
        this.calendarValue.set(calendarValue);
    }

    public Date getDateValue() {
        return dateValue.get();
    }

    public void setDateValue(Date dateValue) {
        this.dateValue.set(dateValue);
    }

    public Short getShortValue() {
        return shortValue.get();
    }

    public void setShortValue(Short shortValue) {
        this.shortValue.set(shortValue);
    }

    public UUID getUuidValue() {
        return uuidValue.get();
    }

    public void setUuidValue(UUID uuidValue) {
        this.uuidValue.set(uuidValue);
    }

    public ElementType getEnumValue() {
        return enumValue.get();
    }

    public void setEnumValue(ElementType enumValue) {
        this.enumValue.set(enumValue);
    }

}
