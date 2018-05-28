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
package dev.rico.internal.server.validation.validator.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.validation.RemoteConstants;
import dev.rico.internal.server.validation.validator.RemoteValidationConfiguration;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import javax.validation.ConstraintViolation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Gunnar Morling
 * @author Hendrik Ebbers
 *
 */
public class ConstraintsViolationSerializer implements JsonSerializer<ConstraintViolation<?>> {

    private final RemoteValidationConfiguration configuration;

    public ConstraintsViolationSerializer(final RemoteValidationConfiguration configuration) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    @Override
    public JsonElement serialize(final ConstraintViolation<?> constraintViolation, final Type type, final JsonSerializationContext jsonSerializationContext) {
        Assert.requireNonNull(constraintViolation, "constraintViolation");
        final JsonObject root = new JsonObject();
        root.addProperty(RemoteConstants.TYPE_PROPERTY_NAME, getTypeName(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType()));
        if(configuration.addViolationMessageToResponse()) {
            root.addProperty(RemoteConstants.MESSAGE_PROPERTY_NAME, constraintViolation.getMessage());
        }
        if(configuration.addViolationMessageTemplateToResponse()) {
            root.addProperty(RemoteConstants.TEMPLATE_PROPERTY_NAME, constraintViolation.getMessageTemplate());
        }
        return root;
    }

    private String getTypeName(final Class<? extends Annotation> annotationClass) {
        Assert.requireNonNull(annotationClass, "annotationClass");
        Map<Class<? extends Annotation>, String> mapping =  configuration.getConstraintAnnotationMapping();
        Assert.requireNonNull(mapping, "mapping");
        if(mapping.containsKey(annotationClass)) {
            return mapping.get(annotationClass);
        }
        return annotationClass.getSimpleName();
    }
}
