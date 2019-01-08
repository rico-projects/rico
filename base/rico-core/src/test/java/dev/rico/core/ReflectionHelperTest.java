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
package dev.rico.core;

import dev.rico.internal.core.ReflectionHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;


public class ReflectionHelperTest {

    private Field forGetInheritedDeclaredFieldCheck;

    private List<String> forTypeParameterCheck1;

    private List forTypeParameterCheck2;

    @Test
    public void testGetInheritedDeclaredField() {
        forGetInheritedDeclaredFieldCheck = ReflectionHelper.getInheritedDeclaredField(Character.class, "COMBINING_SPACING_MARK");
        
        Assert.assertEquals(false, forGetInheritedDeclaredFieldCheck.isEnumConstant());
        Assert.assertTrue(forGetInheritedDeclaredFieldCheck instanceof Field);
        Assert.assertEquals("COMBINING_SPACING_MARK", forGetInheritedDeclaredFieldCheck.getName());
        
        forGetInheritedDeclaredFieldCheck = ReflectionHelper.getInheritedDeclaredField(Integer.class, "MAX_VALUE");
        
        Assert.assertEquals(false, forGetInheritedDeclaredFieldCheck.isEnumConstant());
        Assert.assertTrue(forGetInheritedDeclaredFieldCheck instanceof Field);
        Assert.assertEquals("MAX_VALUE", forGetInheritedDeclaredFieldCheck.getName());
    }

    @Test
    public void testGetInheritedDeclaredFields() {
        Assert.assertTrue(ReflectionHelper.getInheritedDeclaredFields(Date.class) instanceof List);
        Assert.assertTrue(ReflectionHelper.getInheritedDeclaredFields(List.class) instanceof List);
        Assert.assertTrue(ReflectionHelper.getInheritedDeclaredFields(String.class) instanceof List);
    }

    @Test
    public void testGetInheritedDeclaredMethods() {
        Assert.assertTrue(ReflectionHelper.getInheritedDeclaredMethods(Date.class) instanceof List);
        Assert.assertTrue(ReflectionHelper.getInheritedDeclaredMethods(List.class) instanceof List);
        Assert.assertTrue(ReflectionHelper.getInheritedDeclaredMethods(String.class) instanceof List);
    }

    @Test
    public void testIsProxyInstance() {
        Assert.assertFalse(ReflectionHelper.isProxyInstance(new Integer(0)));
        Assert.assertFalse(ReflectionHelper.isProxyInstance(""));
    }

    @Test
    public void testGetTypeParameter() {
        try {
            Assert.assertEquals(ReflectionHelper.getTypeParameter(ReflectionHelperTest.class.getDeclaredField("forTypeParameterCheck1")), String.class);
        } catch (Exception e) {
            Assert.fail("Generic Type not found", e);
        }

        try {
            Assert.assertEquals(ReflectionHelper.getTypeParameter(ReflectionHelperTest.class.getDeclaredField("forTypeParameterCheck2")), null);
        } catch (Exception e) {
            Assert.fail("Generic Type not found", e);
        }

        try {
            ReflectionHelper.getTypeParameter(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testIsNumber() {
        Assert.assertTrue(ReflectionHelper.isNumber(Integer.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Integer.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Long.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Long.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Double.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Double.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Float.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Float.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Byte.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Byte.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Short.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Short.TYPE));

        Assert.assertFalse(ReflectionHelper.isNumber(Date.class));
        Assert.assertFalse(ReflectionHelper.isNumber(String.class));
        Assert.assertFalse(ReflectionHelper.isNumber(Object.class));
    }

    @Test
    public void testIsPrimitiveNumber() {
       Assert.assertTrue(ReflectionHelper.isPrimitiveNumber(Integer.TYPE));
       Assert.assertTrue(ReflectionHelper.isPrimitiveNumber(Long.TYPE));
       Assert.assertTrue(ReflectionHelper.isPrimitiveNumber(Double.TYPE));
       Assert.assertTrue(ReflectionHelper.isPrimitiveNumber(Float.TYPE));
       Assert.assertTrue(ReflectionHelper.isPrimitiveNumber(Short.TYPE));
       Assert.assertTrue(ReflectionHelper.isPrimitiveNumber(Byte.TYPE));
       
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(String.class));
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(Integer.class));
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(Long.class));
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(Double.class));
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(Float.class));
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(Short.class));
       Assert.assertFalse(ReflectionHelper.isPrimitiveNumber(Byte.class));
    }

    @Test
    public void testInvokePrivileged() throws NoSuchMethodException {
        final Method method = ReflectionHelperTest.class.getMethod("invokeThis");
        final String result = ReflectionHelper.invokePrivileged(method, this);
        Assert.assertEquals(result, "done");
    }

    @Test
    public void testInvokePrivilegedPrivate() throws NoSuchMethodException {
        final Method method = ReflectionHelper.getMethod(ReflectionHelperTest.class, "privateInvokeThis").get();
        final String result = ReflectionHelper.invokePrivileged(method, this);
        Assert.assertEquals(result, "done private");
    }

    @Test
    public void testInvokePrivilegedException() throws NoSuchMethodException {
        final Method method = ReflectionHelper.getMethod(ReflectionHelperTest.class, "fail").get();
        try {
            ReflectionHelper.invokePrivileged(method, this);
            Assert.fail();
        } catch (final RuntimeException e) {
            Assert.assertEquals(e.getCause().getClass(), InvocationTargetException.class);
            final InvocationTargetException invocationTargetException = (InvocationTargetException) e.getCause();
            Assert.assertEquals(invocationTargetException.getCause().getClass(), MissingResourceException.class);
            final MissingResourceException missingResourceException = (MissingResourceException) invocationTargetException.getCause();
            Assert.assertEquals(missingResourceException.getClassName(), "Class");
            Assert.assertEquals(missingResourceException.getKey(), "Key");
            Assert.assertEquals(missingResourceException.getMessage(), "FAIL");
        }
    }

    public String fail() {
        throw new MissingResourceException("FAIL", "Class", "Key");
    }

    public String invokeThis() {
        return "done";
    }

    private String privateInvokeThis() {
        return "done private";
    }

}
