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
package dev.rico.internal.remoting.communication.converters;

import dev.rico.internal.remoting.repo.BeanRepository;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ConverterFactory;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * The class {@code Converters} contains all {@link Converter} that are used in the remoting
 */
@API(since = "0.x", status = INTERNAL)
public class Converters {

    private static final Logger LOG = LoggerFactory.getLogger(Converters.class);

    private final List<ConverterFactory> converterFactories;

    public Converters(final BeanRepository beanRepository) {
        converterFactories = new ArrayList<>();
        ServiceLoader<ConverterFactory> loader = ServiceLoader.load(ConverterFactory.class);
        loader.reload();
        Iterator<ConverterFactory> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ConverterFactory factory = iterator.next();
            LOG.trace("Found converter factory {} with type identifier {}", factory.getClass(), factory.getTypeIdentifier());
            if(!isTypeAlreadyAdded(factory) && !isAnyConversionTypeAlreadyAdded(factory)) {
                factory.init(beanRepository);
                converterFactories.add(factory);
            }else{
                throw new RuntimeException("Converter of type(s) "+ Arrays.toString(factory.getSupportedTypes().toArray()) + " already added to the factory.");
            }
        }
    }

    private boolean isTypeAlreadyAdded(final ConverterFactory converterFactory) {
        Assert.requireNonNull(converterFactory, "converterFactory");
        for(ConverterFactory factory : converterFactories){
            if(factory.getTypeIdentifier() == converterFactory.getTypeIdentifier()){
                return true;
            }
        }
        return false;
    }


    private boolean isAnyConversionTypeAlreadyAdded(final ConverterFactory converterFactory) {
        Assert.requireNonNull(converterFactory, "converterFactory");
        for(ConverterFactory factory : converterFactories){
            return factory.getSupportedTypes().stream().filter(type -> converterFactory.getSupportedTypes().contains(type)).findAny().map(c -> true).orElse(false);
        }
        return false;
    }

    public int getFieldType(final Class<?> clazz) {
        return getFactory(clazz).getTypeIdentifier();
    }

    public Converter getConverter(final Class<?> clazz) {
        return getFactory(clazz).getConverterForType(clazz);
    }

    private ConverterFactory getFactory(final Class<?> clazz) {
        Assert.requireNonNull(clazz, "clazz");
        List<ConverterFactory> foundConverters = new ArrayList<>();
        for (ConverterFactory factory : converterFactories) {
            if (factory.supportsType(clazz)) {
                foundConverters.add(factory);
            }
        }
        if (foundConverters.size() > 1) {
            throw new RuntimeException("More than 1 converter instance found to convert " + clazz);
        }
        if (foundConverters.isEmpty()) {
            throw new RuntimeException("No converter instance found to convert " + clazz);
        }
        return foundConverters.get(0);
    }
}
