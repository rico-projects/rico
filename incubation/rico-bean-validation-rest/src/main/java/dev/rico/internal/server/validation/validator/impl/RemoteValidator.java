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
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package dev.rico.internal.server.validation.validator.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.validation.validator.RemoteValidationConfiguration;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;

/**
 * @author Gunnar Morling
 * @author Hendrik Ebbers
 */
public class RemoteValidator {

    private final Validator validator;
    private final ClassLoader classLoader;
    private final RemoteValidationConfiguration configuration;

    public RemoteValidator(final RemoteValidationConfiguration configuration, final Validator validator, final ClassLoader classLoader) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.validator = Assert.requireNonNull(validator, "validator");
        this.classLoader = Assert.requireNonNull(classLoader, "classLoader");
    }

    public Set<ConstraintViolation<?>> validateValue(final String typeName, final String property, final Object value) {
        Assert.requireNonNull(typeName, "typeName");
        Assert.requireNonNull(property, "property");
        return validator.validateValue((Class)getClassForType(typeName), property, value);
    }

    private Class<?> getClassForType(final String typeName) {
        Assert.requireNonNull(typeName, "typeName");
        final Map<String, Class<?>> mapping = configuration.getTypeMapping();
        if(mapping.containsKey(typeName)) {
            final Class<?> cls = mapping.get(typeName);
            if(cls == null) {
                throw new IllegalStateException("null value specified for type " + typeName);
            }
            return cls;
        }
        try {
            return classLoader.loadClass(typeName);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Can not find class for type " + typeName, e);
        }
    }
}
