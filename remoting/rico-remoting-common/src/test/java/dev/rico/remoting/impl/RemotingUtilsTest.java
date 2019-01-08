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
package dev.rico.remoting.impl;

import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.MockedProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.UUID;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class RemotingUtilsTest {

    @Test
    public void testIsAllowedForUnmanaged() {
        //Basics
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Double.class));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Double.TYPE));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Long.class));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Long.TYPE));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Float.class));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Float.TYPE));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Integer.class));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Integer.TYPE));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Boolean.class));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(Boolean.TYPE));
        assertTrue(RemotingUtils.isAllowedForUnmanaged(String.class));

        //Enum
        assertTrue(RemotingUtils.isAllowedForUnmanaged(RetentionPolicy.class));

        //Property
        assertTrue(RemotingUtils.isAllowedForUnmanaged(MockedProperty.class));

        //Other
        assertFalse(RemotingUtils.isAllowedForUnmanaged(Date.class));
        assertFalse(RemotingUtils.isAllowedForUnmanaged(LocalDateTime.class));
        assertFalse(RemotingUtils.isAllowedForUnmanaged(Locale.class));

        try {
            RemotingUtils.isAllowedForUnmanaged(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testIsEnumType() throws Exception {
        assertTrue(RemotingUtils.isEnumType(DataType.class));

        try {
            RemotingUtils.isEnumType(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testIsProperty() throws Exception {
        assertTrue(RemotingUtils.isProperty(MockedProperty.class));

        try {
            RemotingUtils.isProperty((Class<?>) null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testBasicType() throws Exception {
        assertTrue(RemotingUtils.isBasicType(String.class));
        assertTrue(RemotingUtils.isBasicType(Number.class));
        assertTrue(RemotingUtils.isBasicType(Long.class));
        assertTrue(RemotingUtils.isBasicType(Integer.class));
        assertTrue(RemotingUtils.isBasicType(Double.class));
        assertTrue(RemotingUtils.isBasicType(Boolean.class));
        assertTrue(RemotingUtils.isBasicType(Byte.class));
        assertTrue(RemotingUtils.isBasicType(Short.class));
        assertTrue(RemotingUtils.isBasicType(BigDecimal.class));
        assertTrue(RemotingUtils.isBasicType(BigInteger.class));
        assertTrue(RemotingUtils.isBasicType(Long.TYPE));
        assertTrue(RemotingUtils.isBasicType(Integer.TYPE));
        assertTrue(RemotingUtils.isBasicType(Double.TYPE));
        assertTrue(RemotingUtils.isBasicType(Boolean.TYPE));
        assertTrue(RemotingUtils.isBasicType(Byte.TYPE));
        assertTrue(RemotingUtils.isBasicType(Short.TYPE));

        assertFalse(RemotingUtils.isBasicType(RemotingUtilsTest.class));
        assertFalse(RemotingUtils.isBasicType(DataType.class));
        assertFalse(RemotingUtils.isBasicType(UUID.class));

        try {
            RemotingUtils.isBasicType(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testIsObservableList() {
        assertTrue(RemotingUtils.isObservableList(ObservableArrayList.class));
        assertFalse(RemotingUtils.isObservableList(LinkedList.class));

        try {
            RemotingUtils.isObservableList(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }
}
