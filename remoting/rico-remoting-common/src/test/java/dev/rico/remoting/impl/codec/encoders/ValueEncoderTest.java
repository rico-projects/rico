/*
 * Copyright 2018 Karakun AG.
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
package dev.rico.remoting.impl.codec.encoders;

import dev.rico.internal.remoting.communication.codec.encoders.ValueEncoder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class ValueEncoderTest {

    @Test
    public void testStringEncode() {
        //given:
        Object value = "Hello Encoder";

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonPrimitive());
        Assert.assertEquals(encoded.getAsString(), "Hello Encoder");
    }

    @Test
    public void testBooleanEncode() {
        //given:
        Object value = true;

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonPrimitive());
        Assert.assertEquals(encoded.getAsBoolean(), true);
    }

    @Test
    public void testIntegerEncode() {
        //given:
        Object value = 42;

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonPrimitive());
        Assert.assertEquals(encoded.getAsNumber(), 42);
    }

    @Test
    public void testLongEncode() {
        //given:
        Object value = 42L;

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonPrimitive());
        Assert.assertEquals(encoded.getAsNumber(), 42L);
    }

    @Test
    public void testDoubleEncode() {
        //given:
        Object value = 42.9d;

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonPrimitive());
        Assert.assertEquals(encoded.getAsNumber(), 42.9d);
    }

    @Test
    public void testFloatEncode() {
        //given:
        Object value = 42.9f;

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonPrimitive());
        Assert.assertEquals(encoded.getAsNumber(), 42.9f);
    }

    @Test
    public void testNullEncode() {
        //given:
        Object value = null;

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.isJsonNull());
    }

    @Test(expectedExceptions = JsonParseException.class)
    public void testWrongEncode() {
        //given:
        Object value = new Date();

        //when:
        JsonElement encoded = ValueEncoder.encodeValue(value);

        //then:
        Assert.fail();
    }

    @Test
    public void testNullDecode() {
        //given:
        JsonElement value = JsonNull.INSTANCE;

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNull(decoded);
    }

    @Test
    public void testStringDecode() {
        //given:
        JsonElement value = new JsonPrimitive("Hello");

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded.getClass(), String.class);
        Assert.assertEquals(decoded, "Hello");
    }

    @Test
    public void testBooleanDecode() {
        //given:
        JsonElement value = new JsonPrimitive(true);

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded.getClass(), Boolean.class);
        Assert.assertEquals(decoded, true);
    }

    @Test
    public void testIntegerDecode() {
        //given:
        JsonElement value = new JsonPrimitive(42);

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded.getClass(), Integer.class);
        Assert.assertEquals(decoded, 42);
    }

    @Test
    public void testDoubleDecode() {
        //given:
        JsonElement value = new JsonPrimitive(42.2d);

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded.getClass(), Double.class);
        Assert.assertEquals(decoded, 42.2d);
    }

    @Test
    public void testFloatDecode() {
        //given:
        JsonElement value = new JsonPrimitive(42.2f);

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded.getClass(), Float.class);
        Assert.assertEquals(decoded, 42.2f);
    }

    @Test
    public void testLongDecode() {
        //given:
        JsonElement value = new JsonPrimitive(42L);

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded.getClass(), Long.class);
        Assert.assertEquals(decoded, 42L);
    }

    @Test(expectedExceptions = JsonParseException.class)
    public void testWrongDecode() {
        //given:
        JsonElement value = new JsonObject();

        //when:
        Object decoded = ValueEncoder.decodeValue(value);

        //then:
        Assert.fail();
    }

}
