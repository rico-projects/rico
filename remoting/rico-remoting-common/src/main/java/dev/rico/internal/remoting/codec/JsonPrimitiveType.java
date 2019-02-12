package dev.rico.internal.remoting.codec;

import com.google.gson.JsonElement;
import dev.rico.internal.core.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

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

    public <T> T getValueOfElement(final JsonElement element) {
        Assert.requireNonNull(element, "element");

        if(element.isJsonNull()) {
            return null;
        }

        if(Objects.equals(this, BIG_DECIMAL)) {
            return (T) element.getAsBigDecimal();
        }
        if(Objects.equals(this, BIG_INTEGER)) {
            return (T) element.getAsBigInteger();
        }
        if(Objects.equals(this, BOOLEAN)) {
            return (T) new Boolean(element.getAsBoolean());
        }
        if(Objects.equals(this, BYTE)) {
            return (T) new Byte(element.getAsByte());
        }
        if(Objects.equals(this, CHARACTER)) {
            return (T) new Character(element.getAsCharacter());
        }
        if(Objects.equals(this, DOUBLE)) {
            return (T) new Double(element.getAsDouble());
        }
        if(Objects.equals(this, FLOAT)) {
            return (T) new Float(element.getAsFloat());
        }
        if(Objects.equals(this, INT)) {
            return (T) new Integer(element.getAsInt());
        }
        if(Objects.equals(this, LONG)) {
            return (T) new Long(element.getAsLong());
        }
        if(Objects.equals(this, SHORT)) {
            return (T) new Short(element.getAsShort());
        }
        if(Objects.equals(this, STRING)) {
            return (T) element.getAsString();
        }
        throw new IllegalStateException("Type can not be defined!");
    }

    public static JsonPrimitiveType ofType(final String type) {
        Assert.requireNonBlank(type, "type");
        return Arrays.asList(values()).stream()
                .filter(v -> Objects.equals(v.getType(), type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not find type '" + type + "'"));
    }

    public static JsonPrimitiveType ofTypeClass(final Class typeClass) {
        Assert.requireNonNull(typeClass, "typeClass");
        return Arrays.asList(values()).stream()
                .filter(v -> Objects.equals(v.getTypeClass(), typeClass))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not find type '" + typeClass + "'"));
    }
}
