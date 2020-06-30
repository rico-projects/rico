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
package dev.rico.internal.remoting;

import dev.rico.core.functional.Result;
import dev.rico.core.functional.ResultWithInput;
import dev.rico.internal.core.Assert;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ConverterFactory;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * The class {@link Converters} contains all {@link Converter} that are used in the remoting
 */
@API(since = "0.x", status = INTERNAL)
public class Converters {

    private static final Logger LOG = LoggerFactory.getLogger(Converters.class);

    private final List<ConverterFactory> converterFactories;

    public Converters(final BeanRepository beanRepository) {
        converterFactories = new ArrayList<>();

        final ServiceLoader<ConverterFactory> loader = ServiceLoader.load(ConverterFactory.class);
        loader.reload();
        final List<ResultWithInput<ConverterFactory, Void>> failed = loader.stream()
                .map(ServiceLoader.Provider::get)
                .peek(factory -> LOG.trace("Found converter factory {} with type identifier {}", factory.getClass(), factory.getTypeIdentifier()))
                .map(Result.ofConsumer(factory -> addFactory(beanRepository, factory)))
                .filter(ResultWithInput::isFailed)
                .collect(Collectors.toList());

        if (!failed.isEmpty()) {
            handleFailed(failed);
        }
    }

    private void addFactory(BeanRepository beanRepository, ConverterFactory factory) throws IdentifierTypeAlreadyAdded, ConversionTypeAlreadyAdded {
        checkIdentifierTypeNotAlreadyAdded(factory);
        checkConversionTypeNotAlreadyAdded(factory);

        factory.init(beanRepository);
        converterFactories.add(factory);
    }

    private void checkIdentifierTypeNotAlreadyAdded(final ConverterFactory converterFactory) throws IdentifierTypeAlreadyAdded {
        final int typeIdentifier = converterFactory.getTypeIdentifier();

        if (converterFactories.stream().anyMatch(factory -> factory.getTypeIdentifier() == typeIdentifier)) {
            throw new IdentifierTypeAlreadyAdded(typeIdentifier);
        }
    }

    private void checkConversionTypeNotAlreadyAdded(final ConverterFactory converterFactory) throws ConversionTypeAlreadyAdded {
        final Set<Class> typesToRegister = new HashSet<>(converterFactory.getSupportedTypes());

        final Set<Class> duplicate = converterFactories.stream()
                .map(ConverterFactory::getSupportedTypes)
                .flatMap(List::stream)
                .filter(typesToRegister::contains)
                .collect(Collectors.toSet());

        if (!duplicate.isEmpty()) {
            throw new ConversionTypeAlreadyAdded(typesToRegister);
        }
    }

    private void handleFailed(List<ResultWithInput<ConverterFactory, Void>> failed) {
        final Set<String> duplicatedIdentifier = new HashSet<>();
        final Set<String> duplicatedConversions = new HashSet<>();

        failed.forEach(r -> {
            final Exception e = r.getException();
            if (e instanceof IdentifierTypeAlreadyAdded) {
                duplicatedIdentifier.add(((IdentifierTypeAlreadyAdded) e).type);
            } else if (e instanceof ConversionTypeAlreadyAdded) {
                duplicatedConversions.addAll(((ConversionTypeAlreadyAdded) e).duplicatedTypes);
            } else {
                throw new RuntimeException("failed to register converter factory " + r.getInput(), e);
            }

            if (!duplicatedIdentifier.isEmpty()) {
                throw new IllegalStateException("type identifier(s) " + String.join(", ", duplicatedIdentifier) + " registered multiple times");
            } else {
                throw new IllegalStateException("conversion type(s)" + String.join(", ", duplicatedConversions) + " registered multiple times");
            }
        });
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

    private static class IdentifierTypeAlreadyAdded extends Exception {
        private final String type;

        IdentifierTypeAlreadyAdded(int type) {
            this.type = Integer.toString(type);
        }
    }

    private static class ConversionTypeAlreadyAdded extends Exception {
        private final Set<String> duplicatedTypes;

        public ConversionTypeAlreadyAdded(Collection<Class> duplicatedTypes) {
            this.duplicatedTypes = duplicatedTypes.stream().map(Class::getName).collect(Collectors.toSet());
        }
    }
}
