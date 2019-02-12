package dev.rico.internal.remoting.codec;

import com.google.gson.JsonPrimitive;
import dev.rico.internal.core.Assert;

import java.util.Arrays;
import java.util.Objects;

public enum JsonPrimitiveTypes {

    BIG_DECIMAL("BIG_DECIMAL"),
    BIG_INTEGER("BIG_INTEGER"),
    BOOLEAN("BOOLEAN"),
    BYTE("BYTE"),
    CHARACTER("CHARACTER"),
    DOUBLE("DOUBLE"),
    FLOAT("FLOAT"),
    INT("INT"),
    LONG("LONG"),
    SHORT("SHORT"),
    STRING("STRING");

    private final String type;

    JsonPrimitiveTypes(final String type) {
        this.type = Assert.requireNonBlank(type, "type");
    }

    public String getType() {
        return type;
    }

    public <T> T getValueOfElement(final JsonPrimitive element) {
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

    public static JsonPrimitiveTypes ofType(final String type) {
        Assert.requireNonBlank(type, "type");
        return Arrays.asList(values()).stream()
                .filter(v -> Objects.equals(v.getType(), type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not find type '" + type + "'"));
    }
}
