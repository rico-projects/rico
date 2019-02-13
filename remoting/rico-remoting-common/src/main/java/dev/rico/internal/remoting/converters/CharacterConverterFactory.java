package dev.rico.internal.remoting.converters;

import dev.rico.remoting.converter.Converter;

import java.util.Arrays;
import java.util.List;

public class CharacterConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_CHARACTER_BYTE = 107;

    private final static Converter<Character, Number> CONVERTER = new AbstractNumberConverter<Character>() {

        @Override
        public Character convertFromRemoting(final Number value) {
            return value == null ? null : (char) value.intValue();
        }

        @Override
        public Number convertToRemoting(final Character value) {
            return value == null ? null : Integer.valueOf(value.charValue());
        }
    };


    @Override
    public boolean supportsType(final Class<?> cls) {
        return char.class.equals(cls) || Character.class.equals(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Arrays.asList(char.class, Character.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_CHARACTER_BYTE;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }
}
