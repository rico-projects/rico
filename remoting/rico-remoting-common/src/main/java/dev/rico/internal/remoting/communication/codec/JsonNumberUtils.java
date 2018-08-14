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
package dev.rico.internal.remoting.communication.codec;


import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Some helper classes since GSON uses {@link com.google.gson.internal.LazilyParsedNumber} internally.
 */
@API(since = "0.x", status = INTERNAL)
public class JsonNumberUtils {

    public static Number convert(final Class<?> neededType, final Object value) {
        Assert.requireNonNull(neededType, "neededType");
        if(!ReflectionHelper.isNumber(neededType)) {
            throw new IllegalArgumentException("given type is not a number type: " + neededType.getSimpleName());
        }
        if(value == null && ReflectionHelper.isPrimitiveNumber(neededType)) {
            throw new IllegalArgumentException("null can not be converted for a primitive type");
        }

        if(value == null) {
            return null;
        }

        if(!Number.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Given value is not a number! Type " + value.getClass().getSimpleName());
        }
        final Number numberValue = (Number) value;
        if (neededType.equals(Integer.class) || neededType.equals(Integer.TYPE)) {
            return numberValue.intValue();
        } else if (neededType.equals(Long.class) || neededType.equals(Long.TYPE)) {
            return numberValue.longValue();
        } else if (neededType.equals(Double.class) || neededType.equals(Double.TYPE)) {
            return numberValue.doubleValue();
        } else if (neededType.equals(Float.class) || neededType.equals(Float.TYPE)) {
            return numberValue.floatValue();
        } else if (neededType.equals(Byte.class) || neededType.equals(Byte.TYPE)) {
            return numberValue.byteValue();
        } else if (neededType.equals(Short.class) || neededType.equals(Short.TYPE)) {
            return numberValue.shortValue();
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + neededType);
        }
    }


}
