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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.rico.internal.remoting.converters;

import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class BigDecimalConverterFactory extends AbstractConverterFactory {
    
    public final static int FIELD_TYPE_BIGDECIMAL = 12;
    
    private final static Converter CONVERTER = new AbstractNumberConverter<BigDecimal> () {
        @Override
        public BigDecimal convertFromRemoting(final Number value) throws ValueConverterException {
            try {
                return value == null ? null : new BigDecimal(value.toString());
            } catch (final Exception e) {
                throw new ValueConverterException("Unable to parse the number: " + value, e);
            }
        }

        @Override
        public Number convertToRemoting(final BigDecimal value) throws ValueConverterException {
            return value;
        }
    };

    @Override
    public boolean supportsType(final Class<?> cls) {
        return BigDecimal.class.equals(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(BigDecimal.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_BIGDECIMAL;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }
    
}
