package dev.rico.internal.remoting.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonPrimitiveTypeTests {

    @Test
    public void testGetValueByBigDecimalType() {
        //given:
        final JsonElement element = new JsonPrimitive(new BigDecimal(1.1d));
        final JsonPrimitiveType type = JsonPrimitiveType.BIG_DECIMAL;

        //when:
        BigDecimal value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.doubleValue(), 1.1d, 0.00001);
    }

    @Test
    public void testGetNullValueByBigDecimalType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.BIG_DECIMAL;

        //when:
        BigDecimal value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }


    @Test
    public void testGetValueByBigIntegerType() {
        //given:
        final JsonElement element = new JsonPrimitive(BigInteger.valueOf(100l));
        final JsonPrimitiveType type = JsonPrimitiveType.BIG_INTEGER;

        //when:
        BigInteger value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.longValue(), 100l);
    }

    @Test
    public void testGetNullValueByBigIntegerType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.BIG_INTEGER;

        //when:
        BigInteger value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }


    @Test
    public void testGetValueByBooleanType() {
        //given:
        final JsonElement element = new JsonPrimitive(true);
        final JsonPrimitiveType type = JsonPrimitiveType.BOOLEAN;

        //when:
        Boolean value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertTrue(value.booleanValue());
    }

    @Test
    public void testGetNullValueByBooleanType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.BOOLEAN;

        //when:
        Boolean value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByByteType() {
        //given:
        final JsonElement element = new JsonPrimitive(Byte.valueOf((byte)0xa));
        final JsonPrimitiveType type = JsonPrimitiveType.BYTE;

        //when:
        Byte value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.byteValue(), (byte)0xa);
    }

    @Test
    public void testGetNullValueByByteType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.BYTE;

        //when:
        Byte value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByCharacterType() {
        //given:
        final JsonElement element = new JsonPrimitive(Character.valueOf('a'));
        final JsonPrimitiveType type = JsonPrimitiveType.CHARACTER;

        //when:
        Character value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.charValue(), 'a');
    }

    @Test
    public void testGetNullValueByCharacterType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.CHARACTER;

        //when:
        Character value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByDoubleType() {
        //given:
        final JsonElement element = new JsonPrimitive(0.1d);
        final JsonPrimitiveType type = JsonPrimitiveType.DOUBLE;

        //when:
        Double value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.doubleValue(), 0.1d, 0.00000001d);
    }

    @Test
    public void testGetNullValueByDoubleType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.DOUBLE;

        //when:
        Double value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByFloatType() {
        //given:
        final JsonElement element = new JsonPrimitive(0.1f);
        final JsonPrimitiveType type = JsonPrimitiveType.FLOAT;

        //when:
        Float value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.floatValue(), 0.1f, 0.001f);
    }

    @Test
    public void testGetNullValueByFloatType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.FLOAT;

        //when:
        Float value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByIntegerType() {
        //given:
        final JsonElement element = new JsonPrimitive(10);
        final JsonPrimitiveType type = JsonPrimitiveType.INT;

        //when:
        Integer value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.intValue(), 10);
    }

    @Test
    public void testGetNullValueByIntegerType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.INT;

        //when:
        Integer value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByLongType() {
        //given:
        final JsonElement element = new JsonPrimitive(10l);
        final JsonPrimitiveType type = JsonPrimitiveType.LONG;

        //when:
        Long value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.longValue(), 10l);
    }

    @Test
    public void testGetNullValueByLongType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.LONG;

        //when:
        Long value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByShortType() {
        //given:
        final JsonElement element = new JsonPrimitive(Short.valueOf((short) 1));
        final JsonPrimitiveType type = JsonPrimitiveType.SHORT;

        //when:
        Short value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value.shortValue(), (short) 1);
    }

    @Test
    public void testGetNullValueByShortType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.SHORT;

        //when:
        Short value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test
    public void testGetValueByStringType() {
        //given:
        final JsonElement element = new JsonPrimitive("Hello");
        final JsonPrimitiveType type = JsonPrimitiveType.STRING;

        //when:
        String value = type.getValueOfElement(element);

        //then:
        Assert.assertNotNull(value);
        Assert.assertEquals(value, "Hello");
    }

    @Test
    public void testGetNullValueByStringType() {
        //given:
        final JsonElement element = JsonNull.INSTANCE;
        final JsonPrimitiveType type = JsonPrimitiveType.STRING;

        //when:
        Short value = type.getValueOfElement(element);

        //then:
        Assert.assertNull(value);
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testGetValueByWrongType() {
        //given:
        final JsonElement element = new JsonPrimitive("Hello");
        final JsonPrimitiveType type = JsonPrimitiveType.INT;

        //when:
        Object value = type.getValueOfElement(element);
    }

    @Test
    public void testGetBigDecimalTypeByTypeString() {
        //given:
        final String typeDef = "BIG_DECIMAL";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BIG_DECIMAL);
    }

    @Test
    public void testGetBigIntegerTypeByTypeString() {
        //given:
        final String typeDef = "BIG_INTEGER";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BIG_INTEGER);
    }

    @Test
    public void testGetBooleanTypeByTypeString() {
        //given:
        final String typeDef = "BOOLEAN";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BOOLEAN);
    }

    @Test
    public void testGetByteTypeByTypeString() {
        //given:
        final String typeDef = "BYTE";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BYTE);
    }

    @Test
    public void testGetCharacterTypeByTypeString() {
        //given:
        final String typeDef = "CHARACTER";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.CHARACTER);
    }

    @Test
    public void testGetDoubleTypeByTypeString() {
        //given:
        final String typeDef = "DOUBLE";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.DOUBLE);
    }

    @Test
    public void testGetFloatTypeByTypeString() {
        //given:
        final String typeDef = "FLOAT";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.FLOAT);
    }

    @Test
    public void testGetIntegerTypeByTypeString() {
        //given:
        final String typeDef = "INT";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.INT);
    }

    @Test
    public void testGetLongTypeByTypeString() {
        //given:
        final String typeDef = "LONG";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.LONG);
    }

    @Test
    public void testGetShortTypeByTypeString() {
        //given:
        final String typeDef = "SHORT";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.SHORT);
    }

    @Test
    public void testGetStringTypeByTypeString() {
        //given:
        final String typeDef = "STRING";

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofType(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.STRING);
    }














    @Test
    public void testGetBigDecimalTypeByTypeClass() {
        //given:
        final Class<BigDecimal> typeDef = BigDecimal.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BIG_DECIMAL);
    }

    @Test
    public void testGetBigIntegerTypeByTypeClass() {
        //given:
        final Class<BigInteger> typeDef = BigInteger.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BIG_INTEGER);
    }

    @Test
    public void testGetBooleanTypeByTypeClass() {
        //given:
        final Class<Boolean> typeDef = Boolean.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BOOLEAN);
    }

    @Test
    public void testGetByteTypeByTypeClass() {
        //given:
        final Class<Byte> typeDef = Byte.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.BYTE);
    }

    @Test
    public void testGetCharacterTypeByTypeClass() {
        //given:
        final Class<Character> typeDef = Character.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.CHARACTER);
    }

    @Test
    public void testGetDoubleTypeByTypeClass() {
        //given:
        final Class<Double> typeDef = Double.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.DOUBLE);
    }

    @Test
    public void testGetFloatTypeByTypeClass() {
        //given:
        final Class<Float> typeDef = Float.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.FLOAT);
    }

    @Test
    public void testGetIntegerTypeByTypeClass() {
        //given:
        final Class<Integer> typeDef = Integer.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.INT);
    }

    @Test
    public void testGetLongTypeByTypeClass() {
        //given:
        final Class<Long> typeDef = Long.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.LONG);
    }

    @Test
    public void testGetShortTypeByTypeClass() {
        //given:
        final Class<Short> typeDef = Short.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.SHORT);
    }

    @Test
    public void testGetStringTypeByTypeClass() {
        //given:
        final Class<String> typeDef = String.class;

        //when:
        JsonPrimitiveType type = JsonPrimitiveType.ofTypeClass(typeDef);

        //then:
        Assert.assertNotNull(type);
        Assert.assertEquals(type, JsonPrimitiveType.STRING);
    }
}
