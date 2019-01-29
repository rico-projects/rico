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
package dev.rico.internal.server.validation.validator.impl;

import dev.rico.internal.server.validation.validator.RemoteValidationConfiguration;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gunnar Morling
 * @author Hendrik Ebbers
 *
 */
public class DefaultValidationConfiguration implements RemoteValidationConfiguration {

    private boolean addViolationMessageToResponse = true;

    private boolean addViolationMessageTemplateToResponse = false;

    private final Map<Class<? extends Annotation>, String> constraintAnnotationMapping = new HashMap<>();

    private final Map<String, Class<?>> typeMapping = new HashMap<>();

    public DefaultValidationConfiguration() {
    }

    //TODO: Rename
    public void setAddViolationMessageToResponse(final boolean addViolationMessageToResponse) {
        this.addViolationMessageToResponse = addViolationMessageToResponse;
    }

    //TODO: Rename
    public void setAddViolationMessageTemplateToResponse(final boolean addViolationMessageTemplateToResponse) {
        this.addViolationMessageTemplateToResponse = addViolationMessageTemplateToResponse;
    }

    @Override
    public boolean addViolationMessageToResponse() {
        return addViolationMessageToResponse;
    }

    @Override
    public boolean addViolationMessageTemplateToResponse() {
        return addViolationMessageTemplateToResponse;
    }

    public void addConstraintAnnotationMapping(final Class<? extends Annotation> cls, final String identifier) {
        constraintAnnotationMapping.put(cls, identifier);
    }

    @Override
    public Map<Class<? extends Annotation>, String> getConstraintAnnotationMapping() {
        return Collections.unmodifiableMap(constraintAnnotationMapping);
    }

    public void addTypeMapping(final String identifier, final Class<?> cls) {
        typeMapping.put(identifier, cls);
    }

    @Override
    public Map<String, Class<?>> getTypeMapping() {
        return Collections.unmodifiableMap(typeMapping);
    }
}
