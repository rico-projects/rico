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
package dev.rico.internal.remoting.codec;

import com.google.gson.JsonElement;
import dev.rico.internal.core.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * Enum that defines all JSON types and its Java mapping
 */
public enum JsonPrimitiveType {

    BIG_DECIMAL("BIG_DECIMAL", BigDecimal.class),
    BIG_INTEGER("BIG_INTEGER", BigInteger.class),
    BOOLEAN("BOOLEAN", Boolean.class),
    BYTE("BYTE", Byte.class),
    CHARACTER("CHARACTER", Character.class),
    DOUBLE("DOUBLE", Double.class),
    FLOAT("FLOAT", Float.class),
    INT("INT", Integer.class),
    LONG("LONG", Long.class),
    SHORT("SHORT", Short.class),
    STRING("STRING", String.class);

    private final String type;

    private final Class typeClass;

    JsonPrimitiveType(final String type, final Class typeClass) {
        this.type = Assert.requireNonBlank(type, "type");
        this.typeClass = Assert.requireNonNull(typeClass, "typeClass");
    }

    public Class getTypeClass() {
        return typeClass;
    }

    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueOfElement(final JsonElement element) {
        Assert.requireNonNull(element, "element");

        if (element.isJsonNull()) {
            return null;
        }

        if (this == BIG_DECIMAL) {
            return (T) element.getAsBigDecimal();
        }
        if (this == BIG_INTEGER) {
            return (T) element.getAsBigInteger();
        }
        if (this == BOOLEAN) {
            return (T) Boolean.valueOf(element.getAsBoolean());
        }
        if (this == BYTE) {
            return (T) Byte.valueOf(element.getAsByte());
        }
        if (this == CHARACTER) {
            return (T) Character.valueOf(element.getAsCharacter());
        }
        if (this == DOUBLE) {
            return (T) Double.valueOf(element.getAsDouble());
        }
        if (this == FLOAT) {
            return (T) Float.valueOf(element.getAsFloat());
        }
        if (this == INT) {
            return (T) Integer.valueOf(element.getAsInt());
        }
        if (this == LONG) {
            return (T) Long.valueOf(element.getAsLong());
        }
        if (this == SHORT) {
            return (T) Short.valueOf(element.getAsShort());
        }
        if (this == STRING) {
            return (T) element.getAsString();
        }
        throw new IllegalStateException("Type can not be defined!");
    }

    public static JsonPrimitiveType ofType(final String type) {
        Assert.requireNonBlank(type, "type");
        return Arrays.stream(values())
                .filter(v -> Objects.equals(v.getType(), type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not find type '" + type + "'"));
    }

    public static JsonPrimitiveType ofTypeClass(final Class typeClass) {
        Assert.requireNonNull(typeClass, "typeClass");
        return Arrays.stream(values())
                .filter(v -> Objects.equals(v.getTypeClass(), typeClass))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not find type '" + typeClass + "'"));
    }
}
